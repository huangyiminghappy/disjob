package com.huangyiming.disjob.java.spring;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

import com.huangyiming.disjob.java.core.dispatcher.EventObjectDispatcher;

public class DisJobAnnoBeanNameGenerator extends AnnotationBeanNameGenerator {

	@Override
	protected String buildDefaultBeanName(BeanDefinition definition) {
		String className = definition.getBeanClassName();
		EventObjectDispatcher.dispatcherSpringRegisterJob(className);
		return className;
	}
	
	@Override
	protected String determineBeanNameFromAnnotation(AnnotatedBeanDefinition annotatedDef) {
		String className = annotatedDef.getBeanClassName();
		EventObjectDispatcher.dispatcherSpringRegisterJob(className);
		return className;
	}
	
}
