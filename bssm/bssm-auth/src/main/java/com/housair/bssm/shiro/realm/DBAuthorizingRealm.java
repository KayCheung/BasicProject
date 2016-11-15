package com.housair.bssm.shiro.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.housair.bssm.toolkit.util.ThreadLocalUtils;

/**
 * 
 * @author zhangkai
 *
 */
public class DBAuthorizingRealm extends AuthorizingRealm {

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		return null;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
		String username = usernamePasswordToken.getUsername();
//		usernamePasswordToken.getPassword();
		
		// username 缓存到 ThreadLocal中
		ThreadLocalUtils.set("username", username);
		String pwdInDB = ((PasswordMatcher)getCredentialsMatcher()).getPasswordService().encryptPassword("123456");
		return new SimpleAuthenticationInfo(username, pwdInDB, getName());
	}

}
