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

package eu.europeana.portal2.web.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import eu.europeana.corelib.definitions.exception.ProblemType;
import eu.europeana.corelib.definitions.solr.beans.BriefBean;
import eu.europeana.corelib.definitions.solr.beans.FullBean;
import eu.europeana.corelib.definitions.solr.model.Query;
import eu.europeana.corelib.logging.Log;
import eu.europeana.corelib.logging.Logger;
import eu.europeana.corelib.solr.bean.impl.FullBeanImpl;
import eu.europeana.corelib.solr.exceptions.EuropeanaQueryException;
import eu.europeana.corelib.solr.exceptions.MongoDBException;
import eu.europeana.corelib.solr.exceptions.SolrTypeException;
import eu.europeana.corelib.solr.model.ResultSet;
import eu.europeana.corelib.solr.service.SearchService;
import eu.europeana.corelib.solr.utils.SolrUtils;
import eu.europeana.corelib.utils.EuropeanaUriUtils;
import eu.europeana.corelib.utils.StringArrayUtils;
import eu.europeana.corelib.utils.service.MltStopwordsService;
import eu.europeana.corelib.utils.service.OptOutService;
import eu.europeana.corelib.web.model.rights.RightReusabilityCategorizer;
import eu.europeana.corelib.web.support.Configuration;
import eu.europeana.corelib.web.utils.RequestUtils;
import eu.europeana.portal2.services.ClickStreamLogService;
import eu.europeana.portal2.services.ClickStreamLogService.UserAction;
import eu.europeana.portal2.web.model.mlt.EuropeanaMlt;
import eu.europeana.portal2.web.model.mlt.EuropeanaMltCategory;
import eu.europeana.portal2.web.model.mlt.EuropeanaMltLink;
import eu.europeana.portal2.web.model.mlt.MltCollector;
import eu.europeana.portal2.web.model.mlt.MltSuggestion;
import eu.europeana.portal2.web.model.seealso.SeeAlsoCollector;
import eu.europeana.portal2.web.model.seealso.SeeAlsoSuggestion;
import eu.europeana.portal2.web.model.seealso.SeeAlsoSuggestions;
import eu.europeana.portal2.web.presentation.PortalPageInfo;
import eu.europeana.portal2.web.presentation.SearchPageEnum;
import eu.europeana.portal2.web.presentation.model.FullDocPage;
import eu.europeana.portal2.web.presentation.model.abstracts.UrlAwareData;
import eu.europeana.portal2.web.presentation.model.data.decorators.BriefBeanDecorator;
import eu.europeana.portal2.web.presentation.model.submodel.FullBeanView;
import eu.europeana.portal2.web.presentation.model.submodel.impl.FullBeanViewImpl;
import eu.europeana.portal2.web.presentation.semantic.SchemaOrgMapping;
import eu.europeana.portal2.web.util.ControllerUtil;
import eu.europeana.portal2.web.util.FullBeanShortcut;

/**
 * @author Willem-Jan Boogerd <www.eledge.net/contact>
 */
@Controller
public class ObjectController {

	@Log
	private Logger log;

	@Resource
	private SearchService searchService;

	@Resource
	private Configuration config;

	@Resource
	private ClickStreamLogService clickStreamLogger;

	@Resource
	private OptOutService optOutService;
	
	@Resource
	private SchemaOrgMapping schemaOrgMapping;

	@Resource
	private MltStopwordsService mltStopwordsService;

	public static final String V1_PATH = "/v1/record/";
	public static final String SRW_EXT = ".srw";
	public static final String JSON_EXT = ".json";
	public static final int MAX_COUNT_PER_FIELD = 20;

	@Resource
	private ReloadableResourceBundleMessageSource messageSource;

