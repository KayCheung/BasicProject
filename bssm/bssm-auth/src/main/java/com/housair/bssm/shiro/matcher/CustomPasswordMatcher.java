package com.housair.bssm.shiro.matcher;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.cache.Cache;

import com.housair.bssm.shiro.cache.RedisCacheManager;

public class CustomPasswordMatcher extends PasswordMatcher {
	
	private Cache<String, AtomicInteger> passwordRetryCache;
	
	private int retryCount = 5;

	public CustomPasswordMatcher(RedisCacheManager redisCacheManager) {
		passwordRetryCache = redisCacheManager.getCache("passwordRetryCache");
	}
	
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	@Override
	public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {

		String username = (String) token.getPrincipal();
		// retry count + 1
		AtomicInteger _retryCount = passwordRetryCache.get(username);
		if (null == _retryCount) {
			passwordRetryCache.put(username, _retryCount);
		}
		if (_retryCount.incrementAndGet() > retryCount) {
			// if retry count > 5 throw
			throw new ExcessiveAttemptsException();
		}

		boolean matches = getPasswordService().passwordsMatch(getSubmittedPassword(token), (String) getStoredPassword(info));
		if (matches) {
			// clear retry count
			passwordRetryCache.remove(username);
		}
		return matches;
	}

}
