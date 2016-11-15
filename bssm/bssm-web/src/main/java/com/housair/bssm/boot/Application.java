package com.housair.bssm.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@ImportResource(locations = {"classpath:config/spring/applicationContext-placeholder.xml",
							 "classpath:config/spring/spring-mvc.xml", 
							 "classpath:config/spring/spring-beans.xml",
							 "classpath:config/spring/spring-redis.xml",
							 "classpath:config/spring/spring-shiro.xml"})
public class Application extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
