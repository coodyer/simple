package com.app.server.comm.base;


import java.util.Date;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.app.server.comm.util.AspectUtil;
import com.app.server.comm.util.StringUtil;
import com.app.server.model.SimpleDebugLog;

/**
 * 日志
 * 
 * @author Max
 * 
 */
public class BaseLogger {

	private Logger logger;

	public BaseLogger(Logger logger) {
		this.logger = logger;
	}
	

	/**
	 * 获取日志
	 * @param clazz
	 * @return
	 */
	public static BaseLogger getLogger(Class<?> clazz) {
		Logger logger = Logger.getLogger(clazz);
		return new BaseLogger(logger);
	}
	
	/**
	 * 获取日志-生产环境日志级别ERROR
	 * @param clazz
	 * @return
	 */
	public static BaseLogger getLoggerPro(Class<?> clazz) {
		Logger logger = Logger.getLogger(clazz);
		return new BaseLogger(logger);
	}

	/**
	 * 获取日志
	 * @param name
	 * @return
	 */
	public static BaseLogger getLogger(String name) {
		Logger logger = Logger.getLogger(name);
		return new BaseLogger(logger);
	}
	
	
	public Logger getLogger() {
		return logger;
	}
	
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public void info(Object message) {
		writeDebugLog(message);
		if (message instanceof String) {
			logger.info(getCurrModule() + message);
			return;
		}
		logger.info(getCurrModule() + JSON.toJSONString(message));
	}

	public void debug(Object message) {
		try {
			writeDebugLog(message);
			if (message instanceof String) {
				logger.debug(getCurrModule() + message);
				return;
			}
			logger.debug(getCurrModule() + JSON.toJSONString(message));
		} catch (Exception e) {
		}
	}

	public void error(Object message) {
		try {
			writeDebugLog(message);
			if (message instanceof String) {
				logger.error(getCurrModule() + message);
				return;
			}
			logger.error(getCurrModule() + JSON.toJSONString(message));
		} catch (Exception e) {
		}
	}

	public void error(Object message, Throwable t) {
		try {
			writeDebugLog(message);
			if (message instanceof String) {
				logger.error(getCurrModule() + message, t);
				return;
			}
			logger.error(getCurrModule() + JSON.toJSONString(message), t);
		} catch (Exception e) {
		}
	}

	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}
	private void writeDebugLog(Object message){
		try {
			String method=AspectUtil.getDebugKey();
			if(StringUtil.isNullOrEmpty(method)){
				return;
			}
			SimpleDebugLog log=new SimpleDebugLog();
			log.setMethod(method);
			log.setRunTime(new Date());
			String logContext=JSON.toJSONString(message);
			if(!StringUtil.isNullOrEmpty(logContext)){
				logContext=logContext.replace("\\n", "\n");
			}
			log.setLog(logContext);
			log.setThreadId(Thread.currentThread().getId());
			AspectUtil.writeDebugLogger(log);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	/**
	 * 获取日志所属模块名
	 * 
	 * @return
	 */
	private String getCurrModule() {
		String module = AspectUtil.getCurrLog();
		if (StringUtil.isNullOrEmpty(module)) {
			return "";
		}
		return "[" + module + "]";
	}
}
