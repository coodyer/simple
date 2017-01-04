package com.app.server.comm.aspect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.alibaba.fastjson.JSON;
import com.app.server.comm.annotation.CacheRemove;
import com.app.server.comm.annotation.CacheWrite;
import com.app.server.comm.annotation.DBMaster;
import com.app.server.comm.base.BaseLogger;
import com.app.server.comm.cache.CacheTimerHandler;
import com.app.server.comm.enm.ResCodeEnum;
import com.app.server.comm.util.AspectUtil;
import com.app.server.comm.util.PrintException;
import com.app.server.comm.util.PropertUtil;
import com.app.server.comm.util.StringUtil;
import com.app.server.debug.entity.MonitorEntity;
import com.app.server.debug.util.SimpleUtil;

@Aspect
@Component
public class AppAspect {
	
	private final BaseLogger logger = BaseLogger.getLoggerPro(this.getClass());




	/**
	 * 新版本迭代控制未测试状态方法,屏蔽报错
	 * 
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* com.app.server..*.*(..)) && @annotation(com.app.server.comm.annotation.DeBug)")
	public Object deBugMonitor(ProceedingJoinPoint pjp) throws Throwable {
		StopWatch sw = new StopWatch(getClass().getSimpleName());
		try {
			// AOP启动监听
			sw.start(pjp.getSignature().toShortString());
			try {
				return pjp.proceed();
			} catch (Exception e) {
				PrintException.printException(logger, e);
				return null;
			}
		} finally {
			sw.stop();
		}
	}

	/**
	 * 数据库主从控制
	 * 
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* com.app.server..*.*(..)) && @annotation(com.app.server.comm.annotation.DBMaster)")
	public Object dbMonitor(ProceedingJoinPoint pjp) throws Throwable {
		StopWatch sw = new StopWatch(getClass().getSimpleName());
		try {
			// AOP启动监听
			sw.start(pjp.getSignature().toShortString());
			// AOP获取方法执行信息
			Signature signature = pjp.getSignature();
			MethodSignature methodSignature = (MethodSignature) signature;
			Method method = methodSignature.getMethod();
			DBMaster handle = method.getAnnotation(DBMaster.class);
			AspectUtil.writeDBTemplate(handle.value());
			return pjp.proceed();
		} finally {
			AspectUtil.minusDBTemplate();
			sw.stop();
		}
	}


	@Around("execution(* com.app.server..*.*(..)) && @annotation(com.app.server.comm.annotation.CacheWrite)")
	public Object cacheWrite(ProceedingJoinPoint pjp) throws Throwable {
		StopWatch sw = new StopWatch(getClass().getSimpleName());
		try {
			// AOP启动监听
			sw.start(pjp.getSignature().toShortString());
			// AOP获取方法执行信息
			Signature signature = pjp.getSignature();
			MethodSignature methodSignature = (MethodSignature) signature;
			Method method = methodSignature.getMethod();
			if (method == null) {
				return pjp.proceed();
			}
			// 获取注解
			CacheWrite handle = method.getAnnotation(CacheWrite.class);
			if (handle == null) {
				return pjp.proceed();
			}
			// 封装缓存KEY
			StringBuilder key = new StringBuilder(handle.key());
			if (StringUtil.isNullOrEmpty(key)) {
				key = new StringBuilder(PropertUtil.getMethodClass(method)
						.getName() + "." + method.getName().replace(".", "_"));
			}
			Object[] args = pjp.getArgs();
			if (!StringUtil.isNullOrEmpty(args)) {
				try {
					if (StringUtil.isNullOrEmpty(handle.pkField())) {
						key.append(StringUtil.getBeanKey(args));
					} else {
						key.append(AspectUtil.getParaKey(handle.pkField(), method, args));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			Integer cacheTimer = ((handle.validTime() == 0) ? 24 * 3600
					: handle.validTime());
			// 获取缓存
			try {
				Object result = CacheTimerHandler.getCache(key.toString());
				if (!StringUtil.isNullOrEmpty(result)) {
					return result;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Object result = pjp.proceed();
			if (result!=null) {
				try {
					CacheTimerHandler.addCache(key.toString(), result, cacheTimer);
				} catch (Exception e) {
				}
			}
			return result;
		} finally {
			sw.stop();
		}
	}

	/**
	 * 缓存清理
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* com.app.server..*.*(..)) && @annotation(com.app.server.comm.annotation.CacheRemove)")
	public Object cacheRemove(ProceedingJoinPoint pjp) throws Throwable {
		StopWatch sw = new StopWatch(getClass().getSimpleName());
		try {
			// 启动监听
			sw.start(pjp.getSignature().toShortString());
			Signature signature = pjp.getSignature();
			MethodSignature methodSignature = (MethodSignature) signature;
			Method method = methodSignature.getMethod();
			if (method == null) {
				return pjp.proceed();
			}
			CacheRemove handle = method.getAnnotation(CacheRemove.class);
			Object result = pjp.proceed();
			if (handle != null) {
				if (StringUtil.isNullOrEmpty(handle.pkField())) {// 按照模块删除
					for (String key : handle.keys()) {
						CacheTimerHandler.removeCacheFuzzy(key);
					}
					return result;
				}
				if(handle.isModuel()==true){
					for (String key : handle.keys()) {
						CacheTimerHandler.removeCacheFuzzy(key);
					}
					return result;
				}
				// 按照key值删除
				Object[] args = pjp.getArgs();
				StringBuilder apendKey = new StringBuilder("");
				if (!StringUtil.isNullOrEmpty(args)) {
					try {
						if (StringUtil.isNullOrEmpty(handle.pkField())) {
							apendKey.append(StringUtil.getBeanKey(args));
						} else {
							apendKey.append(AspectUtil.getParaKey(handle.pkField(),
									method, args));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				for (String key : handle.keys()) {
					String tmp=key + apendKey.toString();
					CacheTimerHandler.removeCache(tmp);
				}
			}
			return result;
		} finally {
			sw.stop();
		}
	}

	/**
	 * 日志管理
	 * 
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* com.app.server..*.*(..)) && @annotation(com.app.server.comm.annotation.LogHead)")
	public Object loggerMonitor(ProceedingJoinPoint pjp) throws Throwable {
		StopWatch sw = new StopWatch(getClass().getSimpleName());
		try {
			// AOP启动监听
			sw.start(pjp.getSignature().toShortString());
			// AOP获取方法执行信息
			Signature signature = pjp.getSignature();
			MethodSignature methodSignature = (MethodSignature) signature;
			Method method = methodSignature.getMethod();
			Class<?> clazz = PropertUtil.getMethodClass(method);
			String module = AspectUtil.getCurrLog();
			if (!StringUtil.isNullOrEmpty(module)) {
				module +="_";
			}
			String classLog=AspectUtil.getClassLog(clazz);
			if (!StringUtil.isNullOrEmpty(classLog)) {
				module +=classLog;
			}
			if (!StringUtil.isNullOrEmpty(module)) {
				module +=".";
			}
			String methodLog=AspectUtil.getMethodLog(method);
			if (!StringUtil.isNullOrEmpty(methodLog)) {
				module +=methodLog;
			}else{
				module +=method.getName();
			}
			AspectUtil.writeLog(module);
			return pjp.proceed();
		} finally {
			AspectUtil.minusLog();
			sw.stop();
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Around("execution(* com.app.server..*.*(..)))")
	public Object serviceMonitor(ProceedingJoinPoint pjp) throws Throwable {
		StopWatch sw = new StopWatch(getClass().getSimpleName());
		try {
			// AOP启动监听
			sw.start(pjp.getSignature().toShortString());
			// AOP获取方法执行信息
			Signature signature = pjp.getSignature();
			MethodSignature methodSignature = (MethodSignature) signature;
			Class<?> clazz=methodSignature.getDeclaringType();
			Method method = methodSignature.getMethod();
			PropertUtil.setProperties(method, "clazz", clazz);
			String key=SimpleUtil.getMethodKey(method);
			if(CacheTimerHandler.contains(key)){
				Object[] args = pjp.getArgs();
				Date runTime=new Date();
				Object result =pjp.proceed();
				Date resultTime=new Date();
				try {
					String input=getJson(args);
					String output=getJson(result);
					MonitorEntity entity=new MonitorEntity();
					entity.setInput(input);
					entity.setOutput(output);
					entity.setRunTime(runTime);
					entity.setResultTime(resultTime);
					List<MonitorEntity> entitys=(List<MonitorEntity>) CacheTimerHandler.getCache(key);
					if(StringUtil.isNullOrEmpty(entitys)){
						entitys=new ArrayList<MonitorEntity>();
					}
					entitys.add(entity);
					CacheTimerHandler.addCache(key, entitys);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return result;
			}
			Object result = pjp.proceed();
			return result;
		} finally {
			sw.stop();
		}
	}
	private static String getJson(Object...args){
		if(StringUtil.isNullOrEmpty(args)){
			return "";
		}
		List<Object> newArgs=new ArrayList<Object>();
		for(Object arg:args){
			Object tmp=arg;
			if(arg!=null){
				if(ServletRequest.class.isAssignableFrom(arg.getClass())||ServletResponse.class.isAssignableFrom(arg.getClass())){
					tmp=arg.getClass();
				}
			}
			if(ResCodeEnum.class.isAssignableFrom(arg.getClass())){
				
			}
			newArgs.add(tmp);
		}
		return JSON.toJSONString(newArgs);
	}

	public static void main(String[] args) {
		System.out.println("\u0027");
	}
}
