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

package eu.europeana.portal2.web.model.abstracts;

import java.util.List;

import eu.europeana.corelib.definitions.solr.beans.BriefBean;

/**
 * @author Willem-Jan Boogerd <www.eledge.net/contact>
 */
public class AbstractSearchResults extends ApiResponse {

	public long itemsCount;

	public long totalResults;
	
	public List<? extends BriefBean> items;
	
	public AbstractSearchResults(String action) {
		super(null, action);
	}

	public AbstractSearchResults() {
		super();
	}
	
	public List<? extends BriefBean> getItems() {
		return items;
	}
}
