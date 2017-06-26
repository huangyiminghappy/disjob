/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.huangyiming.disjob.console.web.interceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.console.SystemDefault;
import com.huangyiming.disjob.register.center.pool.ConsoleCuratorClient;
import com.huangyiming.disjob.register.center.pool.ThreadLocalClient;
import com.huangyiming.disjob.monitor.db.domain.DBUser;

/**
 * 
 * @author Disjob
 *
 */
public final class CuratorClientInterceptor extends HandlerInterceptorAdapter {
	@Resource
	private ThreadLocalClient threadLocalClient;
	
    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
    	if(request.getSession().getAttribute(SystemDefault.USER_SESSION_KEY) == null){
    		throw new RuntimeException("登陆已失效!");
    	}
    	String username = request.getSession().getAttribute(SystemDefault.USER_SESSION_KEY) == null ? null : ((DBUser)request.getSession().getAttribute("currentUser")).getUsername();
    	threadLocalClient.setCuratorClient(username); 
    	ConsoleCuratorClient curatorClient = threadLocalClient.getCuratorClient();
        if (null == curatorClient || !curatorClient.isConnected()) {
        	LoggerUtil.error("get ZK client failed！");
            return false;
        }
        LoggerUtil.info("get ZK client success！");
        return true;
    } 
    
    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final ModelAndView modelAndView) throws Exception {
    	threadLocalClient.clear();
    }

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		super.afterCompletion(request, response, handler, ex);
	}
    
}
