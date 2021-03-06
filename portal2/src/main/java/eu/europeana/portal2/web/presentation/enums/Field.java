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

package eu.europeana.portal2.web.presentation.enums;

import eu.europeana.corelib.definitions.ApplicationContextContainer;
import eu.europeana.corelib.utils.StringArrayUtils;
import eu.europeana.portal2.web.presentation.enums.fieldutils.FieldValueProcessor;
import eu.europeana.portal2.web.presentation.enums.fieldutils.Wordcapitalizer;
import eu.europeana.portal2.web.presentation.semantic.SchemaOrgMapping;

/**
 * Definition and properties of EDM fields.
 * 
 * The properties are: field name, translation key of label, parent entity, search pattern, 
 * sort values, whether it has seperate lines, translatable, external services, maxLength, 
 * processor
 */
public enum Field {

	// Who for dc:creator and dc:contributor, 
	// What for dc:type and dc:subject
	// When for dc:date, dc:created, dcterms:temportal, dc:created, dc:publsihed
	// Where for dc:coverage and dcterms:spatial.
	// DC
	DC_IDENTIFIER("dc:identifier", "dc_identifier_t", false, false, false, ExternalService.none()),
	DC_PUBLISHER("dc:publisher", "dc_publisher_t", false, false, false, 
		ExternalService.WIKIPEDIA, ExternalService.INTERNETARCHIVE, ExternalService.SMITHSONIAN,
		ExternalService.GOOGLE, ExternalService.GOOGLEBOOKS, ExternalService.WORLDCAT,
		ExternalService.YOUTUBE, ExternalService.FLICKR),
	DC_SUBJECT("dc:subject", "dc_subject_t", "what:%s", false, false, true,
		ExternalService.WIKIPEDIA, ExternalService.INTERNETARCHIVE, ExternalService.SMITHSONIAN,
		ExternalService.GOOGLE, ExternalService.GOOGLEBOOKS, ExternalService.WORLDCAT,
		ExternalService.FLICKR, ExternalService.AMAZON, ExternalService.YOUTUBE, ExternalService.IMDB),
	DC_COVERAGE("dc:coverage", "dc_coverage_t", false, false, true, ExternalService.GOOGLE, 
		ExternalService.GOOGLEMAPS),
	DC_TYPE("dc:type", "dc_type_t", "what:%s", false, false, true, 
		ExternalService.WIKIPEDIA, ExternalService.INTERNETARCHIVE, ExternalService.SMITHSONIAN,
		ExternalService.GOOGLE, ExternalService.GOOGLEBOOKS, ExternalService.WORLDCAT,
		ExternalService.FLICKR, ExternalService.AMAZON, ExternalService.YOUTUBE, ExternalService.IMDB),
	DC_RELATION("dc:relation", "dc_relation_t", false, false, false, ExternalService.none()),
	DC_TITLE("dc:title", "dc_title_t", "dc_title:%s OR dc_alternative:%s", false, false, true, 
		ExternalService.WIKIPEDIA, ExternalService.INTERNETARCHIVE, ExternalService.SMITHSONIAN,
		ExternalService.GOOGLEBOOKS, ExternalService.GOOGLE, ExternalService.WORLDCAT,
		ExternalService.FLICKR, ExternalService.AMAZON, ExternalService.YOUTUBE, ExternalService.IMDB),
	DC_DATE("dc:date", "dc_date_t", "when:%s", false, false, true, ExternalService.none()),
	DC_CREATOR("dc:creator", "Creator_t", "who:%s", false, true, false, 
		ExternalService.WIKIPEDIA, ExternalService.GOOGLE, ExternalService.GOOGLEBOOKS,
		ExternalService.WORLDCAT, ExternalService.FLICKR, ExternalService.AMAZON,
		ExternalService.YOUTUBE, ExternalService.IMDB),
	// DC_DESCRIPTION("dc:description", "Description_t", null, null, null, true, true, 800, true, ExternalService.none()),
	DC_DESCRIPTION("dc:description", "Description_t", true), // translatable
	DC_LANGUAGE("dc:language", "languageDropDownList_t", false, false, false, ExternalService.none()),
	DC_FORMAT("dc:format", "dc_format_t", false, true, true, ExternalService.none()),
	DC_SOURCE("dc:source", "dc_source_t", false, true, false, 
		ExternalService.WIKIPEDIA, ExternalService.INTERNETARCHIVE, ExternalService.SMITHSONIAN,
		ExternalService.GOOGLE, ExternalService.GOOGLEMAPS, ExternalService.YOUTUBE,
		ExternalService.IMDB, ExternalService.FLICKR, ExternalService.GOOGLEBOOKS,
			ExternalService.WORLDCAT),
	DC_RIGHTS("dc:rights", "dc_rights_t", true, false, true, ExternalService.none()),
	DC_CONTRIBUTOR("dc:contributor", "dc_contributor_t", "edm:Proxy", "who:%s"),

