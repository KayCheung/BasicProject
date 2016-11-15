package com.housair.bssm.dao;

import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:config/spring-ds.xml"})
public class BasicDaoTest extends TestCase {

	@Autowired
	private SqlSession sqlSession;
	
	@Test
	public void testQuery() {
		Assert.assertEquals(sqlSession.getMapper(TestMapper.class).testQuery(), "1");
	}
	
}
