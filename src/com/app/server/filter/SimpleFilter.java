package com.app.server.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.server.comm.util.RequestUtil;
import com.app.server.model.SimpleUser;


public class SimpleFilter implements Filter {

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;
		String uri = getRequestUri(request);
		if (uri.equals("login") || uri.equals("doLogin")||uri.equals("verCode")) {
			arg2.doFilter(arg0, arg1);
			return;
		}
		String basePath=loadBasePath(request);
		SimpleUser user = (SimpleUser) RequestUtil.getAdmin(request);
		if (RequestUtil.isAdminLogin(request)) {
			request.setAttribute("user", user);
			arg2.doFilter(arg0, arg1);
			return;
		}
		response.sendRedirect(basePath+ "debug/login.do");
	}
	private String getRequestUri(HttpServletRequest request) {
		String uri = request.getRequestURI();
		String[] uris = uri.split("/debug/");
		if (uris.length == 1) {
			return "";
		}
		uris = uris[uris.length - 1].split("\\.");
		return uris[0];
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}
	private String loadBasePath(HttpServletRequest request) {
		String path = request.getContextPath();
		String basePath = request.getScheme()
				+ "://"
				+ request.getServerName()
				+ (request.getServerPort() == 80 ? "" : ":"
						+ request.getServerPort()) + path + "/";
		request.getSession().setAttribute("basePath", basePath);
		request.setAttribute("basePath", basePath);
		return basePath;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
	}
}
