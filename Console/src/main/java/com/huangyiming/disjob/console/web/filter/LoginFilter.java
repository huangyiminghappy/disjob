package com.huangyiming.disjob.console.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.huangyiming.disjob.common.model.Result;
import com.huangyiming.disjob.console.SystemDefault;
import com.google.gson.Gson;

public class LoginFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain fc)
			throws IOException, ServletException {
		if(req instanceof HttpServletRequest){
			HttpServletRequest httpServletRequest = (javax.servlet.http.HttpServletRequest)req;
			if(httpServletRequest.getSession().getAttribute(SystemDefault.USER_SESSION_KEY) != null || "/login".equals(httpServletRequest.getPathInfo())){
				fc.doFilter(req, resp);
			}else{
				if("XMLHttpRequest".equals(httpServletRequest.getHeader("X-Requested-With"))){
					ServletOutputStream os = resp.getOutputStream();
					os.write(new Gson().toJson(new Result(false, "登陆已失效")).getBytes(SystemDefault.CHARSET));
					os.flush();
				}else
					httpServletRequest.getRequestDispatcher("/app/login").forward(req, resp);
			}
		}else{
			fc.doFilter(req, resp);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}
