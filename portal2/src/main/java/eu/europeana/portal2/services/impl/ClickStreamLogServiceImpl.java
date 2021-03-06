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

package eu.europeana.portal2.services.impl;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.web.servlet.ModelAndView;

import eu.europeana.corelib.definitions.db.entity.relational.User;
import eu.europeana.corelib.definitions.solr.model.Query;
import eu.europeana.corelib.web.model.PageData;
import eu.europeana.corelib.web.model.PageInfo;
import eu.europeana.corelib.web.utils.RequestUtils;
import eu.europeana.portal2.services.ClickStreamLogService;
import eu.europeana.portal2.web.presentation.model.submodel.BriefBeanView;
import eu.europeana.portal2.web.presentation.model.submodel.DocIdWindowPager;
import eu.europeana.portal2.web.presentation.model.submodel.FullBeanView;
import eu.europeana.portal2.web.util.ControllerUtil;

/**
 * @author Sjoerd Siebinga <sjoerd.siebinga@gmail.com>
 */
public class ClickStreamLogServiceImpl implements ClickStreamLogService {

	private Logger log = Logger.getLogger(ClickStreamLogServiceImpl.class.getName());

	public static String VERSION = "2.0";

	@Override
	public void logUserAction(HttpServletRequest request, UserAction action, ModelAndView page) {
		log.info(MessageFormat.format("[action={0}, view={1}, {2}]", action, page.getViewName(), printLogAffix(request, page)));
	}

	/**
	 * This method is used the basic information from the
	 * <code>HttpServletRequest<code>
	 * (@See <code>printLogAffix</code> )
	 * 
	 * @param request
	 *            the HttpServletRequest from the controller
	 * @param action
	 *            the UserAction performed in the controller
	 */

	@Override
	public void logUserAction(HttpServletRequest request, UserAction action) {
		log.info(MessageFormat.format("[action={0}, {1}]", action, printLogAffix(request)));
	}

	@Override
	public void logCustomUserAction(HttpServletRequest request, UserAction action, String logString) {
		log.info(MessageFormat.format("[action={0}, {2}, {1}]", action, printLogAffix(request), logString));
	}

	@Override
	public void logStaticPageView(HttpServletRequest request, PageInfo pageType) {
		log.info(MessageFormat.format("[action={0}, view={1}, {2}]", UserAction.STATICPAGE, pageType.getTemplate(), printLogAffix(request)));
	}

	@Override
	public void logLanguageChange(HttpServletRequest request, Locale oldLocale, UserAction languageChange) {
		log.info(MessageFormat.format("[action={0}, oldLang={1}, {2}]", languageChange, oldLocale.getLanguage(), printLogAffix(request)));
	}

	@Override
	public void logBriefResultView(HttpServletRequest request, BriefBeanView briefBeanView, Query solrQuery, ModelAndView page) {
		// initial values
		int pageNr = 1;
		int nrResults = 0;
		String languageFacets = null;
		String countryFacet = null;
		String query = "";
		String queryConstraints = "";

		if (briefBeanView != null) {
			pageNr = briefBeanView.getPagination().getPageNumber();
			nrResults = briefBeanView.getPagination().getNumFound();
			languageFacets = briefBeanView.getFacetLogs().get("LANGUAGE");
			countryFacet = briefBeanView.getFacetLogs().get("COUNTRY");
			query = (briefBeanView.getPagination() == null) ? null : briefBeanView.getPagination().getPresentationQuery().getUserSubmittedQuery();
			if (solrQuery.getRefinements() != null) {
				queryConstraints = StringUtils.join(solrQuery.getRefinements(), ",");
			}
		}
		// String pageId;
		// private String state;
		UserAction userAction = UserAction.BRIEF_RESULT;
		Map<String, String[]> params = RequestUtils.getParameterMap(request); 
		if (params.containsKey("bt")) {
			if (request.getParameter("bt").equalsIgnoreCase("pacta")) {
				userAction = UserAction.BRIEF_RESULT_FROM_PACTA;
			} else if (request.getParameter("bt").equalsIgnoreCase("savedSearch")) {
				userAction = UserAction.BRIEF_RESULT_FROM_SAVED_SEARCH;
			}
		} else if (params.containsKey("rtr")
				&& request.getParameter("rtr").equalsIgnoreCase("true")) {
			userAction = UserAction.RETURN_TO_RESULTS;
		}
		log.info(MessageFormat.format(
			"[action={0}, view={1}, query={2}, queryType={7}, queryConstraints=\"{3}\", page={4}, numFound={5}, langFacet={8}, countryFacet={9}, {6}]",
			userAction, page.getViewName(), query, queryConstraints, pageNr, nrResults,
			printLogAffix(request, page), solrQuery.getQueryType(), languageFacets, countryFacet));
	}

