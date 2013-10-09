/*
 * Copyright 2007-2013 The Europeana Foundation
 *
 *  Licenced under the EUPL, Version 1.1 (the "Licence") and subsequent versions as approved 
 *  by the European Commission;
 *  You may not use this work except in compliance with the Licence.
 *  
 *  You may obtain a copy of the Licence at:
 *  http://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under 
 *  the Licence is distributed on an "AS IS" basis, without warranties or conditions of 
 *  any kind, either express or implied.
 *  See the Licence for the specific language governing permissions and limitations under 
 *  the Licence.
 */

package eu.europeana.portal2.web.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.response.FacetField;

import eu.europeana.corelib.definitions.model.web.BreadCrumb;
import eu.europeana.corelib.definitions.solr.beans.BriefBean;
import eu.europeana.corelib.definitions.solr.model.Query;
import eu.europeana.corelib.solr.exceptions.SolrTypeException;
import eu.europeana.corelib.solr.model.ResultSet;
import eu.europeana.corelib.solr.service.SearchService;
import eu.europeana.corelib.web.model.rights.RightReusabilityCategorizer;
import eu.europeana.corelib.web.utils.NavigationUtils;
import eu.europeana.corelib.web.utils.UrlBuilder;
import eu.europeana.portal2.web.model.ModelUtils;
import eu.europeana.portal2.web.presentation.SearchPageEnum;
import eu.europeana.portal2.web.presentation.model.submodel.BriefBeanView;
import eu.europeana.portal2.web.presentation.model.submodel.ResultPagination;
import eu.europeana.portal2.web.presentation.model.submodel.impl.BriefBeanViewImpl;
import eu.europeana.portal2.web.presentation.model.submodel.impl.ResultPaginationImpl;

public class SearchUtils {

	private static final Pattern ID_PATTERN = Pattern.compile("^\\{!id=([^:]+):([^:]+)\\}");

	private static Logger log = Logger.getLogger(SearchUtils.class.getCanonicalName());

	public static BriefBeanView createResults(SearchService searchService,
			Class<? extends BriefBean> clazz, String profile, Query query,
			int start, int rows,
			Map<String, String[]> params, Query usabilityQuery) 
					throws SolrTypeException {

		BriefBeanViewImpl briefBeanView = new BriefBeanViewImpl();
		ResultSet<? extends BriefBean> resultSet = searchService.search(clazz, query);

		List<FacetField> facetFields = resultSet.getFacetFields();
		if (usabilityQuery != null) {
			Map<String, Integer> usability = searchService.queryFacetSearch(
				usabilityQuery.getQuery(),
				usabilityQuery.getRefinements(true),
				usabilityQuery.getFacetQueries()
			);
			List<FacetField> allQueryFacetsMap = extractQueryFacets(usability);
			if (allQueryFacetsMap != null && !allQueryFacetsMap.isEmpty()) {
				facetFields.addAll(allQueryFacetsMap);
			}
		}

		briefBeanView.setBriefBeans(resultSet.getResults());

		if (StringUtils.containsIgnoreCase(profile, "facets") || StringUtils.containsIgnoreCase(profile, "portal")) {
			briefBeanView.makeQueryLinks(ModelUtils.conventFacetList(facetFields), query);
		}

		if (StringUtils.containsIgnoreCase(profile, "filters") || StringUtils.containsIgnoreCase(profile, "portal")) {
			briefBeanView.makeFilters(query, params);
		}

		List<BreadCrumb> breadCrumbs = null;
		if (StringUtils.containsIgnoreCase(profile, "breadcrumb") || StringUtils.containsIgnoreCase(profile, "portal")) {
			breadCrumbs = NavigationUtils.createBreadCrumbList(QueryUtil.escapeQuery(query));
		}

		if (StringUtils.containsIgnoreCase(profile, "spelling") || StringUtils.containsIgnoreCase(profile, "portal")) {
			briefBeanView.setSpellcheck(ModelUtils.convertSpellCheck(resultSet.getSpellcheck()));
		}

		briefBeanView.setFacetLogs(resultSet.getFacetFields());

//		if (StringUtils.containsIgnoreCase(profile, "suggestions") || StringUtils.containsIgnoreCase(profile, "portal")) {
//		}

		ResultPagination pagination = new ResultPaginationImpl(start, rows, (int)resultSet.getResultSize(), query, breadCrumbs);
		briefBeanView.setPagination(pagination);
		return briefBeanView;
	}

