package com.housair.bssm.security.access;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;

import com.housair.bssm.toolkit.util.JsonUtils;

public class CustomSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

	private static final Logger logger = LoggerFactory.getLogger(CustomSecurityMetadataSource.class);
	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		logger.info(JsonUtils.toJson(object));
		return null;
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		return null;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return FilterInvocation.class.isAssignableFrom(clazz);
	}

}
