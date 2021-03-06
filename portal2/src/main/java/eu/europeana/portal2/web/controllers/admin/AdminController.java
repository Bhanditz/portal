package eu.europeana.portal2.web.controllers.admin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.ImmutableMap;

import eu.europeana.corelib.db.service.ApiKeyService;
import eu.europeana.corelib.db.service.ApiLogService;
import eu.europeana.corelib.db.service.UserService;
import eu.europeana.corelib.definitions.db.entity.relational.ApiKey;
import eu.europeana.corelib.definitions.db.entity.relational.User;
import eu.europeana.corelib.logging.Log;
import eu.europeana.corelib.logging.Logger;
import eu.europeana.corelib.utils.DateIntervalUtils;
import eu.europeana.corelib.web.support.Configuration;
import eu.europeana.portal2.services.ClickStreamLogService;
import eu.europeana.portal2.web.presentation.PortalPageInfo;
import eu.europeana.portal2.web.presentation.model.AdminPage;
import eu.europeana.portal2.web.util.CSVUtils;
import eu.europeana.portal2.web.util.ControllerUtil;
import eu.europeana.portal2.web.util.MsExcelUtils;

/**
 * 
 * Show monitoring and administration info.
 * 
 * @author Borys Omelayenko
 */

@Controller
public class AdminController {


	@Resource
	private UserService userService;

	@Resource
	private Configuration config;

	@Resource
	private ApiKeyService apiKeyService;

	@Resource
	private ClickStreamLogService clickStreamLogger;

	@Resource
	private ApiLogService apiLogService;

	private static final String ACTUAL = "actual";
	private static final String TOTAL = "total";
	private static final int LIMIT = 50;

	private static List<Map<String, String>> header = new LinkedList<Map<String, String>>();
	static {
		header.add(new ImmutableMap.Builder<String, String>().put("name", "count").put("type", "int").build());
		header.add(new ImmutableMap.Builder<String, String>().put("name", "id").put("type", "int").build());
		header.add(new ImmutableMap.Builder<String, String>().put("name", "Date of registration").put("type", "String").build());
		header.add(new ImmutableMap.Builder<String, String>().put("name", "First name").put("type", "String").build());
		header.add(new ImmutableMap.Builder<String, String>().put("name", "Last name").put("type", "String").build());
		header.add(new ImmutableMap.Builder<String, String>().put("name", "Email").put("type", "String").build());
		header.add(new ImmutableMap.Builder<String, String>().put("name", "Company").put("type", "String").build());
		header.add(new ImmutableMap.Builder<String, String>().put("name", "Country").put("type", "String").build());
		header.add(new ImmutableMap.Builder<String, String>().put("name", "Address").put("type", "String").build());
		header.add(new ImmutableMap.Builder<String, String>().put("name", "Phone").put("type", "String").build());
		header.add(new ImmutableMap.Builder<String, String>().put("name", "Website").put("type", "String").build());
		header.add(new ImmutableMap.Builder<String, String>().put("name", "Field of work").put("type", "String").build());
		header.add(new ImmutableMap.Builder<String, String>().put("name", "Number of keys").put("type", "int").build());
		header.add(new ImmutableMap.Builder<String, String>().put("name", "Key").put("type", "String").build());
		header.add(new ImmutableMap.Builder<String, String>().put("name", "Limit").put("type", "int").build());
		header.add(new ImmutableMap.Builder<String, String>().put("name", "Total usage").put("type", "int").build());
		header.add(new ImmutableMap.Builder<String, String>().put("name", "Current usage").put("type", "int").build());
	}

