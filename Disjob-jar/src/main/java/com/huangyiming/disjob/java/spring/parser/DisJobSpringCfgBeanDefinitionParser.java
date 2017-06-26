package com.huangyiming.disjob.java.spring.parser;

import java.lang.reflect.Field;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.huangyiming.disjob.java.core.startup.DisJobSpringStartUp;
import com.huangyiming.disjob.java.utils.StringUtils;
 
public class DisJobSpringCfgBeanDefinitionParser implements BeanDefinitionParser {
	
	private final static Class<DisJobSpringStartUp> clazz = DisJobSpringStartUp.class;
	
	private void doParse(Element element, ParserContext parserContext,RootBeanDefinition startupBean) {
		Field[] fields = clazz.getDeclaredFields();
		for(Field field : fields){
			String fieldName = field.getName();
			String attributeVal = element.getAttribute(fieldName).trim();
			
			if("zkhost".equals(fieldName)&&StringUtils.isEmpty(attributeVal)){
				throw new IllegalArgumentException(fieldName+" must be set a value");
			}
			
			if(StringUtils.isEmpty(attributeVal)){
				continue;
			}
			
			startupBean.getPropertyValues().addPropertyValue(fieldName, attributeVal);
		}
	}

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition startupBean = new RootBeanDefinition();
		doParse(element, parserContext,startupBean);
		startupBean.setBeanClassName("com.huangyiming.disjob.java.core.startup.DisJobSpringStartUp");
		startupBean.setBeanClass(DisJobSpringStartUp.class);
		startupBean.setLazyInit(false);
		parserContext.getRegistry().registerBeanDefinition("startupBean", startupBean);
		return null;
	}

}
