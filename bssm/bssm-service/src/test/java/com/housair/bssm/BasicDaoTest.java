package com.housair.bssm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.housair.bssm.service.TestService;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:config/spring/spring-beans.xml"})
public class BasicDaoTest extends TestCase {

	@Autowired
	private TestService testService;
	
	@Test
	public void test() {
		testService.test();
	}
	
}