	@RequestMapping("/admin.html")
	public ModelAndView adminHandler(
			@RequestParam(value = "page", required = false, defaultValue = "1") int pageNr
			) throws Exception {

		int offset = ((pageNr - 1) * LIMIT);
		AdminPage model = new AdminPage();
		model.setPageNr(pageNr);

		long t0 = new Date().getTime();

		Map<String, Map<String, Long>> usage = new HashMap<String, Map<String, Long>>();
		usage.put(ACTUAL, new HashMap<String, Long>());
		usage.put(TOTAL, new HashMap<String, Long>());

		List<User> users = new ArrayList<User>();
		long count = apiKeyService.count();
		model.setApiKeyCount(count);

		List<ApiKey> apiKeys = apiKeyService.findAllSortByDate(false, offset, LIMIT);
		for (ApiKey apiKey : apiKeys) {
			User user = apiKey.getUser();
			if (!users.contains(user)) {
				users.add(user);
			}
		}

		// Requests API key statistics
		for (User user : users) {
			for (ApiKey apiKey : user.getApiKeys()) {
				getUsageByApiKey(usage, apiKey.getId());
			}
		}

		long t = (new Date().getTime() - t0);
		model.setUsers(users);
		model.setUsage(usage);
		List<Integer> pageNumbers = new ArrayList<Integer>();
		for (int i = 1; ((i - 1) * LIMIT) + 1 < count; i++) {
			pageNumbers.add(i);
		}
		model.setPageNumbers(pageNumbers);

		ModelAndView page = ControllerUtil.createModelAndViewPage(model, PortalPageInfo.ADMIN);
		return page;
	}

	@RequestMapping("/admin/limitInfo.json")
	public void limitInfoHandler(
			@RequestParam(value = "apiKey", required = false, defaultValue = "") String apiKey,
			HttpServletResponse response)
					throws Exception {
		response.setContentType("application/json");
		long[] usage = new long[]{0, 0};
		if (!StringUtils.isBlank(apiKey)) {
			usage = getUsageByApiKey(apiKey);
		}
		ServletOutputStream out = response.getOutputStream();
		out.print(String.format("{\"actual\": %d, \"total\": %d}", usage[0], usage[1]));
		out.flush();
	}

	@RequestMapping("/admin/removeUser.html")
	public String removeUserHandler(
			@RequestParam(value = "id", required = true) long id
			) throws Exception {

		User user = userService.findByID(id);
		userService.remove(user);

		return "redirect:/admin.html";
	}

	/*
	 * @RequestMapping("/admin/blockUser.html") public String blockUserHandler(
	 * 
	 * @RequestParam(value = "id", required = true) int id, HttpServletRequest request, HttpServletResponse response,
	 * Locale locale) throws Exception { log.info("==== admin.html ====");
	 * 
	 * User user = userService.findByID(id); user.setEnabled(false); userService.store(user);
	 * 
	 * return "redirect:/admin.html"; }
	 */

	@RequestMapping("/admin/removeApiKey.html")
	public String removeApiKeyHandler(
			@RequestParam(value = "userId", required = true) long userId,
			@RequestParam(value = "apiKey", required = true) String apiKey
			) throws Exception {
		Logger.getLogger(this.getClass().getName()).info(String.format("%s, %s", userId, apiKey));

		apiKeyService.removeApiKey(userId, apiKey);

		return "redirect:/admin.html";
	}