	// DCTERMS
	DCTERMS_ALTERNATIVE("dcterms:alternative", "dcterms_alternative_t", false, false, true, ExternalService.none()),
	DCTERMS_CONFORMSTO("dcterms:conformsTo", null),
	DCTERMS_CREATED("dcterms:created", "dcterms_created_t"),
	DCTERMS_EXTENT("dcterms:extent", null),
	DCTERMS_HASFORMAT("dcterms:hasFormat", null),
	DCTERMS_HASPART("dcterms:hasPart", "dcterms_hasPart_t"),
	DCTERMS_HASVERSION("dcterms:hasVersion", null),
	DCTERMS_ISFORMATOF("dcterms:isFormatOf", null),
	DCTERMS_ISPARTOF("dcterms:isPartOf", "dcterms_isPartOf_t"),
	PROXY_DCTERMS_ISPARTOF("dcterms:isPartOf", "dcterms_isPartOf_t", "edm:Proxy"),
	PLACE_DCTERMS_ISPARTOF("dcterms:isPartOf", "dcterms_isPartOf_t", "edm:Place"),
	DCTERMS_ISREFERENCEDBY("dcterms:isReferencedBy", null),
	DCTERMS_ISREPLACEDBY("dcterms:isReplacedBy", null),
	DCTERMS_ISREQUIREDBY("dcterms:isRequiredBy", null),
	DCTERMS_ISSUED("dcterms:issued", "dcterms_issued_t", false, false, true, ExternalService.none()),
	DCTERMS_ISVERSIONOF("dcterms:isVersionOf", null),
	DCTERMS_MEDIUM("dcterms:medium", null),
	DCTERMS_PROVENANCE("dcterms:provenance", "dcterms_provenance_t", false, false, false, ExternalService.none()),
	DCTERMS_REFERENCES("dcterms:references", null),
	DCTERMS_REPLACES("dcterms:replaces", null),
	DCTERMS_REQUIRES("dcterms:requires", null),
	DCTERMS_SPATIAL("dcterms:spatial", "dcterms_spatial_t", "where:%s", false, false, true, ExternalService.none()),
	DCTERMS_TABLEOFCONTENTS("dcterms:tableOfContents", null),
	DCTERMS_TEMPORAL("dcterms:temporal", "dcterms_temporal_t", false, false, true, ExternalService.none()),

