package com.huangyiming.disjob.java.spring.handle;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.huangyiming.disjob.java.spring.parser.DisJobSpringCfgBeanDefinitionParser;

public class DisJobGroupNamespaceHandler extends NamespaceHandlerSupport {
	public void init() {
		registerBeanDefinitionParser("cfg", new DisJobSpringCfgBeanDefinitionParser());
 	}

}
