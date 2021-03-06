package eu.europeana.portal2.web.presentation.model;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import eu.europeana.corelib.definitions.db.entity.relational.User;
import eu.europeana.portal2.web.presentation.model.data.AdminData;

public class AdminPage extends AdminData {

	private List<User> users;

	private Map<String, Map<String, Long>> usage;

	private long apiKeyCount;

	private int pageNr;

	private List<Integer> pageNumbers;

	/**
	 * Returns the total amount of memory in the Java virtual machine. The value 
	 * returned by this method may vary over time, depending on the host environment.
	 * Note that the amount of memory required to hold an object of any given type may 
	 * be implementation-dependent.
	 * 
	 * @return
	 */
	public String getTotalMemory() {
		return new DecimalFormat("#,###,###,###").format(Runtime.getRuntime().totalMemory());
	}

	/**
	 * Returns the amount of free memory in the Java Virtual Machine. Calling the gc 
	 * method may result in increasing the value returned by freeMemory.
	 * 
	 * @return
	 */
	public String getFreeMemory() {
		return new DecimalFormat("#,###,###,###").format(Runtime.getRuntime().freeMemory());
	}

	/**
	 * Returns the maximum amount of memory that the Java virtual machine will attempt 
	 * to use. If there is no inherent limit then the value java.lang.Long.MAX_VALUE will 
	 * be returned.
	 * 
	 * @return
	 */
	public String getMaxMemory() {
		return new DecimalFormat("#,###,###,###").format(Runtime.getRuntime().maxMemory());
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public Map<String, Map<String, Long>> getUsage() {
		return usage;
	}

	public void setUsage(Map<String, Map<String, Long>> usage) {
		this.usage = usage;
	}

	public int getPageNr() {
		return pageNr;
	}

	public void setPageNr(int pageNr) {
		this.pageNr = pageNr;
	}

	public List<Integer> getPageNumbers() {
		return pageNumbers;
	}

	public void setPageNumbers(List<Integer> pageNumbers) {
		this.pageNumbers = pageNumbers;
	}

	public long getApiKeyCount() {
		return apiKeyCount;
	}

	public void setApiKeyCount(long apiKeyCount) {
		this.apiKeyCount = apiKeyCount;
	}
}