	// EUROPEANA
	EUROPEANA_URI("europeana:uri", null),
	EUROPEANA_COMPLETENESS("europeana:completeness", "europeana_completeness_t"),
	EDM_COUNTRY("edm:country", "edm_country_t", new Wordcapitalizer()),
	EDM_PROVIDER("edm:provider", "Provider_t", "europeana_provider:\"%s\" OR europeana_country:\"%s\"", false, false, false, 
		ExternalService.WIKIPEDIA, ExternalService.INTERNETARCHIVE, ExternalService.SMITHSONIAN,
		ExternalService.GOOGLE, ExternalService.GOOGLEMAPS, ExternalService.YOUTUBE,
		ExternalService.IMDB, ExternalService.FLICKR, ExternalService.GOOGLEBOOKS,
		ExternalService.WORLDCAT),
	EDM_COLLECTIONNAME("edm:collectionName", "europeana_collectionName_t", null, "europeana_collectionName:%s"),
	EDM_ISSHOWNAT("edm:isShownAt", null),
	EDM_ISSHOWNBY("edm:isShownBy", null),
	EDM_OBJECT("edm:object", null, true, false),
	EDM_PREVIEW("edm:preview", null, true, false),
	EDM_LANGUAGE("edm:language", null),
	EDM_PLACE_LONGITUDE("wgs84_pos:lat", "edm_place_longitude_t"),
	EDM_PLACE_LATITUDE("wgs84_pos:long", "edm_place_latitude_t"),
	EDM_TYPE("edm:type", null),
	EDM_USERTAG("edm:userTag", null),
	EDM_YEAR("edm:year", null),
	EDM_RIGHTS("edm:rights", "europeana_rights_t", false, false, false, ExternalService.none()),
	EDM_DATAPROVIDER("edm:dataProvider", "europeana_dataProvider_t", "europeana_dataProvider:\"%s\"", false, false, false,
		ExternalService.WIKIPEDIA, ExternalService.INTERNETARCHIVE,
		ExternalService.SMITHSONIAN, ExternalService.GOOGLE, ExternalService.GOOGLEBOOKS,
		ExternalService.WORLDCAT, ExternalService.FLICKR, ExternalService.AMAZON,
		ExternalService.YOUTUBE, ExternalService.IMDB),
	EDM_UGC("edm:UGC", null),
	EDM_CURRENTLOCATION("edm:currentLocation", null),
	EDM_HASTYPE("edm:hasType", null),
	EDM_HASVIEW("edm:hasView", null),
	EDM_ISRELATEDTO("edm:isRelatedTo", null),
	EDM_ISREPRESENTATIONOF("edm:isRepresentationOf", null),
	EDM_LANDINGPAGE("edm:landingPage", "edm_landingPage_t"),
	EDM_HASMET("edm:hasMet", null),
	EDM_INCORPORATES("edm:incorporates", null),
	EDM_ISDERIVATIVEOF("edm:isDerivativeOf", null),
	EDM_ISSIMILARTO("edm:isSimilarTo", null),
	EDM_ISSUCCESSOROF("edm:isSuccessorOf", null),
	EDM_REALIZES("edm:realizes", null),
	EDM_ISNEXTINSEQUENCE("edm:isNextInSequence", "edm_isNextInSequence_t"),

	SKOS_PREFLABEL("skos:prefLabel", null),
	SKOS_NOTE("skos:note", null),

	FOAF_NAME("foaf:name", null),

	RDA_BIOGRAPHICALINFORMATION("rdaGr2:biographicalInformation", null),
	RDA_DATEOFBIRTH("rdaGr2:dateOfBirth", null),
	RDA_DATEOFDEATH("rdaGr2:dateOfDeath", null),
	RDA_DATEOFESTABLISHMENT("rdaGr2:dateOfEstablishment", null),
	RDA_GENDER("rdaGr2:gender", null),
	RDA_PROFESSIONOROCCUPATION("rdaGr2:professionOrOccupation", null),

	// ENRICHMENT
	ENRICHMENT_AGENT_ABOUT("enrichment:agent_term", "agent_term_t"),
	ENRICHMENT_AGENT_LABEL("enrichment:agent_label", "agent_label_t"),
	ENRICHMENT_CONCEPT_ABOUT("enrichment:concept_term", "concept_term_t"),
	ENRICHMENT_CONCEPT_LABEL("enrichment:concept_label", "concept_label_t"),
	ENRICHMENT_CONCEPT_BROADER_TERM("enrichment:concept_broader_term", "concept_broader_term_t"),
	ENRICHMENT_CONCEPT_BROADER_LABEL("enrichment:concept_broader_label", "concept_broader_label_t"),
	ENRICHMENT_PLACE_ABOUT("enrichment:place_term", "place_term_t"),
	ENRICHMENT_PLACE_LABEL("enrichment:place_label", "place_label_t"),
	ENRICHMENT_PLACE_LAT_LONG("enrichment:place_lat_long", "place_lat_long_t"),
	ENRICHMENT_PLACE_LATITUDE("enrichment:place_latitude", null),
	ENRICHMENT_PLACE_LONGITUDE("enrichment:place_longitude", null),
	ENRICHMENT_PLACE_BROADER_TERM("enrichment:place_broader_term", "place_broader_term_t"),
	ENRICHMENT_PLACE_BROADER_LABEL("enrichment:place_broader_label", "place_broader_label_t"),
	ENRICHMENT_TIMESPAN_ABOUT("enrichment:period_term", "period_term_t"),
	ENRICHMENT_TIMESPAN_LABEL("enrichment:period_label", "period_label_t"),
	ENRICHMENT_TIMESPAN_BEGIN("enrichment:period_begin", "period_begin_t"),
	ENRICHMENT_TIMESPAN_END("enrichment:period_end", "period_end_t"),
	ENRICHMENT_TIMESPAN_BROADER_TERM("enrichment:period_broader_term", "period_broader_term_t"),
	ENRICHMENT_TIMESPAN_BROADER_LABEL("enrichment:period_broader_label", "period_broader_label"),

