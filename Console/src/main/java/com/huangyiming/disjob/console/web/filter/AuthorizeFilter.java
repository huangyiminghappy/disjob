package com.huangyiming.disjob.console.web.filter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.huangyiming.disjob.common.model.Result;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.common.util.SpringWorkFactory;
import com.huangyiming.disjob.console.SystemDefault;
import com.huangyiming.disjob.monitor.db.service.PermitService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.huangyiming.disjob.monitor.db.domain.DBUser;

public class AuthorizeFilter implements Filter {

	private Map<String, Map<String, Boolean>> authCacheMap;

	private PermitService permitService;

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain fc)
			throws IOException, ServletException {
		if (req instanceof HttpServletRequest) {
			HttpServletRequest request = (HttpServletRequest) req;
			if("/login".equals(request.getPathInfo())){
				fc.doFilter(req, resp);
				return ;
			}
			Object user_session_obj = request.getSession().getAttribute(SystemDefault.USER_SESSION_KEY);
			if (user_session_obj != null && user_session_obj instanceof DBUser) {
				DBUser dbUser = (DBUser) user_session_obj;
				String username = dbUser.getUsername();
				String permitItem = request.getPathInfo();
				if (authCacheMap.containsKey(username) && authCacheMap.get(username).containsKey(permitItem)) {
					if (authCacheMap.get(username).get(permitItem) == Boolean.TRUE) {
						fc.doFilter(req, resp);
					}else{
						backNoOperatePermit(username, permitItem, request, resp);
					}
				} else {
					boolean hasPermit = permitService.hasPermit(dbUser, permitItem);
					if (authCacheMap.containsKey(username)) {
						authCacheMap.get(username).put(permitItem, hasPermit);
					} else {
						Map<String, Boolean> itemMap = new ConcurrentHashMap<String, Boolean>();
						itemMap.put(permitItem, hasPermit);
						authCacheMap.put(username, itemMap);
					}
					if (hasPermit) {
						fc.doFilter(req, resp);
					}else{
						backNoOperatePermit(username, permitItem, request, resp);
					}
				}
			}
		} else {
			fc.doFilter(req, resp);
		}
	}

	private void backNoOperatePermit(String username, String permitItem, HttpServletRequest request,
			ServletResponse resp) {
		try {
			if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
				ServletOutputStream os = resp.getOutputStream();
				os.write(new Gson().toJson(new Result(false, "NO OPERATION PERMISSION")).getBytes(SystemDefault.CHARSET));
				os.flush();
			} else
				request.getRequestDispatcher("/app/page/permit/nopermission").forward(request, resp);
		} catch (ServletException | IOException e) {
			LoggerUtil.error("AuthorizeFilter backNoOperatePermit - " + e.getMessage());
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		Cache<String, Map<String, Boolean>> cache = CacheBuilder
				.newBuilder()
				.expireAfterWrite(10, TimeUnit.SECONDS)
				.maximumSize(1000).build();
		authCacheMap = cache.asMap();
		permitService = (PermitService) SpringWorkFactory.getWorkObject("permitService");
	}
}
