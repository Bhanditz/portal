/*
 *  Copyright 2007-2013 The Europeana Foundation
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
package eu.europeana.portal2.web.model;

import eu.europeana.corelib.solr.bean.impl.FullBeanImpl;

/**
 * @see eu.europeana.corelib.definitions.edm.beans.FullBean
 */
public class FullBean4Json extends FullBeanImpl {

	private String id;

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}
}
