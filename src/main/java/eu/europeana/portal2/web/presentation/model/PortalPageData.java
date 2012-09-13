package eu.europeana.portal2.web.presentation.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import eu.europeana.corelib.web.model.PageData;

public abstract class PortalPageData extends PageData {

	private final Logger log = Logger.getLogger(getClass().getName());

	private String theme = "default";

	private String googlePlusPublisherId;
	
	private List<String> messages = null;
	
	private boolean useCache = true;

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public void setGooglePlusPublisherId(String googlePlusPublisherId) {
		this.googlePlusPublisherId = googlePlusPublisherId;
	}

	public String getGooglePlusPublisherId() {
		return googlePlusPublisherId;
	}
	
	public void addMessage(String message) {
		if (messages == null) {
			messages = new ArrayList<String>();
		}
		messages.add(message);
	}

	public List<String> getMessages() {
		return messages;
	}

	public boolean isUseCache() {
		return useCache;
	}

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}
}
