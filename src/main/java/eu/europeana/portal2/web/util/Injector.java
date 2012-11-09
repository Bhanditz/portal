package eu.europeana.portal2.web.util;

import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import eu.europeana.corelib.db.service.UserService;
import eu.europeana.corelib.definitions.db.entity.relational.User;
import eu.europeana.corelib.web.interceptor.ConfigInterceptor;
import eu.europeana.corelib.web.interceptor.LocaleInterceptor;
import eu.europeana.portal2.services.Configuration;
import eu.europeana.portal2.web.presentation.model.PortalPageData;

public class Injector {

	private final Logger log = Logger.getLogger(getClass().getName());

	private static UserService userService = Beans.getUserService();

	private static ConfigInterceptor configInterceptor = Beans.getConfigInterceptor();

	private static Configuration config = Beans.getConfig();

	private LocaleInterceptor localeChangeInterceptor = Beans.getLocaleChangeInterceptor();

	private HttpServletRequest request;
	private HttpServletResponse response;
	// private Locale locale;
	private long start;

	public Injector() {
		log.info("create injector(): " + (config == null));
	}

	public Injector(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		start = new Date().getTime();
		// log.info("create injector(" + start + "): " + (config == null));
		this.request = request;
		this.response = response;
		// this.locale = locale;
		// log.info("locale: " + locale.getLanguage());
		localeChangeInterceptor.preHandle(request, response, this);
	}

	public void injectProperties(PortalPageData model) {
		model.setGooglePlusPublisherId(StringUtils.trimToEmpty(config.getPortalGooglePlusPublisherId()));
		model.setTheme(getTheme(request));
		User user = ControllerUtil.getUser(userService);
		model.setUser(user);
	}

	private String getTheme(HttpServletRequest request) {
		return ControllerUtil.getSessionManagedTheme(request, config.getDefaultTheme());
	}

	public void postHandle(Object object, ModelAndView page) {
		try {
			configInterceptor.postHandle(request, response, object, page);
		} catch (Exception e) {
			log.severe("Exception - " + e.getClass() + ": " + e.getMessage());
			log.severe(ControllerUtil.getStackTrace(e));
			log.severe(String.format("configInterceptor: %s, request: %s, response: %s, object: %s, page: %s",
				(configInterceptor != null), (request != null), (response != null), (object != null), (page != null)));
			e.printStackTrace();
		}
		logTime(object.getClass().getSimpleName());
	}

	public void logTime(String type) {
		long end = new Date().getTime();
		log.info(type + " compare: " + end + " vs " + start);
		ControllerUtil.logTime(type, (end - start));
	}
}