	;

	private String fieldName;
	private String fieldLabel;
	private String contextualEntity;
	private String searchOn;
	private boolean sortValues;
	protected boolean seperateLines;
	protected boolean translatable;
	private ExternalService[] externalServices;
	private int maxLength;
	private FieldValueProcessor processor;
	private boolean optOutAware = false;


	private Field(String name, String label) {
		this(name, label, null, null, null, false, false, -1, false, ExternalService.none());
	}

	private Field(String name, String label, boolean translatable) {
		this(name, label, null, null, null, false, false, -1, translatable, ExternalService.none());
	}

	private Field(String name, String label, boolean optOutAware, boolean translatable) {
		this(name, label, null, null, null, false, false, -1, translatable, ExternalService.none());
		this.optOutAware = optOutAware;
	}

	private Field(String name, String label, FieldValueProcessor processor) {
		// name, label, contextualEntity, processor, searchOn, seperateLines, sortValues, maxLength, translatable, externalServices
		this(name, label, null, processor, null, false, false, -1, false, ExternalService.none());
	}

	private Field(String name, String label, String contextualEntity) {
		this(name, label, contextualEntity, null, null, false, false, -1, false, ExternalService.none());
	}

	private Field(String name, String label, String contextualEntity, String searchOn) {
		this(name, label, contextualEntity, null, searchOn, false, false, -1, false, ExternalService.none());
	}

	private Field(String name, String label, boolean seperateLines,
			boolean sortValues, boolean translatable, ExternalService... externalServices) {
		this(name, label, null, null, null, seperateLines, sortValues, -1, translatable, externalServices);
	}

	private Field(String name, String label, String searchOn,
			boolean seperateLines, boolean sortValues, boolean translatable,
			ExternalService... externalServices) {
		this(name, label, null, null, searchOn, seperateLines, sortValues, -1, translatable, externalServices);
	}

	private Field(String name, String label, String contextualEntity, FieldValueProcessor processor,
			String searchOn, boolean seperateLines, boolean sortValues,
			int maxLength, boolean translatable, ExternalService... externalServices) {
		fieldName = name;
		fieldLabel = label;
		this.contextualEntity = contextualEntity;
		this.processor = processor;
		this.searchOn = searchOn;
		this.seperateLines = seperateLines;
		this.sortValues = sortValues;
		this.maxLength = maxLength;
		this.translatable = translatable;
		this.externalServices = externalServices;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getFieldLabel() {
		return fieldLabel;
	}

	public String getContextualEntity() {
		return contextualEntity;
	}

	public boolean isSeperateLines() {
		return seperateLines;
	}

	public boolean doSortValues() {
		return sortValues;
	}

	public boolean isTranslatable() {
		return translatable;
	}

	public ExternalService[] getExternalServices() {
		return externalServices;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public String getSearchOn() {
		return searchOn;
	}

	public FieldValueProcessor getProcessor() {
		return processor;
	}

	public boolean isOptOutAware() {
		return optOutAware;
	}

	public String getSemanticAttributes() {
		return getSchemaOrgMapping().getSemanticAttributes(fieldName, contextualEntity);
	}

	public boolean isSemanticUrl() {
		return getSchemaOrgMapping().isSemanticUrl(fieldName, contextualEntity);
	}

	public String[] getValues(String[] values) {
		if (StringArrayUtils.isNotBlank(values)) {
			if (getProcessor() != null) {
				for (int i = 0; i < values.length; i++) {
					values[i] = getProcessor().processValue(values[i]);
				}
			}
		}
		return values;
	}

	private static SchemaOrgMapping schemaOrgMapping;

	private SchemaOrgMapping getSchemaOrgMapping() {
		if (schemaOrgMapping == null) {
			schemaOrgMapping = ApplicationContextContainer.getBean(SchemaOrgMapping.class);
		}
		return schemaOrgMapping;
	}

	public boolean isEntityIdentifier() {
		if (this.equals(Field.ENRICHMENT_AGENT_ABOUT)
			|| this.equals(Field.ENRICHMENT_CONCEPT_ABOUT)
			|| this.equals(Field.ENRICHMENT_PLACE_ABOUT)
			|| this.equals(Field.ENRICHMENT_TIMESPAN_ABOUT)) {
			return true;
		}
		return false;
	}

}
