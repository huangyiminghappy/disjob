package com.huangyiming.disjob.java.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JobDec {
	String group();
	String jobName();
	String quartz() default "";//quartz 表达式
	boolean fireNow() default false; //在配置了 cron 的基础上，如果需要立即执行，则设置为true,默认是不立即执行的
}
