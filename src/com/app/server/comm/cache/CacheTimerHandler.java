package com.app.server.comm.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.app.server.comm.util.StringUtil;

/**
 * @className：CacheHandler
 * @description：缓存操作类，对缓存进行管理,清除方式采用Timer定时的方式
 * @creater：Websos
 * @creatTime：2014年5月7日 上午9:18:54
 * @remark：
 * @version
 */
public class CacheTimerHandler {
	private static final ConcurrentHashMap<String, Object> map;
	private static final Timer timer;
	static {
		timer = new Timer();
		map = new ConcurrentHashMap<String, Object>();
	}

	/**
	 * 增加缓存对象
	 * 
	 * @param key
	 * @param ce
	 * @param validityTime
	 *            有效时间
	 */
	public static synchronized void addCache(String key, Object ce,
			int validityTime) {
			map.put(key, ce);
			timer.schedule(new TimeoutTimerTask(key), validityTime * 1000);
	}
	//获取缓存KEY列表
	public static Set<String> getCacheKeys() {
		return map.keySet();
	}
	/**
	 * 增加缓存对象
	 * 
	 * @param key
	 * @param ce
	 * @param validityTime
	 *            有效时间
	 */
	public static synchronized void addCache(String key, Object ce) {
			map.put(key, ce);
	}

	/**
	 * 获取缓存对象
	 * 
	 * @param key
	 * @return
	 */
	public static Object getCache(String key) {
		return map.get(key);
	}
	public static List<String> getKeysFuzz(String patton){
		List<String> list=new ArrayList<String>();
		for (String tmpKey : map.keySet()) {
			if (tmpKey.contains(patton)) {
				list.add(tmpKey);
			}
		}
		if(StringUtil.isNullOrEmpty(list)){
			return null;
		}
		return list;
	}
	/**
	 * 检查是否含有制定key的缓冲
	 * 
	 * @param key
	 * @return
	 */
	public static boolean contains(String key) {
		return map.containsKey(key);
	}

	/**
	 * 删除缓存
	 * 
	 * @param key
	 */
	public static void removeCache(String key) {
		map.remove(key);
	}

	/**
	 * 删除缓存
	 * 
	 * @param key
	 */
	public static void removeCacheFuzzy(String key) {
		for (String tmpKey : map.keySet()) {
			if (tmpKey.indexOf(key)>-1) {
				map.remove(tmpKey);
			}
		}
	}

	/**
	 * 获取缓存大小
	 * 
	 * @param key
	 */
	public static int getCacheSize() {
		return map.size();
	}

	/**
	 * 清除全部缓存
	 */
	public static void clearCache() {
		map.clear();
	}
	static class TimeoutTimerTask extends TimerTask {
		private String ceKey;

		public TimeoutTimerTask(String key) {
			this.ceKey = key;
		}

		@Override
		public void run() {
			CacheTimerHandler.removeCache(ceKey);
		}
	}
	
	
	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 1000000; i++) {
			CacheTimerHandler.addCache(String.valueOf(i), i,5);
		}
		System.out.println("ok");
		while (CacheTimerHandler.getCacheSize()>0) {
			System.out.println(CacheTimerHandler.getCacheSize());
			Thread.sleep(1000);
		}
	}

}