	@Override
	public void logFullResultView(HttpServletRequest request, UserAction userAction, FullBeanView fullResultView, ModelAndView page, String europeanaUri) {
		String originalQuery = "";
		String startPage = "";
		String numFound = "";
		try {
			DocIdWindowPager idWindowPager = fullResultView.getDocIdWindowPager();
			originalQuery = idWindowPager.getQuery();
			startPage = String.valueOf(idWindowPager.getFullDocUriInt());
			numFound = idWindowPager.getDocIdWindow().getHitCount().toString();
		} catch (UnsupportedEncodingException e) {
			// TODO decide what to do with this error
		} catch (Exception e) {
			// TODO decide what to do with this error
		}

		Map<String, String[]> params = RequestUtils.getParameterMap(request); 
		if (params.containsKey("bt")) {
			if (request.getParameter("bt").equalsIgnoreCase("savedItem")) {
				userAction = UserAction.FULL_RESULT_FROM_SAVED_ITEM;
			} else if (request.getParameter("bt").equalsIgnoreCase("savedTag")) {
				userAction = UserAction.FULL_RESULT_FROM_SAVED_TAG;
			} else if (request.getParameter("bt").equalsIgnoreCase("bob")) {
				userAction = UserAction.FULL_RESULT_FROM_YEAR_GRID;
			} else if (request.getParameter("bt").equalsIgnoreCase("tlv")) {
				userAction = UserAction.FULL_RESULT_FROM_TIME_LINE_VIEW;
			}
		}
		log.info(MessageFormat.format("[action={0}, europeana_uri={2}, query={4}, start={3}, numFound={5}, {1}]",
				userAction, printLogAffix(request, page), europeanaUri, startPage, originalQuery, numFound));
	}

	private static String printLogAffix(HttpServletRequest request) {
		return printLogAffix(request, null);
	}

	private static String printLogAffix(HttpServletRequest request, ModelAndView page) {
		DateTime date = new DateTime();
		String ip = request.getRemoteAddr();
		String reqUrl = getRequestUrl(request);
		User user = null;
		if (page != null) {
			PageData model = (PageData)page.getModel().get(PageData.PARAM_MODEL);
			user = (User)model.getUser();
		}
		String userId;
		if (user != null) {
			userId = user.getId().toString();
		} else {
			userId = "";
		}
		String language = ControllerUtil.getLocale(request).toString();
		String userAgent = request.getHeader("User-Agent");
		String referer = request.getHeader("referer");
		Cookie[] cookies = request.getCookies();
		String utma = "";
		String utmb = "";
		String utmc = "";
		String trac_session = "";
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equalsIgnoreCase("__utma")) {
					utma = cookie.getValue();
				} else if (cookie.getName().equalsIgnoreCase("__utmb")) {
					utmb = cookie.getValue();
				} else if (cookie.getName().equalsIgnoreCase("__utmc")) {
					utmc = cookie.getValue();
				} else if (cookie.getName().equalsIgnoreCase("trac_session")) {
					trac_session = cookie.getValue();
				}
			}
		}

		return MessageFormat.format(
			"userId={0}, lang={1}, req={4}, date={2}, ip={3}, user-agent={5}, referer={6}, utma={8}, utmb={9}, utmc={10}, trac_session={11}, v={7}", 
			userId, language, date, ip, reqUrl, userAgent, referer, VERSION, utma, utmb, utmc, trac_session);
	}

	private static String getRequestUrl(HttpServletRequest request) {
		String base = ControllerUtil.getFullServletUrl(request);
		String queryStringParameters = request.getQueryString();
		Map<String, String[]> postParameters = RequestUtils.getParameterMap(request); 
		StringBuilder out = new StringBuilder();
		out.append(base);
		String queryString;
		if (queryStringParameters != null) {
			out.append("?").append(queryStringParameters);
			queryString = out.toString();
		} else if (postParameters.size() > 0) {
			out.append("?");
			for (Entry<String, String[]> entry : postParameters.entrySet()) {
				String key = entry.getKey().toString();
				String[] values = (String[]) entry.getValue();
				for (String value : values) {
					out.append(key).append("=").append(value).append("&");
				}
			}
			queryString = out.toString().substring(0, out.toString().length() - 1);
		} else {
			queryString = out.toString();
		}
		return queryString;
	}
}
