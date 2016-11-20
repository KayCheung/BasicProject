package com.housair.bssm.security.access;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;

import com.housair.bssm.toolkit.util.JsonUtils;

public class CustomAccessDecisionManager implements AccessDecisionManager {

	private static final Logger logger = LoggerFactory.getLogger(CustomAccessDecisionManager.class);
	
	@Override
	public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
			throws AccessDeniedException, InsufficientAuthenticationException {
		logger.info(JsonUtils.toJson(authentication));
		logger.info(JsonUtils.toJson(object));
		logger.info(JsonUtils.toJson(configAttributes));
	}

	@Override
	public boolean supports(ConfigAttribute attribute) {
		logger.info(JsonUtils.toJson(attribute));
		return true;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

}
