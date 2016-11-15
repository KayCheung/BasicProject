package com.housair.bssm.toolkit.xss;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.util.HtmlUtils;

/**
 * Created by zhangkai on 11/10/16.
 */
public class XSSEscapeMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    private static Logger logger = LoggerFactory.getLogger(XSSEscapeMappingJackson2HttpMessageConverter.class);

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        Object form =  super.read(type, contextClass, inputMessage);

        escapeFields(form);

        return form;

    }

    private void escapeFields(Object form) {

        boolean global = false;
        if (form.getClass().isAnnotationPresent(XSSEscapable.class)){
            global = true;
        }
        Field[] fields = form.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().isAssignableFrom(String.class)){
                if (global) {
                    escape(form, field);
                    continue;
                }
                if (field.isAnnotationPresent(XSSEscapable.class)){
                    escape(form, field);
                }
            }
        }
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        Object form = super.readInternal(clazz, inputMessage);

        escapeFields(form);

        return form;
    }

    private void escape(Object instance, Field field) {
        String name = field.getName();
        String getMethodName;
        String setMethodName;
        if (name.length() > 1) {
            String formatName = name.substring(0, 1).toUpperCase() + name.substring(1);
            getMethodName = "get" + formatName;
            setMethodName = "set" + formatName;
        }else {
            getMethodName = "get" + name.toUpperCase();
            setMethodName = "set" + name.toUpperCase();
        }
        try {
            Method getMethod = instance.getClass().getDeclaredMethod(getMethodName);
            Method setMethod = instance.getClass().getDeclaredMethod(setMethodName, String.class);
            Object value = getMethod.invoke(instance);
            if(value != null) {
                setMethod.invoke(instance, new String(HtmlUtils.htmlEscape(value.toString())));
            }
        } catch (Exception e) {
            logger.warn("转义字符失败.字段为:" + name, e);
        }
    }


}
