package com.housair.bssm.shiro.filter;

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.cas.CasFilter;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.housair.bssm.toolkit.util.JsonUtils;

public class CustomCasFilter extends CasFilter {

	private static final Logger logger = LoggerFactory.getLogger(CustomCasFilter.class);
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
			ServletResponse response) throws Exception {
		PrincipalCollection principalCollection = SecurityUtils.getSubject().getPrincipals();
		if(null != principalCollection) {
			List principals = principalCollection.asList();
			for (int i = 0; i < principals.size(); i++) {
				logger.info(JsonUtils.toJson(principals.get(i)));
				
				
				// 获取CAS返回的用户信息
//				Map<String, String> attributes = (Map<String, String>) principals.get(i);
//				if(null != attributes) {
//					Set<Entry<String, String>> entrySet = attributes.entrySet();
//					for (Iterator<Entry<String, String>> it = entrySet.iterator(); it.hasNext();) {
//						Entry<String, String> entry = it.next();
//						logger.info(entry.getKey() + " = " + entry.getValue());
//					}
//				}
			}
		}
		return super.onLoginSuccess(token, subject, request, response);
	}

}
