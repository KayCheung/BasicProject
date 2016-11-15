package com.housair.bssm.toolkit.xss;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;

import com.housair.bssm.toolkit.util.FilterConfigUtils;

/**
 * Created by zhangkai on 11/10/16.
 */
public class XSSEscapeFilter implements Filter {

    private static Logger logger = LoggerFactory.getLogger(XSSEscapeFilter.class);

    private List<String> excludeUrlList = new ArrayList<>();

    public static final String COMMA = ",";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    	
        String excludeUrlListStr = FilterConfigUtils.getString(filterConfig, "excludeUrlList");
        if (excludeUrlListStr != null && !excludeUrlListStr.trim().equals("")) {
            String[] urlListArray = excludeUrlListStr.split(COMMA);
            if (urlListArray != null) {
                for (String url : urlListArray) {
                    if (url != null){
                        logger.info("escape exclude url:" + url);
                        excludeUrlList.add(url.trim());
                    }

                }
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if (RequestMethod.POST.name().equals(req.getMethod()) && !excludeUrlList.contains(req.getRequestURI())) {
            XSSEscapeHttpServletRequest escapeRequest = new XSSEscapeHttpServletRequest(req);
            chain.doFilter(escapeRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
