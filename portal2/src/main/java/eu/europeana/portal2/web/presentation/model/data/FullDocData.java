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

package eu.europeana.portal2.web.presentation.model.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.europeana.corelib.definitions.edm.beans.FullBean;
import eu.europeana.corelib.definitions.model.web.BreadCrumb;
import eu.europeana.corelib.web.utils.UrlBuilder;
import eu.europeana.portal2.web.model.mlt.EuropeanaMlt;
import eu.europeana.portal2.web.model.mlt.MltCollector;
import eu.europeana.portal2.web.model.seealso.SeeAlsoCollector;
import eu.europeana.portal2.web.model.seealso.SeeAlsoSuggestions;
import eu.europeana.portal2.web.presentation.SearchPageEnum;
import eu.europeana.portal2.web.presentation.model.abstracts.RestLocationsData;
import eu.europeana.portal2.web.presentation.model.data.decorators.BriefBeanDecorator;
import eu.europeana.portal2.web.presentation.model.data.decorators.FullBeanViewDecorator;
import eu.europeana.portal2.web.presentation.model.data.decorators.fullbean.FullBeanDecorator;
import eu.europeana.portal2.web.presentation.model.data.decorators.fullbean.FullBeanShortcut;
import eu.europeana.portal2.web.presentation.model.data.submodel.RightsValue;
import eu.europeana.portal2.web.presentation.model.submodel.FullBeanView;
import eu.europeana.portal2.web.presentation.model.submodel.Image;
import eu.europeana.portal2.web.presentation.semantic.EdmSchemaMapping;
import eu.europeana.portal2.web.presentation.semantic.Element;
import eu.europeana.portal2.web.presentation.semantic.FieldInfo;
import eu.europeana.portal2.web.presentation.semantic.SchemaOrgMapping;

public abstract class FullDocData extends RestLocationsData<Void> {

	/**
	 * The list of fields usable in lightbox. The key is a Java property, the value is an EDM field name
	 */
	protected static final Map<String, String> IMAGE_FIELDS = new HashMap<String, String>();
	static {
		{
			IMAGE_FIELDS.put("EdmIsShownBy", "edm:isShownBy");
			IMAGE_FIELDS.put("EdmHasView", "edm:hasView");
		}
	}; // "EdmObject", "WebResourceAbout",

	protected FullBeanView fullBeanView;

	protected FullBeanViewDecorator fullBeanViewDecorator;

	protected FullBean document;

	protected FullBeanShortcut shortcut;

	protected boolean showFields = false;

	abstract public UrlBuilder prepareFullDocUrl(UrlBuilder builder);

	private String jsonCallback;

	private String format;

	protected SearchPageEnum returnTo = SearchPageEnum.getDefault();

	protected String[] shownAtProviderOverride;

	protected FullBeanDecorator decorator;

	protected List<BriefBeanDecorator> moreLikeThis;

	protected SeeAlsoSuggestions seeAlsoSuggestions;

	private boolean isOptedOut = false;

	protected List<FieldInfo> edmTopLevels;

	protected Map<String, List<FieldInfo>> edmFullMap;

	protected Map<String, Element> edmElements;

	protected SeeAlsoCollector seeAlsoCollector;
	protected MltCollector mltCollector;
	protected EuropeanaMlt europeanaMlt;

	protected RightsValue rightsOption = null;

	protected Map<String, String> allImages = null;

	protected List<Image> imagesToShow;

	protected String lightboxRef = null;
	protected boolean lightboxRefChecked = false;
	protected String lightboxRefField = null;
	protected String urlRef = null;
	protected boolean showSimilarItems = false;
	protected boolean showEuropeanaMlt = false;
	private boolean showContext = false;
	private boolean showHierarchical = false;
	protected List<String> soundCloudAwareCollections = new ArrayList<String>();
	private long startTime;

