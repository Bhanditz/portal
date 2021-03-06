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

package eu.europeana.portal2.web.model.seealso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Collector of SeeAlsoSuggestion objects
 * 
 * You can add, and retrieve individual objects, get the objects belongs to a 
 * given field, and get the list of Solr facet queries.
 */
public class SeeAlsoCollector {

	private List<SeeAlsoSuggestion> suggestions = new ArrayList<SeeAlsoSuggestion>();
	private Map<String, List<Integer>> fieldIndex = new HashMap<String, List<Integer>>();

	/**
	 * Adds an object to the collection
	 * @param suggestion
	 */
	public void add(SeeAlsoSuggestion suggestion) {
		int id = suggestion.getId();
		String field = suggestion.getMetaField();
		suggestions.add(id, suggestion);
		if (!fieldIndex.containsKey(field)) {
			fieldIndex.put(field, new ArrayList<Integer>());
		}
		fieldIndex.get(field).add(id);
	}

	/**
	 * Returns the field names
	 * @return
	 */
	public Set<String> getFields() {
		return fieldIndex.keySet();
	}

	/**
	 * Gets the list of objects belonged to the same field
	 * @param field
	 *   The name of the field
	 * @return
	 */
	public List<SeeAlsoSuggestion> get(String field) {
		if (fieldIndex.containsKey(field)) {
			List<SeeAlsoSuggestion> suggestions4field = new ArrayList<SeeAlsoSuggestion>();
			for (int i : fieldIndex.get(field)) {
				suggestions4field.add(suggestions.get(i));
			}
			return suggestions4field;
		}
		return null;
	}

	/**
	 * Retrieve an object by its id
	 * @param id
	 * @return
	 */
	public SeeAlsoSuggestion findById(int id) {
		if (id < 0 || id >= suggestions.size()) {
			return null;
		}
		return suggestions.get(id);
	}

	/**
	 * Get the list of Solr queries in tagged form
	 * @return
	 */
	public List<String> getQueries() {
		return getQueries(true);
	}

	/**
	 * Get the list of Solr queries
	 * @return
	 */
	public List<String> getQueries(boolean tagged) {
		List<String> queries = new ArrayList<String>();
		for (SeeAlsoSuggestion suggestion : suggestions) {
			if (tagged) {
				queries.add(suggestion.getTaggedEscapedQuery());
			} else {
				queries.add(suggestion.getEscapedQuery());
			}
		}
		return queries;
	}
}
