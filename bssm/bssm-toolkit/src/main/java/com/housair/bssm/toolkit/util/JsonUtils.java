package com.housair.bssm.toolkit.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public abstract class JsonUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
	
	private static final Gson gson = new Gson();
	
	private static ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
    
    public static String toJson(Object value) {
		return gson.toJson(value);
    }
    
    public static <T> T toObject(String value, Class<T> type) {
    	return gson.fromJson(value, type);
    }
    
    public static <T> T toObject(String value, TypeReference<T> type) {
    	try {
			return objectMapper.readValue(value, type);
		} catch (IOException e) {
			logger.error("JSON: 转对象失败...", e);
			return null;
		}
    }

}
