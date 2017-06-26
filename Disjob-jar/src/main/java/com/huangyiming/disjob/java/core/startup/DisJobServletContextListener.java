package com.huangyiming.disjob.java.core.startup;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.huangyiming.disjob.java.DisJobConstants;
import com.huangyiming.disjob.java.bean.StartUpConfig;
import com.huangyiming.disjob.java.core.dispatcher.EventObjectDispatcher;

public class DisJobServletContextListener implements ServletContextListener{

	public void contextDestroyed(ServletContextEvent event) {
		
		EventObjectDispatcher.dispatcherDisJobStop();
	}

	public void contextInitialized(ServletContextEvent event) {
		String disJobConfig = event.getServletContext().getInitParameter(DisJobConstants.Config.DISJOB_CONFIG_PATH);
		EventObjectDispatcher.dispatcherDisJobStartUp(new StartUpConfig(DisJobConstants.StartUpType.WEB_SERVLET_START_UP,disJobConfig));
	}
}
