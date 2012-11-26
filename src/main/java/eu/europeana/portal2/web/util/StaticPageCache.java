package eu.europeana.portal2.web.util;

import java.util.Calendar;

import javax.annotation.Resource;

import eu.europeana.portal2.services.Configuration;

public class StaticPageCache extends StaticCache {

	@Resource(name="configurationService") private Configuration config;

	public StaticPageCache() {
		super();
	}

	protected Integer getCheckFrequency() {
		return config.getStaticPageCheckFrequencyInMinute();
	}

	protected Calendar getLastCheck() {
		return ApplicationStatusInfo.getLastStaticPageCacheChecked();
	}

	protected void setLastCheck(Calendar lastCheck) {
		ApplicationStatusInfo.setLastStaticPageCacheChecked(lastCheck);
	}
}
