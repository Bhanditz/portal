package eu.europeana.portal2.services;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.ModelAndView;

import eu.europeana.corelib.db.service.UserService;
import eu.europeana.corelib.definitions.db.entity.relational.User;
import eu.europeana.corelib.web.interceptor.ConfigInterceptor;
import eu.europeana.portal2.web.presentation.model.PortalPageData;
import eu.europeana.portal2.web.util.ControllerUtil;

public class Configuration {

	@Resource(name="corelib_db_userService") private UserService userService;

	@Resource(name="corelib_web_configInterceptor") private ConfigInterceptor configInterceptor;
	
	@Resource private Properties europeanaProperties;

	private final Logger log = Logger.getLogger(getClass().getName());

	private HttpServletRequest request;
	private HttpServletResponse response;
	private Locale locale;

	///////////////////////////////// properties

	// basic portal value
	@Value("#{europeanaProperties['portal.name']}")
	private String portalName;

	@Value("#{europeanaProperties['portal.server']}")
	private String portalServer;

	@Value("#{europeanaProperties['portal.theme']}")
	private String defaultTheme;

	@Value("#{europeanaProperties['static.page.path']}")
	private String staticPagePath;

	// blog settings
	@Value("#{europeanaProperties['portal.blog.url']}")
	private String blogUrl;

	@Value("#{europeanaProperties['portal.blog.timeout']}")
	private Integer blogTimeout;

	// Pinterest settings
	@Value("#{europeanaProperties['portal.pinterest.url']}")
	private String pintUrl;

	@Value("#{europeanaProperties['portal.pinterest.feedurl']}")
	private String pintFeedUrl;

	@Value("#{europeanaProperties['portal.pinterest.timeout']}")
	private Integer pintTimeout;

	@Value("#{europeanaProperties['portal.pinterest.itemslimit']}")
	private Integer pintItemLimit;

	// Google+ settings
	@Value("#{europeanaProperties['portal.google.plus.publisher.id']}")
	private static String portalGooglePlusPublisherId;

	// responsive images in the index page
	@Value("#{europeanaProperties['portal.responsive.widths']}")
	private String responsiveImageWidthString;

	@Value("#{europeanaProperties['portal.responsive.labels']}")
	private String responsiveImageLabelString;

	@Value("#{europeanaProperties['portal.shownAtProviderOverride']}")
	private String[] shownAtProviderOverride;

	// API settings
	@Value("#{europeanaProperties['api2.url']}")
	private String api2url;

	@Value("#{europeanaProperties['api2.key']}")
	private String api2key;

	@Value("#{europeanaProperties['api2.secret']}")
	private String api2secret;

	// Schema.org maping
	@Value("#{europeanaProperties['schema.org.mapping']}")
	private String schemaOrgMappingFile;
	
	@Value("#{europeanaProperties['imageCacheUrl']}")
	private String imageCacheUrl;

	@Value("#{europeanaProperties['portal.minCompletenessToPromoteInSitemaps']}")
	private int minCompletenessToPromoteInSitemaps;

	@Value("#{europeanaProperties['portal.contentchecker']}")
	private String isContentChecker;

	@Value("#{europeanaProperties['portal.rowLimit']}")
	private String rowLimit;

	/////////////////////////////// generated properties

	private Map<String, String> seeAlsoTranslations;

	private String portalUrl;

	/////////////////////////////// complex functions

	public void registerBaseObjects(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		this.request = request;
		this.response = response;
		this.locale = locale;
	}

	public void injectProperties(PortalPageData model) {
		model.setGooglePlusPublisherId(StringUtils.trimToEmpty(portalGooglePlusPublisherId));
		model.setTheme(getTheme(request));
		User user = ControllerUtil.getUser(userService);
		model.setUser(user);
	}

	/**
	 * Wraps the config interceptor's postHandle method.
	 * 
	 * @param object
	 *   A controller object
	 * @param page
	 *   The model and view object
	 */
	public void postHandle(Object object, ModelAndView page) {
		try {
			configInterceptor.postHandle(request, response, object, page);
		} catch (Exception e) {
			log.severe("Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private String getTheme(HttpServletRequest request) {
		return ControllerUtil.getSessionManagedTheme(request, defaultTheme);
	}

	///////////////////////////////// getters and setters

	public String getPortalName() {
		return portalName;
	}

	public String getPortalServer() {
		return portalServer;
	}

	public String[] getShownAtProviderOverride() {
		return shownAtProviderOverride;
	}

	public String getApi2url() {
		return api2url;
	}

	public String getApi2key() {
		return api2key;
	}

	public String getApi2secret() {
		return api2secret;
	}

	public String getSchemaOrgMappingFile() {
		return schemaOrgMappingFile;
	}

	public String getBlogUrl() {
		return blogUrl;
	}

	public int getBlogTimeout() {
		return blogTimeout.intValue();
	}

	public String getPintUrl() {
		return pintUrl;
	}

	public String getPintFeedUrl() {
		return pintFeedUrl;
	}

	public int getPintTimeout() {
		return pintTimeout.intValue();
	}

	public int getPintItemLimit() {
		return pintItemLimit.intValue();
	}

	public String getDefaultTheme() {
		return defaultTheme;
	}

	public String getStaticPagePath() {
		return staticPagePath;
	}

	public String getResponsiveImageWidthString() {
		return responsiveImageWidthString;
	}

	public String getResponsiveImageLabelString() {
		return responsiveImageLabelString;
	}

	public static String getPortalGooglePlusPublisherId() {
		return portalGooglePlusPublisherId;
	}

	public String getImageCacheUrl() {
		return imageCacheUrl;
	}

	public int getMinCompletenessToPromoteInSitemaps() {
		return minCompletenessToPromoteInSitemaps;
	}

	public boolean isContentChecker() {
		return isContentChecker.equals("true");
	}

	public int getRowLimit() {
		return Integer.parseInt(rowLimit);
	}

	public Map<String, String> getSeeAlsoTranslations() {
		if (seeAlsoTranslations == null) {
			seeAlsoTranslations = new HashMap<String, String>();
			int i = 1;
			while (europeanaProperties.containsKey("portal.seeAlso.field." + i)) {
				String[] parts = europeanaProperties.getProperty("portal.seeAlso.field." + i).split("=", 2);
				seeAlsoTranslations.put(parts[0], parts[1]);
				i++;
			}
		}
		return seeAlsoTranslations;
	}
	
	public String getPortalUrl() {
		if (portalUrl == null) {
			StringBuilder sb = new StringBuilder(portalServer);
			if (!portalServer.endsWith("/") && !portalName.startsWith("/")) {
				sb.append("/");
			}
			sb.append(portalName);
			portalUrl = sb.toString();
		}
		return portalUrl;
	}
}
