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

package eu.europeana.portal2.web.presentation.model.abstracts;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import eu.europeana.corelib.utils.model.LanguageVersion;
import eu.europeana.corelib.web.service.EuropeanaUrlService;
import eu.europeana.corelib.web.service.impl.EuropeanaUrlServiceImpl;
import eu.europeana.corelib.web.utils.UrlBuilder;
import eu.europeana.portal2.web.presentation.PortalLanguage;
import eu.europeana.portal2.web.presentation.model.PortalPageData;
import eu.europeana.portal2.web.presentation.model.data.decorators.LanguageVersionLink;

/**
 * Abstract model for all pages containing a searchform...
 * 
 * @author GJW Boogerd
 * 
 */
public abstract class SearchPageData extends PortalPageData {

	private String query;

	private boolean enableRefinedSearch = false;

	private String[] refinements;

	/**
	 * The first result count
	 */
	private int start = 1;

	/**
	 * Number of results
	 */
	private int rows = 24;

	/**
	 * The Solr sort parameter
	 */
	private String sort;

	/**
	 * The translated version of the query
	 */
	private List<LanguageVersion> queryTranslations;

	private int keywordLanguagesLimit;

	public void setQuery(String query) {
		this.query = query;
	}

	public String getQuery() {
		return query;
	}

	public void setEnableRefinedSearch(boolean enableRefinedSearch) {
		this.enableRefinedSearch = enableRefinedSearch;
	}

	public boolean isEnableRefinedSearch() {
		return enableRefinedSearch && !isEmbedded();
	}

	public boolean isEuroeanaUri() {
		return StringUtils.startsWith(query, "europeana_uri:");
	}

	public void setRefinements(String[] refinements) {
		this.refinements = refinements;
	}

	public String[] getRefinements() {
		return ArrayUtils.nullToEmpty(refinements);
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getStart() {
		return start;
	}

	public String getImageLocale() {
		PortalLanguage current = PortalLanguage.safeValueOf(getLocale());
		if (!current.hasImageSupport()) {
			current = PortalLanguage.EN;
		}
		return current.getLanguageCode();
	}

	/**
	 * Get the list of available languages
	 */
	public List<PortalLanguage> getPortalLanguages() {
		return PortalLanguage.getSupported();
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public List<LanguageVersion> getQueryTranslations() {
		return queryTranslations;
	}

	public void setQueryTranslations(List<LanguageVersion> queryTranslations) {
		this.queryTranslations = queryTranslations;
	}

	public List<LanguageVersionLink> getQueryTranslationLinks() {
		List<LanguageVersionLink> links = new ArrayList<LanguageVersionLink>();
		List<LanguageVersion> queryTranslationsList = getQueryTranslations();
		if (queryTranslationsList != null && queryTranslationsList.size() > 0) {
			EuropeanaUrlService urlService = EuropeanaUrlServiceImpl.getBeanInstance();
			try {
				for (LanguageVersion query : queryTranslationsList) {
					UrlBuilder url = getBaseSearchUrl(urlService);
					if (queryTranslationsList.size() == 1) {
						url.addMultiParam("qt", "false");
					} else {
						for (LanguageVersion other : queryTranslationsList) {
							if (!other.equals(query)) {
								url.addMultiParam("qt", other.getLanguageCode() + ":" + other.getText());
							}
						}
					}
					links.add(new LanguageVersionLink(query, url.toString()));
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return links;
	}

	private UrlBuilder getBaseSearchUrl(EuropeanaUrlService urlService)
			throws UnsupportedEncodingException {
		UrlBuilder url = urlService.getPortalSearch(true, getQuery(), String.valueOf(getRows()));
		url.addParam("qf", getRefinements());
		return url;
	}

	public int getKeywordLanguagesLimit() {
		return keywordLanguagesLimit;
	}

	public void setKeywordLanguagesLimit(int keywordLanguagesLimit) {
		this.keywordLanguagesLimit = keywordLanguagesLimit;
	}
}