	public static final Map<String, List<String>> SEE_ALSO_FIELDS = new LinkedHashMap<String, List<String>>() {
		private static final long serialVersionUID = 1L;
		{
			put("title", Arrays.asList(new String[] { "DcTitle", "DctermsAlternative" }));
			put("who", Arrays.asList(new String[] { "DcContributor", "DcCreator" }));
			put("what", Arrays.asList(new String[] { "DcType", "DcSubject", "DcFormat" }));
			// put("when", Arrays.asList(new String[]{"DcCoverage", "DcDate", "DcSubject", "DctermsCreated",
			// "DctermsTemporal"}));
			// put("where", Arrays.asList(new String[]{"DcCoverage", "DcSubject", "DctermsSpatial"}));
			put("DATA_PROVIDER", Arrays.asList(new String[] { "DataProvider" }));
			put("PROVIDER", Arrays.asList(new String[] { "EdmProvider" }));
		}
	};

	@RequestMapping(value = "/record/{collectionId}/{recordId}.html", produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView record(
			@PathVariable String collectionId, 
			@PathVariable String recordId,
			@RequestParam(value = "format", required = false) String format,
			@RequestParam(value = "embedded", required = false) String embedded,
			@RequestParam(value = "query", required = false) String queryString,
			@RequestParam(value = "qf", required = false) String[] qf,
			@RequestParam(value = "start", required = false, defaultValue = "1") int start,
			@RequestParam(value = "returnTo", required = false, defaultValue = "SEARCH_HTML") SearchPageEnum returnTo,
			@RequestParam(value = "rows", required = false, defaultValue = "24") int rows,
			@RequestParam(value = "mlt", required = false, defaultValue = "false") String mlt,
			HttpServletRequest request, 
			HttpServletResponse response, 
			Locale locale) throws EuropeanaQueryException {

		long t0 = (new Date()).getTime();
		// workaround of a Spring issue (https://jira.springsource.org/browse/SPR-7963)
		String[] _qf = (String[]) request.getParameterMap().get("qf");
		if (_qf != null && _qf.length != qf.length) {
			qf = _qf;
		}

		boolean showEuropeanaMlt = false;
		if (StringUtils.isNotBlank(mlt) && Boolean.parseBoolean(mlt)) {
			showEuropeanaMlt = true;
		}

		FullDocPage model = new FullDocPage();
		model.setCollectionId(collectionId);
		model.setRecordId(recordId);
		model.setFormat(format);
		model.setEmbedded(StringUtils.equalsIgnoreCase(embedded, "true"));
		if (!StringUtils.isBlank(queryString)) {
			queryString = SolrUtils.translateQuery(queryString);
		}
		model.setQuery(queryString);
		model.setRefinements(qf);
		model.setStart(start);
		model.setReturnTo(returnTo);
		model.setRows(rows);
		model.setShowEuropeanaMlt(showEuropeanaMlt);
		model.setSoundCloudAwareCollections(config.getSoundCloudAwareCollections());

		// TODO: refactor this!!!
		boolean showSimilarItems = false;
		try {
			String sShowSimilarItems = StringUtils.replace(
					messageSource.getMessage("notranslate_show_similar_items_t", null, locale), ";", "");
			showSimilarItems = Boolean.parseBoolean(sShowSimilarItems.trim());
		} catch (NoSuchMessageException e) {
			e.printStackTrace();
		}
		model.setShowSimilarItems(showSimilarItems);

		model.setShownAtProviderOverride(config.getShownAtProviderOverride());
		model.setEdmSchemaMappings(schemaOrgMapping);

		long tgetFullBean0 = (new Date()).getTime();
		FullBean fullBean = getFullBean(collectionId, recordId, showSimilarItems);
		if (fullBean == null) {
			String newRecordId = resolveNewRecordId(collectionId, recordId);
			if (StringUtils.isNotBlank(newRecordId)) {
				StringBuilder location = new StringBuilder();
				if (!config.getPortalName().startsWith("/")) {
					location.append("/");
				}
				location.append(config.getPortalName()).append("/record").append(newRecordId).append(".html");
				response.setStatus(301);
				response.setHeader("Location", location.toString());
				return null;
			} else {
				throw new EuropeanaQueryException(ProblemType.RECORD_NOT_FOUND);
			}
		}

		if (log.isDebugEnabled()) {
			long tgetFullBean1 = (new Date()).getTime();
			log.debug("fullBean takes: " + (tgetFullBean1 - tgetFullBean0));
		}
		ModelAndView page = ControllerUtil.createModelAndViewPage(model, locale, PortalPageInfo.FULLDOC_HTML);
		if (fullBean != null) {
			model.setOptedOut(optOutService.check(fullBean.getAbout()));
			Query query = new Query(queryString)
				.setRefinements(qf)
				.setValueReplacements(RightReusabilityCategorizer.mapValueReplacements(qf))
				.setAllowFacets(false)
				.setAllowSpellcheck(false);

			// full bean view
			FullBeanView fullBeanView = new FullBeanViewImpl(fullBean, RequestUtils.getParameterMap(request), query, searchService);
			model.setFullBeanView(fullBeanView);

			// more like this
			if (model.isShowSimilarItems()) {
				List<? extends BriefBean> similarItems;
				if (fullBean.getSimilarItems() == null) {
					similarItems = getMoreLikeThis(collectionId, recordId);
				} else {
					similarItems = fullBean.getSimilarItems();
				}
				model.setMoreLikeThis(prepareMoreLikeThis(similarItems, model));
			}

			long tSeeAlso0 = 0;
			if (log.isDebugEnabled()) {
				tSeeAlso0 = (new Date()).getTime();
			}
			model.setSeeAlsoCollector(createSeeAlsoCollector(fullBean));
			model.setSeeAlsoSuggestions(createSeeAlsoSuggestions(model.getSeeAlsoCollector()));
			if (showEuropeanaMlt) {
				model.setMltCollector(createMltCollector(fullBean));
				model.setEuropeanaMlt(createEuropeanaMlt(model.getMltCollector(), fullBean.getAbout()));
			}
			if (log.isDebugEnabled()) {
				long tSeeAlso1 = (new Date()).getTime();
				log.debug("see also takes: " + (tSeeAlso1 - tSeeAlso0));
			}
			clickStreamLogger.logFullResultView(request, UserAction.FULL_RESULT_HMTL, fullBeanView, page, fullBeanView
					.getFullDoc().getAbout());
		}

		if (log.isDebugEnabled()) {
			long t1 = (new Date()).getTime();
			log.debug("object page takes: " + (t1 - t0));
		}

		return page;
	}

	/**
	 * Redirects to API's record JSON call
	 * 
	 * @param collectionId
	 * @param recordId
	 * @param wskey
	 * @param callback
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/record/{collectionId}/{recordId}.json", produces = MediaType.TEXT_HTML_VALUE)
	public String redirectJson(
			@PathVariable String collectionId, 
			@PathVariable String recordId,
			HttpServletRequest request) throws Exception {
		StringBuilder sb = new StringBuilder(config.getApi2url());
		sb.append(V1_PATH).append(collectionId).append("/").append(recordId).append(JSON_EXT);
		if (!StringUtils.isBlank(request.getQueryString())) {
			sb.append("?").append(request.getQueryString());
		}
		return "redirect:" + sb.toString();
	}

	/**
	 * Redirects to API's record SRW call
	 * 
	 * @param collectionId
	 * @param recordId
	 * @param wskey
	 * @param callback
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/record/{collectionId}/{recordId}.srw", produces = MediaType.TEXT_HTML_VALUE)
	public String redirectSrw(
			@PathVariable String collectionId, 
			@PathVariable String recordId,
			HttpServletRequest request)	throws Exception {
		StringBuilder sb = new StringBuilder(config.getApi2url());
		sb.append(V1_PATH).append(collectionId).append("/").append(recordId).append(SRW_EXT);
		if (!StringUtils.isBlank(request.getQueryString())) {
			sb.append("?").append(request.getQueryString());
		}
		return "redirect:" + sb.toString();
	}

	/**
	 * Get FullBean through corelib calls
	 * 
	 * @param collectionId
	 * @param recordId
	 * @return
	 */
	private FullBean getFullBean(String collectionId, String recordId, boolean showSimilarItems) {
		FullBean fullBean = null;
		String europeanaId = EuropeanaUriUtils.createResolveEuropeanaId(collectionId, recordId);
		try {
			fullBean = searchService.findById(europeanaId, showSimilarItems);
		} catch (MongoDBException e) {
			System.out.println("here should be a log.error");
			log.error(String.format("MongoDB Exception during getting the full bean for ID %s: %s", europeanaId,
					e.getMessage()));
		} catch (NullPointerException e) {
			System.out.println("here should be a log.error");
			log.error(String.format("Exception during getting the full bean for ID %s: %s", europeanaId,
					e.getStackTrace()[0]));
		}
		return fullBean;
	}

	private String resolveNewRecordId(String collectionId, String recordId) {
		String newRecordId = null;
		String europeanaId = EuropeanaUriUtils.createResolveEuropeanaId(collectionId, recordId);
		try {
			newRecordId = searchService.resolveId(europeanaId);
		} catch (NullPointerException e) {
			System.out.println("here should be a log.error");
			log.error(String.format("Exception during getting the full bean for ID %s: %s", europeanaId,
					e.getStackTrace()[0]));
		}
		return newRecordId;
	}

	private List<BriefBean> getMoreLikeThis(String collectionId, String recordId) {
		String europeanaId = EuropeanaUriUtils.createResolveEuropeanaId(collectionId, recordId);
		List<BriefBean> result = null;
		try {
			result = searchService.findMoreLikeThis(europeanaId);
		} catch (SolrServerException e) {
			log.error("Solr Server Exception: " + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	private List<BriefBeanDecorator> prepareMoreLikeThis(List<? extends BriefBean> result, UrlAwareData<?> model) {
		List<BriefBeanDecorator> moreLikeThis = new ArrayList<BriefBeanDecorator>();
		if (result != null) {
			for (BriefBean bean : result) {
				moreLikeThis.add(new BriefBeanDecorator(model, bean));
			}
		}

		return moreLikeThis;
	}

	
	/**
	 * Create see also suggestions
	 * 
	 * @param fullBean
	 *            The full bean
	 * @return The object contains the see also suggestions
	 */
	private SeeAlsoSuggestions createSeeAlsoSuggestions(SeeAlsoCollector seeAlsoCollector) {

		SeeAlsoSuggestions seeAlsoSuggestions = new SeeAlsoSuggestions(
			config.getSeeAlsoTranslations(),
			config.getSeeAlsoAggregations(),
			seeAlsoCollector
		);

		try {
			Map<String, Integer> seeAlsoResponse = searchService.seeAlso(seeAlsoCollector.getQueries());
			if (seeAlsoResponse != null) {
				for (Entry<String, Integer> entry : seeAlsoResponse.entrySet()) {
					String query = entry.getKey();
					int count = entry.getValue();
					if (count > 0) {
						seeAlsoSuggestions.add(query, count);
					}
				}
			}
		} catch (Exception e) {
			log.error("See also error: " + e.getClass().getCanonicalName() + " " + e.getMessage(),e);
		}

		return seeAlsoSuggestions;
	}

	private EuropeanaMlt createEuropeanaMlt(MltCollector mltCollector, String europeanaId) {
		config.getSeeAlsoTranslations();
		long tSeeAlso0 = (new Date()).getTime();
		EuropeanaMlt mlt = new EuropeanaMlt();
		boolean hasDataProvider = (mltCollector.get("DATA_PROVIDER") != null);
		for (String field : SEE_ALSO_FIELDS.keySet()) {
			if ((field.equals("PROVIDER") && hasDataProvider)
				|| mltCollector.get(field) == null
				|| mltCollector.get(field).size() == 0)
			{
				continue;
			}
			MltSuggestion suggestion = mltCollector.get(field).get(0);

			EuropeanaMltCategory category = new EuropeanaMltCategory(
				suggestion.getLabel(),
				field,
				config.getMltTranslations().get(field)
			);
			category.setQuery(suggestion.getEscapedQuery());
			for (BriefBean bean : searchMltItem(suggestion.getEscapedQuery())) {
				if (!bean.getId().equals(europeanaId)) {
					String title = (StringArrayUtils.isNotBlank(bean.getTitle()) ? bean.getTitle()[0] : bean.getId());
					category.addUrl(new EuropeanaMltLink(bean.getId(), title));
				}
			}
			if (category.getUrls().size() == 11) {
				category.getUrls().remove(10);
			}

			if (category.getUrls().size() > 1) {
				mlt.addCategory(category);
			}
		}
		long tSeeAlso1 = (new Date()).getTime();
		log.info("createEuropeanaMlt takes: " + (tSeeAlso1 - tSeeAlso0));

		return mlt;
	}

	private List<? extends BriefBean> searchMltItem(String queryTerm) {
		Query query = new Query(queryTerm)
			.setPageSize(11)
			.setStart(0) // Solr starts from 0
			.setAllowSpellcheck(false)
			.setAllowFacets(false)
		;
		try {
			ResultSet<? extends BriefBean> resultSet = searchService.search(BriefBean.class, query);
			return resultSet.getResults();
		} catch (SolrTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private SeeAlsoCollector createSeeAlsoCollector(FullBean fullBean) {
		FullBeanShortcut shortcut = new FullBeanShortcut((FullBeanImpl) fullBean);
		SeeAlsoCollector seeAlsoCollector = new SeeAlsoCollector();
		int countPerField = 0, id = 0;
		for (String metaField : SEE_ALSO_FIELDS.keySet()) {
			for (String edmField : SEE_ALSO_FIELDS.get(metaField)) {
				String[] values = shortcut.get(edmField);
				if (values != null) {
					countPerField = 0;
					for (String value : values) {
						if (!StringUtils.isBlank(value)
							&& value.length() < 500 
							&& countPerField < MAX_COUNT_PER_FIELD)
						{
							SeeAlsoSuggestion suggestion = new SeeAlsoSuggestion(metaField, value, id);
							suggestion.makeEscapedQuery(SolrUtils.escapeQuery(suggestion.getQuery()));
							seeAlsoCollector.add(suggestion);
							countPerField++; id++;
						}
					}
				}
			}
		}
		return seeAlsoCollector;
	}

	private MltCollector createMltCollector(FullBean fullBean) {
		FullBeanShortcut shortcut = new FullBeanShortcut((FullBeanImpl) fullBean);
		MltCollector mltCollector = new MltCollector();
		int countPerField = 0, id = 0;
		for (String metaField : SEE_ALSO_FIELDS.keySet()) {
			for (String edmField : SEE_ALSO_FIELDS.get(metaField)) {
				String[] values = shortcut.get(edmField);
				if (values != null) {
					countPerField = 0;
					for (String value : values) {
						if (StringUtils.isNotBlank(value)
							&& value.length() < 500
							&& countPerField < MAX_COUNT_PER_FIELD
							&& !mltStopwordsService.check(value)
						)
						{
							boolean clear = true;
							if (StringUtils.equals(metaField, "PROVIDER") ||
							    StringUtils.equals(metaField, "DATA_PROVIDER")) {
								clear = false;
							}
							log.info("new MltSuggestion(" + metaField + ", " + value + ", " + id + ")");
							MltSuggestion suggestion = new MltSuggestion(metaField, value, id, clear);
							suggestion.makeEscapedQuery(SolrUtils.escapeQuery(suggestion.getQuery()));
							mltCollector.add(suggestion);
							countPerField++; id++;
						}
					}
				}
			}
		}
		return mltCollector;
	}
}