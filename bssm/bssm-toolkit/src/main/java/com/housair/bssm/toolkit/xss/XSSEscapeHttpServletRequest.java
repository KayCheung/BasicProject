package com.housair.bssm.toolkit.xss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.web.util.HtmlUtils;

/**
 * Created by zhangkai on 11/10/16.
 */
public class XSSEscapeHttpServletRequest extends HttpServletRequestWrapper {

    private HttpServletRequest request;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request
     * @throws IllegalArgumentException if the request is null
     */
    public XSSEscapeHttpServletRequest(HttpServletRequest request) {
        super(request);
        this.request = request;
    }

    @Override
    public String getParameter(String name) {
        if (name == null) {
            return null;
        }
        if (this.request == null) {
            return null;
        }
        String parameter = request.getParameter(name);
        return escape(parameter);
    }


    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return values;
        }
        for(int i = 0; i < values.length; i++) {
            String value = values[i];
            values[i] = escape(value);
        }
        return values;
    }

    private String escape(String value) {

        if (value == null) {
            return value;
        }

        return new String(HtmlUtils.htmlEscape(value));
    }
}