	public void setEdmSchemaMappings(SchemaOrgMapping schema) {
		this.edmTopLevels = EdmSchemaMapping.getTopLevel(schema);
		this.edmFullMap = EdmSchemaMapping.getFullMap(schema);
		this.edmElements = EdmSchemaMapping.getEdmElements(schema);
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getFormat() {
		return format;
	}

	public void setFullBeanView(FullBeanView fullBeanView) {
		this.fullBeanView = fullBeanView;
		document = fullBeanView.getFullDoc();
		decorator = new FullBeanDecorator(document, getLocale().getLanguage());
		shortcut = decorator.getShortcut();
	}

	public FullBeanViewDecorator getFullBeanView() {
		if (fullBeanViewDecorator == null) {
			fullBeanViewDecorator = new FullBeanViewDecorator(this, fullBeanView, getDocument());
		}
		return fullBeanViewDecorator;
	}

	public void setShowFields(boolean showFields) {
		this.showFields = showFields;
	}

	public boolean isShowFields() {
		return showFields;
	}

	public FullBeanDecorator getDocument() {
		return decorator;
	}

	public void setJsonCallback(String jsonCallback) {
		this.jsonCallback = jsonCallback;
	}

	public String getJsonCallback() {
		return jsonCallback;
	}

	public void setReturnTo(SearchPageEnum returnTo) {
		this.returnTo = returnTo;
	}

	public void setShownAtProviderOverride(String[] shownAtProviderOverride) {
		this.shownAtProviderOverride = shownAtProviderOverride;
	}

	public FullBeanShortcut getShortcut() {
		return shortcut;
	}

	public List<BriefBeanDecorator> getMoreLikeThis() {
		return moreLikeThis;
	}

	public void setMoreLikeThis(List<BriefBeanDecorator> moreLikeThis) {
		this.moreLikeThis = moreLikeThis;
	}

	public SeeAlsoSuggestions getSeeAlsoSuggestions() {
		return seeAlsoSuggestions;
	}

	public void setSeeAlsoSuggestions(SeeAlsoSuggestions seeAlsoSuggestions) {
		this.seeAlsoSuggestions = seeAlsoSuggestions;
	}

	public boolean isOptedOut() {
		return isOptedOut;
	}

	public void setOptedOut(boolean isOptedOut) {
		this.isOptedOut = isOptedOut;
	}

	public boolean isShowSimilarItems() {
		return showSimilarItems;
	}

	public boolean isShowEuropeanaMlt() {
		return showEuropeanaMlt;
	}

	/**
	 * Null-returning getter to satisfy EL
	 */
	public List<BreadCrumb> getBreadcrumbs() {
		return null;
	}

	public Map<String, Element> getEdmElements() {
		return edmElements;
	}

	public EuropeanaMlt getEuropeanaMlt() {
		return europeanaMlt;
	}

	public void setShowSimilarItems(boolean showSimilarItems) {
		this.showSimilarItems = showSimilarItems;
	}

	public void setEuropeanaMlt(EuropeanaMlt europeanaMlt) {
		this.europeanaMlt = europeanaMlt;
	}

	public void setSeeAlsoCollector(SeeAlsoCollector seeAlsoCollector) {
		this.seeAlsoCollector = seeAlsoCollector;
	}

	public void setShowEuropeanaMlt(boolean showEuropeanaMlt) {
		this.showEuropeanaMlt = showEuropeanaMlt;
	}

	public SeeAlsoCollector getSeeAlsoCollector() {
		return this.seeAlsoCollector;
	}

	public void setMltCollector(MltCollector mltCollector) {
		this.mltCollector = mltCollector;
	}

	public MltCollector getMltCollector() {
		return this.mltCollector;
	}

	public Map<String, List<FieldInfo>> getSchemaMap() {
		return edmFullMap;
	}

	public String getLightboxRefField() {
		return lightboxRefField;
	}

	public List<String> getSoundCloudAwareCollections() {
		return soundCloudAwareCollections;
	}

	public void setSoundCloudAwareCollections(
			List<String> soundCloudAwareCollections) {
		this.soundCloudAwareCollections = soundCloudAwareCollections;
	}

	public void setShowContext(boolean showContext) {
		this.showContext = showContext;
	}

	public boolean isShowContext() {
		return showContext;
	}

	public void setShowHierarchical(boolean showHierarchical) {
		this.showHierarchical = showHierarchical;
	}

	public boolean isShowHierarchical() {
		return showHierarchical;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getDuration() {
		long endTime = (new Date()).getTime();
		return (endTime - startTime);
	}
}
