package com.housair.bssm.toolkit.xss;

import java.io.IOException;

import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.util.HtmlUtils;

public class XXSStringHttpMessageConverter extends StringHttpMessageConverter {

	@Override
	protected void writeInternal(String str, HttpOutputMessage outputMessage) throws IOException {
		super.writeInternal(HtmlUtils.htmlEscape(str), outputMessage); 
	}

}
