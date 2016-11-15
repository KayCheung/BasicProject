package com.housair.bssm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.housair.bssm.service.TestService;

@Controller
@RequestMapping("testShow")
//@EnableAutoConfiguration
public class TestController {

	@Autowired
	private TestService testService;
	
	@RequestMapping
	public String testShow() {
		assert testService.test().equals("1");
		return "show";
	}

	public static void main(String[] args) {
//		SpringApplication.run(Application.class, args);
	}
	
}
