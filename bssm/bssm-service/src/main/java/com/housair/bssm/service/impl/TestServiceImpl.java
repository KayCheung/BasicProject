package com.housair.bssm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.housair.bssm.dao.TestMapper;
import com.housair.bssm.service.TestService;

@Service
public class TestServiceImpl implements TestService {

	@Autowired
	private TestMapper testMapper;
	
	@Override
	public String test() {
		return testMapper.testQuery();
	}

}
