package com.housair.bssm.controller;

import static com.housair.bssm.constants.ViewNameConstants.LOGIN_PAGE;
import static com.housair.bssm.constants.ViewNameConstants.REDIRECT_INDEX_PAGE;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@RequestMapping("/loginPage")
	public String loginPage() {
		return LOGIN_PAGE;
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView login(String userName, String password) {
		Subject user = SecurityUtils.getSubject();
		ModelAndView modelAndView = new ModelAndView(REDIRECT_INDEX_PAGE);
		try {
			UsernamePasswordToken token = new UsernamePasswordToken(userName, password);
			token.setRememberMe(true);
			user.login(token);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("登录失败！", e);
			modelAndView.setViewName(LOGIN_PAGE);
			modelAndView.addObject("errorMessage", "用户名或者密码错误!");
		}
		return modelAndView;
	}
	
}
