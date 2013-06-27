package eu.europeana.portal2.web.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import eu.europeana.corelib.definitions.solr.beans.BriefBean;
import eu.europeana.corelib.definitions.solr.model.Query;
import eu.europeana.corelib.logging.Log;
import eu.europeana.corelib.solr.exceptions.SolrTypeException;
import eu.europeana.corelib.solr.service.SearchService;
import eu.europeana.corelib.web.model.PageInfo;
import eu.europeana.corelib.web.utils.RequestUtils;
import eu.europeana.portal2.web.presentation.PortalPageInfo;
import eu.europeana.portal2.web.presentation.model.BriefBeanView;
import eu.europeana.portal2.web.presentation.model.SitemapPage;
import eu.europeana.portal2.web.presentation.model.data.decorators.BriefBeanDecorator;
import eu.europeana.portal2.web.util.ControllerUtil;
import eu.europeana.portal2.web.util.Injector;
import eu.europeana.portal2.web.util.SearchUtils;
import eu.europeana.portal2.web.util.abstracts.ClickStreamLogger;

@Controller
public class BrowsePageController {

	@Log
	private Logger log;

	@Resource
	private SearchService searchService;

	@Resource
	private ClickStreamLogger clickStreamLogger;

	@Value("#{europeanaProperties['portal.minCompletenessToPromoteInSitemaps']}")
	private int minCompletenessToPromoteInSitemaps;

	@RequestMapping("/browse-all.html")
	public ModelAndView browseAll(@RequestParam(value = "prefix", required = false) String prefix,
			HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		log.info("=========== browse-all.html ===========");

		Injector injector = new Injector(request, response, locale);

		SitemapPage<BriefBeanDecorator> model = new SitemapPage<BriefBeanDecorator>();
		injector.injectProperties(model);

		PageInfo pageInfo = PortalPageInfo.SITEMAP_BROWSE_INDEX;
		if (StringUtils.isEmpty(prefix)) {
			// index page
			prefix = "";
		} else {
			if (prefix.length() >= 4) {
				// landing page
				prefix = prefix.substring(0, 4);
				pageInfo = PortalPageInfo.SITEMAP_BROWSE_LANDING;
				Query query = new Query(
						SitemapController
								.solrQueryClauseToIncludeRecordsToPromoteInSitemaps(minCompletenessToPromoteInSitemaps))
						.setRefinements("id6hash:" + prefix + "*").setPageSize(1000);

				BriefBeanView briefBeanView = null;
				Class<? extends BriefBean> clazz = BriefBean.class;
				Map<String, String[]> params = RequestUtils.getParameterMap(request); 

				try {
					briefBeanView = SearchUtils.createResults(searchService, clazz, "portal", query, 0, 1000, params);
					List<BriefBeanDecorator> beans = new ArrayList<BriefBeanDecorator>();
					int index = model.getStart();
					for (BriefBean briefDoc : briefBeanView.getBriefBeans()) {
						beans.add(new BriefBeanDecorator(model, briefDoc, index++));
					}
					model.setResults(beans);
					log.info("results: " + model.getResults().size());
				} catch (SolrTypeException e) {
					log.error("SolrTypeException: " + e.getMessage(),e);
				} catch (Exception e) {
					log.error("Exception: " + e.getMessage(),e);
				}

			}
		}

		makePageObject(model, prefix);
		ModelAndView page = ControllerUtil.createModelAndViewPage(model, pageInfo);

		injector.postHandle(this, page);
		clickStreamLogger.logUserAction(request, ClickStreamLogger.UserAction.BROWSE_ALL, page);

		return page;
	}

	public static void makePageObject(SitemapPage<?> model, String prefix) {
		int clickNr = prefix.length() / 2;
		StringBuilder sb = new StringBuilder("Get to every object page in three clicks");
		sb.append((clickNr > 0 ? (", click " + clickNr) : ""));
		sb.append(". This is a randomly generated group of pages");
		model.setTitle(sb.toString());
		model.setPrefix(prefix);
	}

}
