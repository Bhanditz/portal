package eu.europeana.portal2.services.impl;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import eu.europeana.corelib.db.exception.DatabaseException;
import eu.europeana.corelib.db.service.ApiKeyService;
import eu.europeana.corelib.db.service.UserService;
import eu.europeana.corelib.definitions.db.entity.relational.ApiKey;
import eu.europeana.corelib.definitions.db.entity.relational.User;
import eu.europeana.corelib.logging.Log;
import eu.europeana.portal2.web.security.Portal2ClientDetails;
import eu.europeana.portal2.web.security.Portal2UserDetails;

public class UserDetailsServiceImpl implements UserDetailsService {

	@Log
	private Logger log;

	@Resource
	private ApiKeyService apiKeyService;

	@Resource
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String key) 
			throws UsernameNotFoundException {

		if (StringUtils.contains(key, "@")) {
			User user = userService.findByEmail(key);
			if (user != null) {
				log.info("Login with user name and password");
				return new Portal2UserDetails(user);
			}
		} else {
			try {
				ApiKey apiKey = apiKeyService.findByID(key);
				if (apiKey != null) {
					log.info("Login with api key");
					return new Portal2ClientDetails(apiKey);
				}
			} catch (DatabaseException e) {
				log.error("DatabaseException: " + e.getMessage(),e);
			}
		}
		throw new UsernameNotFoundException(key);
	}
}