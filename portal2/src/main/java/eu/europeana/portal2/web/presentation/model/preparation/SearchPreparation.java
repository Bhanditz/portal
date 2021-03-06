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

package eu.europeana.portal2.web.presentation.model.preparation;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import eu.europeana.corelib.definitions.edm.beans.BriefBean;
import eu.europeana.corelib.definitions.model.web.BreadCrumb;
import eu.europeana.corelib.web.utils.UrlBuilder;
import eu.europeana.portal2.web.presentation.model.data.SearchData;
import eu.europeana.portal2.web.presentation.model.data.decorators.BriefBeanViewDecorator;
import eu.europeana.portal2.web.presentation.model.submodel.BriefBeanView;

public abstract class SearchPreparation extends SearchData {

	private String imageUri;
	
	public void setImageUri(String imageUri){
		this.imageUri = imageUri;
	}
	/**
	 * pack into decorator class
	 * 
	 * @param view
	 *            Original search results
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("unchecked")
	public void setBriefBeanView(BriefBeanView view) throws UnsupportedEncodingException {
		briefBeanView = new BriefBeanViewDecorator(this, view);
		briefBeanView.setImageUri(imageUri);
		setResults((List<BriefBean>) briefBeanView.getBriefBeans());
		setBreadcrumbs();
		setStart(view.getPagination().getStart());
		setStartPage(getBriefBeanView().getPagination().getPageNumber());
	}

	private void setBreadcrumbs() throws UnsupportedEncodingException {
		if (!isEmbedded()) {
			breadcrumbs = briefBeanView.getPagination().getBreadcrumbs();
		} else {
			breadcrumbs = new ArrayList<BreadCrumb>();
			List<BreadCrumb> defaultBreadcrumbs = briefBeanView.getPagination().getBreadcrumbs();

			/**
			 * We don't want the normal default as that will remove all the
			 * default filters that have been applied
			 */
			defaultBreadcrumbs.remove(0);

			for (BreadCrumb crumb : defaultBreadcrumbs) {
				/**
				 * The only crumbs we are interested in in embed mode are the
				 * refine search crumbs. Therefore we only include these and the
				 * default query. The default query is defined by the last non
				 * refine search related crumb.
				 */
				if (crumb.getHref().contains("qf=text:")) {
					breadcrumbs.add(crumb);
				}
			}

			/**
			 * Make the default crumb the first in the list
			 */
			UrlBuilder url = new UrlBuilder(briefBeanView.getPagination().getPresentationQuery().getQueryToSave());

			/**
			 * Replace any subsequent search params with the embedded default.
			 */
			if (url.hasParam("query")) {
				String queryValue = URLEncoder.encode(getQuery(), "utf-8");
				url.addParam("query", queryValue, true);
			}
			url.removeParam("rq");
			url.removeStartWith("qf", "text:");
			BreadCrumb defaultCrumb = new BreadCrumb(url.toString(), "*:*");
			breadcrumbs.add(0, defaultCrumb);
		}
	}
}
