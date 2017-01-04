package com.app.server.comm.base;

import java.lang.reflect.Method;

import org.springframework.scheduling.annotation.Scheduled;

import com.app.server.comm.avafinal.CacheFinal;
import com.app.server.comm.cache.CacheClient;
import com.app.server.comm.handle.SystemDirHandle;
import com.app.server.comm.thread.SysThreadPool;
import com.app.server.comm.util.SpringContextHelper;
import com.app.server.comm.util.StringUtil;

public class BaseTask {

	public BaseTask() {
		SysThreadPool.threadPool.execute(new Runnable() {

			@Override
			public void run() {
				initTask();
			}
		});
	}

	public void initTask() {
		CacheClient cacheClient = (CacheClient) SpringContextHelper
				.getBean("cacheClient");
		Integer num = 0;
		while (cacheClient == null && num < 100) {
			try {
				cacheClient = (CacheClient) SpringContextHelper
						.getBean("cacheClient");
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Method[] methods = this.getClass().getDeclaredMethods();
		if (StringUtil.isNullOrEmpty(methods)) {
			return;
		}
		for (Method method : methods) {
			Scheduled handle = method.getAnnotation(Scheduled.class);
			if (StringUtil.isNullOrEmpty(handle)) {
				continue;
			}
			String key = this.getClass().getName() + "." + method.getName();
			key = key.replace(".", "_").replace("\\.", "_");
			if (cacheClient.contains(CacheFinal.SYSTEM_SCHEDULE_WRAPPER + key)) {
				// 该方法已经被其他机器注册
				continue;
			}
			// 注册方法
			cacheClient.addCache(CacheFinal.SYSTEM_SCHEDULE_WRAPPER + key,
					SystemDirHandle.pathKey);
		}
	}
}
