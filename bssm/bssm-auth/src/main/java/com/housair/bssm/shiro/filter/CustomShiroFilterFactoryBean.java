package com.housair.bssm.shiro.filter;

import org.apache.shiro.config.Ini;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.config.IniFilterChainResolverFactory;

/**
 * 
 * @author zhangkai
 *
 */
public class CustomShiroFilterFactoryBean extends ShiroFilterFactoryBean {

	/**
	 * 重写，用来配置权限
	 */
	@Override
	public void setFilterChainDefinitions(String definitions) {
		Ini ini = new Ini();
        ini.load(definitions);
        //did they explicitly state a 'urls' section?  Not necessary, but just in case:
        Ini.Section section = ini.getSection(IniFilterChainResolverFactory.URLS);
        if (CollectionUtils.isEmpty(section)) {
            //no urls section.  Since this _is_ a urls chain definition property, just assume the
            //default section contains only the definitions:
            section = ini.getSection(Ini.DEFAULT_SECTION_NAME);
        }
        setFilterChainDefinitionMap(section);
	}
	
}
