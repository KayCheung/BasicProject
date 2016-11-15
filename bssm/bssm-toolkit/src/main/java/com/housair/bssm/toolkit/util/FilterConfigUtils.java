package com.housair.bssm.toolkit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterConfig;

/**
 * Created by zhangkai on 11/10/16.
 */
public class FilterConfigUtils {

    private static Logger logger = LoggerFactory.getLogger(FilterConfigUtils.class);


    public static boolean getBoolean(FilterConfig config, String name) {
        return getBoolean(config, name, false);
    }

    public static boolean getBoolean(FilterConfig config, String name,  boolean defaultValue) {
        String value = config.getInitParameter(name);
        if (value == null) {
            return defaultValue;
        }
        try{
            return Boolean.valueOf(value).booleanValue();
        }catch (Exception e) {
            logger.error("获取配置异常: ", e);
            return defaultValue;
        }
    }

    public static String getString(FilterConfig config, String name) {
        return getString(config, name, null);
    }

    public static String getString(FilterConfig config, String name, String defaultValue) {
        if(config == null || name == null) {
            return defaultValue;
        }
        String value = config.getInitParameter(name);

        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}
