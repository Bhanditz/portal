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

package eu.europeana.portal2.web.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Suggestion;

import eu.europeana.portal2.web.model.facets.Facet;
import eu.europeana.portal2.web.model.facets.LabelFrequency;
import eu.europeana.portal2.web.model.spellcheck.SpellCheck;

/**
 * @author Willem-Jan Boogerd <www.eledge.net/contact>
 */
public class ModelUtils {

	private ModelUtils() {
		// Constructor must be private
	}

	public static List<Facet> conventFacetList(List<FacetField> facetFields) {
		if ((facetFields != null) && !facetFields.isEmpty()) {
			List<Facet> facets = new ArrayList<Facet>();
			for (FacetField facetField : facetFields) {
				if (facetField.getValues() != null) {
					Facet facet = new Facet();
					facet.name = facetField.getName();
					for (FacetField.Count count : facetField.getValues()) {
						if (StringUtils.isNotEmpty(count.getName()) && (count.getCount() > 0)) {
							facet.fields.add(new LabelFrequency(count.getName(), count.getCount()));
						}
					}
					if (!facet.fields.isEmpty()) {
						facets.add(facet);
					}
				}
			}
			return facets;
		}
		return null;
	}

	public static SpellCheck convertSpellCheck(SpellCheckResponse response) {
		if (response != null) {
			SpellCheck spellCheck = new SpellCheck();
			spellCheck.correctlySpelled = response.isCorrectlySpelled();
			for (Suggestion suggestion : response.getSuggestions()) {
				for (int i=0; i<suggestion.getNumFound(); i++ ) {
					LabelFrequency value = new LabelFrequency();
					value.label = suggestion.getAlternatives().get(i);
					value.count = suggestion.getAlternativeFrequencies().get(i).longValue();
					spellCheck.suggestions.add(value);
				}
			} 
			return spellCheck;
		}
		return null;
	}
}