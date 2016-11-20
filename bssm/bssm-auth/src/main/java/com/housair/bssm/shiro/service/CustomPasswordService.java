package com.housair.bssm.shiro.service;

import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.shiro.authc.credential.PasswordService;
import org.springframework.util.StringUtils;

import com.housair.bssm.toolkit.util.ThreadLocalUtils;

public class CustomPasswordService implements PasswordService {

	@Override
	public String encryptPassword(Object plaintextPassword) throws IllegalArgumentException {
		String salt = (String) ThreadLocalUtils.get("username");
		if(plaintextPassword instanceof char[]) {
			plaintextPassword = new String((char[])plaintextPassword);
		}
		return Md5Crypt.apr1Crypt(plaintextPassword.toString().getBytes(), salt);
	}

	@Override
	public boolean passwordsMatch(Object submittedPlaintext, String encrypted) {
		if (StringUtils.isEmpty(encrypted)) {
            return StringUtils.isEmpty(submittedPlaintext);
        } else {
            if (StringUtils.isEmpty(submittedPlaintext)) {
                return false;
            }
        }
		
		String computed = encryptPassword(submittedPlaintext);
		
		return encrypted.equals(computed);
	}

}
