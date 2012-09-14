package eu.europeana.portal2.web.presentation.model;

import java.util.Arrays;
import java.util.List;

import eu.europeana.portal2.web.presentation.model.data.ApiData;

/**
 * Model for API requests
 * 
 * @author peter.kiraly@kb.nl
 */
public class ApiConsolePage extends ApiData {
	
	private String function;
	private String jsonString;
	
	// search parameters
	private String query;
	private String[] refinements;
	private String profile;
	private int start;
	private int rows;
	private String sort;

	// record parameters
	private String collectionId;
	private String recordId;

	private List<String> defaultFunctions = Arrays.asList(new String[]{"search", "record"});
	private List<String> defaultProfiles = Arrays.asList(new String[]{"standard", "portal", "facets", "breadcrumb", "spelling"});
	private List<String> defaultRows = Arrays.asList(new String[]{"12", "24", "48", "96"});

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	public List<String> getDefaultProfiles() {
		return defaultProfiles;
	}

	public List<String> getDefaultRows() {
		return defaultRows;
	}

	public List<String> getDefaultFunctions() {
		return defaultFunctions;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String[] getRefinements() {
		return refinements;
	}

	public void setRefinements(String[] refinements) {
		this.refinements = refinements;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
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

	public String getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}
}
