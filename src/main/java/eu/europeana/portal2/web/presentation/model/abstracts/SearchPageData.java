/*
 * Copyright 2007-2012 The Europeana Foundation
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

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import eu.europeana.corelib.web.model.PageData;
import eu.europeana.portal2.web.presentation.PortalLanguage;

/**
 * Abstract model for all pages containing a searchform...
 * 
 * @author GJW Boogerd
 * 
 */
public abstract class SearchPageData extends PageData {

	private String query;

	private boolean enableRefinedSearch = false;

	private String[] refinements;

	private int start = 1;

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
}