	/**
	 * Export API users to comma separated list
	 * 
	 * @param request
	 * @param response
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/admin/export/users.csv", produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody String exportUsersHandler(HttpServletResponse response) throws Exception {
		response.setHeader("Content-Type", "text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=\"users_with_apikeys.csv\"");

		Map<Long, User> users = new LinkedHashMap<Long, User>();
		List<ApiKey> apiKeys = apiKeyService.findAllSortByDate(true);
		for (ApiKey apiKey : apiKeys) {
			User user = apiKey.getUser();
			if (!users.containsKey(user.getId())) {
				users.put(user.getId(), user);
			}
		}

		StringBuilder sb = new StringBuilder();
		List<String> fieldNames = new ArrayList<String>();
		for (Map<String, String> entry : header) {
			fieldNames.add(entry.get("name"));
		}
		sb.append(CSVUtils.encodeRecord(fieldNames));
		int count = 0;
		for (User user : users.values()) {
			sb.append(CSVUtils.encodeRecords(csvEncodeUser(count++, user)));
		}
		return sb.toString();
	}

	/**
	 * Export API users to comma separated list
	 * 
	 * @param request
	 * @param response
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/admin/export/users.xls")
	public ModelAndView exportUsersInExcelHandler(HttpServletResponse response) throws Exception {

		Map<Long, User> users = new LinkedHashMap<Long, User>();
		List<ApiKey> apiKeys = apiKeyService.findAllSortByDate(true);
		for (ApiKey apiKey : apiKeys) {
			User user = apiKey.getUser();
			if (!users.containsKey(user.getId())) {
				users.put(user.getId(), user);
			}
		}

		String title = "API users";

		List<List<String>> data = new ArrayList<List<String>>();
		int count = 1;
		for (User user : users.values()) {
			data.addAll(serializeUser(count++, user));
		}

		HSSFWorkbook workbook = new HSSFWorkbook();
		MsExcelUtils.build(title, header, data, workbook);
		MsExcelUtils.flush(response, "users_with_apikeys", workbook);

		return null;
	}

	/**
	 * Create an CSV encoded record (list of fields) from a User object
	 * 
	 * @param user
	 * @return
	 */
	private List<List<String>> csvEncodeUser(int count, User user) {
		List<List<String>> rows = serializeUser(count, user);

		for (List<String> fields : rows) {
			for (int i=1, size = fields.size(); i<size; i++) {
				fields.set(i, CSVUtils.encodeField(fields.get(i)));
			}
		}

		return rows;
	}

	/**
	 * Create an CSV encoded record (list of fields) from a User object
	 * 
	 * @param user
	 * @return
	 */
	private List<List<String>> serializeUser(int count, User user) {

		List<List<String>> rows = new LinkedList<List<String>>();
		List<String> fields = new LinkedList<String>();

		fields.add(String.valueOf(count));
		fields.add(user.getId().toString());
		if (user.getRegistrationDate() != null) {
			fields.add(new SimpleDateFormat("yyyy-MM-dd").format(user.getRegistrationDate()));
		} else {
			fields.add("");
		}
		fields.add(user.getFirstName());
		fields.add(user.getLastName());
		fields.add(user.getEmail());
		fields.add(user.getCompany());
		fields.add(user.getCountry());
		fields.add(user.getAddress());
		fields.add(user.getPhone());
		fields.add(user.getWebsite());
		fields.add(user.getFieldOfWork());
		fields.add(String.valueOf(user.getApiKeys().size()));

		int i = 0;
		for (ApiKey key : user.getApiKeys()) {
			long[] usage = getUsageByApiKey(key.getId());
			if (i > 0) {
				fields = new LinkedList<String>();
				for (int j = 0; j < 13; j++) {
					fields.add("");
				}
			}
			fields.add(key.getId());
			fields.add(String.valueOf(key.getUsageLimit()));
			fields.add(String.valueOf(usage[1]));
			fields.add(String.valueOf(usage[0]));
			rows.add(fields);
			i++;
		}

		return rows;
	}

	/**
	 * Gets and saves the total and actual usage by API key
	 * 
	 * @param usage
	 *            A map for collecting the usage statistics
	 * @param apiKey
	 *            The API key
	 */
	private void getUsageByApiKey(Map<String, Map<String, Long>> usage, String apiKey) {
		long actual = 0; // apiLogService.countByIntervalAndApiKey(DateIntervalUtils.getLast24Hours(), apiKey);
		long total = 0; // apiLogService.countByApiKey(apiKey);
		usage.get(ACTUAL).put(apiKey, actual);
		usage.get(TOTAL).put(apiKey, total);
	}

	private long[] getUsageByApiKey(String apiKey) {
		long actual = apiLogService.countByIntervalAndApiKey(DateIntervalUtils.getLast24Hours(), apiKey);
		long total = apiLogService.countByApiKey(apiKey);
		return new long[]{actual, total};
	}
}
