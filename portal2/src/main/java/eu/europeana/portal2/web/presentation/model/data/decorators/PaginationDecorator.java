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

package eu.europeana.portal2.web.presentation.model.data.decorators;

import java.util.List;

import eu.europeana.corelib.definitions.model.web.BreadCrumb;
import eu.europeana.portal2.web.presentation.model.data.SearchData;
import eu.europeana.portal2.web.presentation.model.submodel.PresentationQuery;
import eu.europeana.portal2.web.presentation.model.submodel.ResultPagination;

public class PaginationDecorator implements ResultPagination {

	SearchData model;
	ResultPagination pagination;

	public PaginationDecorator(SearchData model, ResultPagination pagination) {
		this.model = model;
		this.pagination = pagination;
	}

	@Override
	public boolean isPrevious() {
		return pagination.isPrevious();
	}

	@Override
	public boolean isNext() {
		return pagination.isNext();
	}

	@Override
	public boolean isFirst() {
		return pagination.isFirst();
	}

	@Override
	public boolean isLast() {
		return pagination.isLast();
	}

	@Override
	public int getPreviousPage() {
		return pagination.getPreviousPage();
	}

	@Override
	public int getNextPage() {
		return pagination.getNextPage();
	}

	@Override
	public int getFirstPage() {
		return pagination.getFirstPage();
	}

	@Override
	public int getLastPage() {
		return pagination.getLastPage();
	}

	@Override
	public int getNumberOfPages() {
		return pagination.getNumberOfPages();
	}

	@Override
	public int getLastViewableRecord() {
		return pagination.getLastViewableRecord();
	}

	@Override
	public int getRows() {
		return pagination.getRows();
	}

	@Override
	public int getStart() {
		return pagination.getStart();
	}

	@Override
	public List<BreadCrumb> getBreadcrumbs() {
		return pagination.getBreadcrumbs();
	}

	@Override
	public PresentationQuery getPresentationQuery() {
		return pagination.getPresentationQuery();
	}

	@Override
	public int getPageNumber() {
		return pagination.getPageNumber();
	}

	@Override
	public int getNumFound() {
		return pagination.getNumFound();
	}
}
