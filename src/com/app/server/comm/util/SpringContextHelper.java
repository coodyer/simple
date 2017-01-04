package com.app.server.comm.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextHelper implements ApplicationContextAware {
	
	private static ApplicationContext context;

	@SuppressWarnings("static-access")
    @Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String beanName){
		int i=0;
		while(context==null&&i<100){
			try {
				Thread.sleep(10);
				i++;
			} catch (InterruptedException e) {
				PrintException.getErrorStack(e, 0);
			}
		}
		return (T) context.getBean(beanName);
	}
	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<?> beanType){
		int i=0;
		while(context==null&&i<100){
			try {
				Thread.sleep(10);
				i++;
			} catch (InterruptedException e) {
				PrintException.getErrorStack(e, 0);
			}
		}
		return (T) context.getBean(beanType);
	}
}