	/**
	 * The QueryFacets are in this form:
	 * {!id=REUSABILITY:Limited}RIGHTS:(http\:\/\/creativecommons.org\/licenses\/by-nc\/*
	 * OR http\:\/\/creativecommons.org\/licenses\/by-nc-sa\/* OR http\:\/\/creativecommons.org\/licenses\/by-nc-nd\/* 
	 * OR http\:\/\/creativecommons.org\/licenses\/by-nd\/*
	 * OR http\:\/\/www.europeana.eu\/rights\/out-of-copyright-non-commercial\/*)
	 * 
	 * this function creates a hierarchy:
	 * REUSABILITY
	 *   Limited: x
	 *   Free: y
	 * ...
	 * 
	 * @param queryFacets
	 * @return
	 */
	public static List<FacetField> extractQueryFacets(Map<String, Integer> queryFacets) {
		Map<String, FacetField> map = new HashMap<String, FacetField>();
		for (String query : queryFacets.keySet()) {
			Matcher matcher = ID_PATTERN.matcher(query);
			if (!matcher.find()) {
				continue;
			}
			String field = matcher.group(1);
			String value = matcher.group(2);
			if (!map.containsKey(field)) {
				map.put(field, new FacetField(field));
			}
			map.get(field).add(value, queryFacets.get(query));
		}
		List<FacetField> list = new ArrayList<FacetField>(map.values());
		return list;
	}

	public static UrlBuilder createSearchUrl(String portalname, SearchPageEnum returnTo, 
			String searchTerm, String[] qf, String start)
					throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		sb.append("/").append(portalname).append("/").append(returnTo.getPageInfo().getPageName());
		UrlBuilder url = new UrlBuilder(sb.toString());
		if (searchTerm != null) {
			url.addParam("query", URLEncoder.encode(searchTerm, "UTF-8"), true);
		}
		if (qf != null) {
			url.addParam("qf", qf, true);
		}
		url.addParam("start", start, true);

		return url;
	}

	public static Query createUsabilityQuery(String q, String[] qf) {
		List<String> refinements = new ArrayList<String>();
		for (String value : qf) {
			if (!value.equals("REUSABILITY:Free") && !value.equals("REUSABILITY:Limited")) {
				refinements.add(value);
			}
		}
		String[] filteredQf = refinements.toArray(new String[refinements.size()]);
		Query query = new Query(q)
					.setRefinements(filteredQf)
					.setPageSize(0)
					.setProduceFacetUnion(true)
		;

		Map<String, String> replMap = RightReusabilityCategorizer.getQueryFacets();
		if (replMap != null) {
			query.setFacetQueries(replMap);
		}
		return query;
	}

	public static Map<String, String> mapValueReplacements(String[] qf) {
		if (ArrayUtils.isEmpty(qf)) {
			return null;
		}
		Map<String, String> valueReplacements = new HashMap<String, String>();
		int reusabilityFilters = 0;
		for (String value : qf) {
			if (value.equals("REUSABILITY:Free")) {
				valueReplacements.put(value, RightReusabilityCategorizer.getFreeRightsQuery());
				reusabilityFilters++;
			} else if (value.equals("REUSABILITY:Limited")) {
				valueReplacements.put(value, RightReusabilityCategorizer.getLimitedRightsQuery());
				reusabilityFilters++;
			}
		}
		if (reusabilityFilters == 2) {
			valueReplacements.put("REUSABILITY:Free", RightReusabilityCategorizer.getAllRightsQuery());
			valueReplacements.put("REUSABILITY:Limited", "");
		}
		return valueReplacements;
	}

}
