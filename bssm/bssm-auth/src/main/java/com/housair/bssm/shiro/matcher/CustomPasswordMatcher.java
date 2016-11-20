package com.housair.bssm.shiro.matcher;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.springframework.util.SerializationUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.housair.bssm.toolkit.redis.IRedisClient;
import com.housair.bssm.toolkit.util.ThreadLocalUtils;

public class CustomPasswordMatcher extends PasswordMatcher {
	
	private IRedisClient redisClient;
	
	private int retryCount = 5;
	
	private int expireTime = 7200000;

	public CustomPasswordMatcher(IRedisClient redisClient) {
		this.redisClient = redisClient;
	}
	
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public void setExpireTime(int expireTime) {
		this.expireTime = expireTime;
	}

	@Override
	public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {

		String username = (String) token.getPrincipal();
		// retry count + 1
		AtomicInteger _retryCount = redisClient.get("passwordRetryCache." + username, "shiro.cahce", new TypeReference<AtomicInteger>() {
		});
		if (null == _retryCount) {
			_retryCount = new AtomicInteger(0);
		}
		if (_retryCount.incrementAndGet() > retryCount) {
			// if retry count > 5 throw
			throw new ExcessiveAttemptsException();
		}

		redisClient.set("passwordRetryCache." + username, "shiro.cahce", SerializationUtils.serialize(_retryCount), expireTime);
		ThreadLocalUtils.set("username", username);
		boolean matches = getPasswordService().passwordsMatch(getSubmittedPassword(token), (String) getStoredPassword(info));
		if (matches) {
			// clear retry count
			redisClient.del("passwordRetryCache." + username, "shiro.cahce");
		}
		return matches;
	}

}
