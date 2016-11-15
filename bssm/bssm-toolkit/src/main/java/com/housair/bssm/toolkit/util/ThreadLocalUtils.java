package com.housair.bssm.toolkit.util;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * 
 * @author zhangkai
 *
 */
public abstract class ThreadLocalUtils {

	private static final ThreadLocal<Map<String, Object>> threadLocalHolder = new ThreadLocal<Map<String, Object>>();
	
	public static void set(String key, Object value) {
		Map<String, Object> maps = threadLocalHolder.get();
		if(null == maps) {
			maps = Maps.newHashMap();
			threadLocalHolder.set(maps);
		}
		maps.put(key, value);
	}
	
	public static Object get(String key) {
		Map<String, Object> maps = threadLocalHolder.get();
		if(null == maps) {
			return null;
		}
		return maps.get(key);
	}
	
}
