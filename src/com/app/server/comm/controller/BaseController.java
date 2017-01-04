package com.app.server.comm.controller;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.app.server.comm.util.DateUtils;
import com.app.server.comm.util.JSONWriter;
import com.app.server.comm.util.RequestUtil;
import com.app.server.comm.util.StringUtil;

public abstract class BaseController {
	@Autowired
	protected HttpServletRequest request;

	protected void keepParas() {
		Enumeration<String> paras = request.getParameterNames();
		if (StringUtil.isNullOrEmpty(paras)) {
			return;
		}
		while (paras.hasMoreElements()) {
			try {
				String string = (String) paras.nextElement();
				if (StringUtil.isNullOrEmpty(string)) {
					continue;
				}
				request.setAttribute(string.replace(".", "_"),
						request.getParameter(string));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	protected String loadBasePath(HttpServletRequest request) {
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
	protected String getPara(String paraName) {
		return request.getParameter(paraName);
	}

	protected Integer getParaInteger(String paraName) {
		return StringUtil.toInteger(request.getParameter(paraName));
	}

	protected Integer[] getParaIntegers(String paraName) {
		String[] paras = request.getParameterValues(paraName);
		if (StringUtil.isNullOrEmpty(paras)) {
			return null;
		}
		Integer[] values = new Integer[paras.length];
		for (int i = 0; i < paras.length; i++) {
			try {
				values[i] = Integer.valueOf(paras[i]);
			} catch (Exception e) {
			}
		}
		return values;
	}

	protected Double getParaDouble(String paraName) {
		return StringUtil.toDouble(request.getParameter(paraName));
	}

	protected Float getParaFloat(String paraName) {
		return StringUtil.toFloat(request.getParameter(paraName));
	}

	protected Long getParaLong(String paraName) {
		return StringUtil.toLong(request.getParameter(paraName));
	}

	protected Date getParaDate(String paraName) {
		try {
			return DateUtils.stringToDate(request.getParameter(paraName));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	protected Date getParaDateTime(String paraName) {
		try {
			return DateUtils.stringToDate(request.getParameter(paraName),
					DateUtils.DATETIME_PATTERN);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	protected Object getBeanAll(Object obj) {
		return RequestUtil.getBeanAll(request, obj);
	}

	protected Object getBeanAccept(Object obj, String... paras) {
		return RequestUtil.getBeanAccept(request, obj, paras);
	}

	protected Object getBeanRemove(Object obj, String... paras) {
		return RequestUtil.getBeanRemove(request, obj, paras);
	}

	protected Object getBeanAll(String fatherName, Object obj) {
		return RequestUtil.getBeanAll(request, fatherName, obj);
	}

	protected Object getBeanAccept(String fatherName, Object obj,
			String... paras) {
		return RequestUtil.getBeanAccept(request, fatherName, obj, paras);
	}

	protected Object getBeanRemove(String fatherName, Object obj,
			String... paras) {
		return RequestUtil.getBeanRemove(request, fatherName, obj, paras);
	}

	protected Object getSessionPara(String paraName) {
		return request.getSession().getAttribute(paraName);
	}

	protected void setSessionPara(String paraName, Object obj) {
		request.getSession().setAttribute(paraName, obj);
	}

	protected Object getAttribute(String paraName) {
		return request.getAttribute(paraName);
	}

	protected void setAttribute(String paraName, Object obj) {
		request.setAttribute(paraName, obj);
	}

	protected String getHeader(String paraName) {
		return request.getHeader(paraName);
	}

	protected Map<String, String> getParas() {
		Map<String, String[]> map=request.getParameterMap();
		if(StringUtil.isNullOrEmpty(map)){
			return null;
		}
		Map<String, String> paraMap=new HashMap<String, String>();
		for (String key:map.keySet()) {
			String[] values=map.get(key);
			if(StringUtil.isNullOrEmpty(values)){
				continue;
			}
			paraMap.put(key, values[0]);
		}
		return paraMap;
		
		
	}
	// 打印返回消息
	protected void printMsg(HttpServletResponse res, Object obj) {
		try {
			String msg = JSONWriter.write(obj);
			res.getWriter().print(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	} // 打印返回消息

	protected void print(HttpServletResponse res, String str) {
		try {
			res.getWriter().print(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
