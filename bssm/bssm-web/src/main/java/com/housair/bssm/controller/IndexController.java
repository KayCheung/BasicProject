package com.housair.bssm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.housair.bssm.constants.ViewNameConstants.*;

@Controller
public class IndexController {
	
	@RequestMapping("/index")
	public String index() {
		return INDEX_PAGE;
	}

}
