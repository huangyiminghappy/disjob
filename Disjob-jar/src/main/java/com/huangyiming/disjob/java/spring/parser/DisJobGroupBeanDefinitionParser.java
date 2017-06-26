package com.huangyiming.disjob.java.spring.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.huangyiming.disjob.java.CronExpression;
import com.huangyiming.disjob.java.bean.JobInfo;
import com.huangyiming.disjob.java.spring.bean.DisJobGroup;
import com.huangyiming.disjob.java.utils.StringUtils;

/**
 * 解析组标签
 * @author Disjob
 *
 */
public class DisJobGroupBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
	public static Set<String> sers = new HashSet<String>();
	protected Class<DisJobGroup> getBeanClass(Element element) {
		return DisJobGroup.class;
	}

	protected void doParse(Element element, BeanDefinitionBuilder bean) {
		String name = element.getAttribute("name");
 		String id = element.getAttribute("id");
		NodeList elementSons = element.getChildNodes();
		if (!StringUtils.isEmpty(id)) {
			bean.addPropertyValue("id", id);
			sers.add(id);
		}
		if (!StringUtils.isEmpty(name)) {
			bean.addPropertyValue("name", name);
		}
		 
		if (elementSons == null || elementSons.getLength() < 0){
			return ;
		}

		List<JobInfo> list = new ArrayList<JobInfo>();
		for (int i = 0; i < elementSons.getLength(); i++) {
			Node node = elementSons.item(i);
			if (node instanceof Element == false) {
				continue ;
			}
			if (!"job".equals(node.getNodeName()) && !"job".equals(node.getLocalName())) {
				continue ;
			}
			String jobName = ((Element) node).getAttribute("name");
			String groupName = ((Element) node).getAttribute("group");
			if (StringUtils.isEmpty(groupName)) {// job没设置group则默认使用父标签的groupname
				groupName = name;
			}
			String classname = ((Element) node).getAttribute("classname");
			String cron = ((Element) node).getAttribute("cron");
			if(!checkCron(cron)){
				throw new IllegalArgumentException(groupName +"; "+jobName+"; cron express is invalidata.[cron="+cron+"]");
			}
			String fireNowStr = ((Element) node).getAttribute("fireNow");
			boolean fireNow = Boolean.valueOf(StringUtils.isEmpty(fireNowStr)? "false":fireNowStr);
			fireNow = (checkCron(cron) ? fireNow : false);
			JobInfo disJob = new JobInfo(classname,groupName,jobName,cron,fireNow);
			list.add(disJob);
		}
		bean.addPropertyValue("jobList", list);
	}
	
	private boolean checkCron(String cron){
		if(StringUtils.isEmpty(cron)){
			return true ;
		}
		return CronExpression.isValidExpression(cron);
	}
	
}
