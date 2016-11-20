package com.housair.bssm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.housair.bssm.constants.ViewNameConstants.*;

@Controller
public class ExceptionController {

	@RequestMapping("/error/403")
	public String noPermissions() {
		return NO_PERMISSIONS_PAGE;
	}
	
	@RequestMapping("/error/404")
	public String pageNotFound() {
		return PAGE_NOT_FOUND_PAGE;
	}
	
	@RequestMapping("/error/500")
	public String serverError() {
		return SERVER_ERROR_PAGE;
	}
	
	@RequestMapping("/casFailure")
	public String casFailure() {
		return CAS_FAILURE_PAGE;
	}
	
}
