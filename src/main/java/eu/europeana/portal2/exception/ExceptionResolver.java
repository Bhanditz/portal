package eu.europeana.portal2.exception;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import eu.europeana.corelib.definitions.exception.ProblemType;
import eu.europeana.corelib.solr.exceptions.EuropeanaQueryException;
import eu.europeana.corelib.web.service.EmailService;
import eu.europeana.portal2.services.Configuration;
import eu.europeana.portal2.web.presentation.PortalPageInfo;
import eu.europeana.portal2.web.presentation.model.ExceptionPage;
import eu.europeana.portal2.web.util.ControllerUtil;

public class ExceptionResolver implements HandlerExceptionResolver {

	@Resource(name="configurationService") private Configuration config;

	@Resource(name = "corelib_web_emailService") private EmailService emailService;

	private final Logger log = Logger.getLogger(getClass().getName());

	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object object, Exception exception) {

		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		ProblemType problem = ProblemType.NONE;
		if (exception instanceof EuropeanaQueryException) {
			problem = ((EuropeanaQueryException) exception).getProblem();
		}

		String stackTrace = ExceptionUtils.getFullStackTrace(exception);

		switch (problem.getAction()) {
		case MAIL :
			try {
				StringBuilder message = new StringBuilder();
				message.append("hostName: ").append(request.getServerName()).append("\n");
				message.append("request: ").append(ControllerUtil.formatFullRequestUrl(request)).append("\n");
				message.append("stackTrace: ").append(stackTrace).append("\n");
				// model.put("cacheUrl", imageCacheUrl);
				message.append("portalName: ").append(config.getPortalName()).append("\n");
				message.append("agent: ").append(request.getHeader("User-Agent")).append("\n");
				message.append("referer: ").append(request.getHeader("referer")).append("\n");
				message.append("date: ").append(new DateTime()).append("\n");

				if (!config.getDebugMode()) {
					emailService.sendException(problem.getMessage(), message.toString());
				}
				else {
					log.severe(stackTrace);
				}
			}
			catch (Exception e) {
				log.warning("Unable to send exception email to system admin. " + e);
			}
			break;
		case LOG:
			log.warning(problem.getMessage() + "\n" + stackTrace);
			break;
		// ignore
		case IGNORE:
			break;
		}

		ExceptionPage model = new ExceptionPage();
		model.setException(exception);
		model.setProblem(problem);
		model.setStackTrace(stackTrace);
		model.setPortalName(config.getPortalName());
		// model.setCacheUrl(imageCacheUrl);
		model.setDebug(config.getDebugMode());

		return ControllerUtil.createModelAndViewPage(model, PortalPageInfo.EXCEPTION);
	}
}
