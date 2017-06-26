package com.huangyiming.disjob.console.web.filter;

import java.io.IOException;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.common.util.SpringWorkFactory;
import com.huangyiming.disjob.console.SystemDefault;
import com.huangyiming.disjob.monitor.db.service.PermitService;
import com.huangyiming.disjob.monitor.db.domain.DBUser;

public class OperateLogFilter implements Filter{

	private static final int max_param_length = 500;
	
	@Override
	public void destroy() {
		
	}

	private PermitService permitService;
	
	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		try {
			if(arg0 instanceof HttpServletRequest){
				HttpServletRequest request = (HttpServletRequest)arg0;
				Object user_session_obj = request.getSession().getAttribute(SystemDefault.USER_SESSION_KEY);
				if(user_session_obj != null && user_session_obj instanceof DBUser){
					DBUser dbUser = (DBUser)user_session_obj;
					java.util.Map<String, String[]> map = request.getParameterMap();
					String parameters = null;
					for(Entry<String, String[]> entry : map.entrySet()){
						String key = entry.getKey();
						String[] values = entry.getValue();
						String value = StringUtils.join(values, ",");
						if(parameters == null){
							parameters = key + "=" + value;
						}else{
							parameters = StringUtils.join(new String[]{parameters,key + " = " + value},";");
						}
					}
					String host = request.getRemoteHost();
					String addr = request.getRemoteAddr();
					if(parameters != null && parameters.length() > max_param_length){
						parameters = parameters.substring(0, max_param_length);
					}
					permitService.createUserActionRecord(dbUser.getUsername(), request.getPathInfo(), parameters, host, addr);					
				}
			}
		} catch (Exception e) {
			LoggerUtil.error("log user action got an exception " , e);
		}
		arg2.doFilter(arg0, arg1);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		permitService = (PermitService) SpringWorkFactory.getWorkObject("permitService");
	}

}
