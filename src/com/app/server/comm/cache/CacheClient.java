package com.app.server.comm.cache;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;

import com.app.server.comm.util.StringUtil;
import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class CacheClient  implements  InitializingBean{
	private MemCachedClient client;
	private Properties pollInitParams;

	public void removeCache(String key) {
		try {
			client.delete(key);
		} catch (Exception e) {
		}
	}
	public void removeCacheFuzzy(String key){
		try {
			Set<String> keys=getCacheKeys();
			for (String str:keys) {
				if(str.indexOf(key)>-1){
					removeCache(str);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public boolean contains(String key) {
		try {
			return client.keyExists(key);
		} catch (Exception e) {
			return false;
		}
	}

	public Object getCache(String key) {
		try {
			return client.get(key);
		} catch (Exception e) {
			return null;
		}
	}

	public void addCache(String key, Object value, int exp) {
		try {
			client.set(key, value, new Date(System.currentTimeMillis() + exp
					* 1000));
		} catch (Exception e) {
		}
	}
	public void addCache(String key, Object value) {
		try {
			client.set(key, value);
		} catch (Exception e) {
		}
	}
	public void afterPropertiesSet() throws Exception {
		client = new MemCachedClient();
		SockIOPool pool = SockIOPool.getInstance();
		pool.setServers(StringUtil.splitToStringArray(
				pollInitParams.getProperty("servers"), ","));
		pool.setWeights(StringUtil.splitToIntgArray(
				pollInitParams.getProperty("weights"), ","));
		pool.setInitConn(Integer.parseInt(pollInitParams
				.getProperty("initConn")));
		pool.setMinConn(Integer.parseInt(pollInitParams.getProperty("minConn")));
		pool.setMaxConn(Integer.parseInt(pollInitParams.getProperty("maxConn")));
		pool.setMaxIdle(1000 * Long.parseLong(pollInitParams
				.getProperty("maxIdle")));
		pool.setMaintSleep(Integer.parseInt(pollInitParams
				.getProperty("maintSleep")));
		pool.setNagle(false);
		pool.setSocketTO(1000 * Integer.parseInt(pollInitParams
				.getProperty("socketTO")));
		pool.setSocketConnectTO(1000 * Integer.parseInt(pollInitParams
				.getProperty("socketConnectTO")));
		pool.initialize();

		// client.setCompressEnable(true);
		// client.setCompressThreshold(64 * 1024);
	}

	public Properties getPollInitParams() {
		return pollInitParams;
	}

	public void setPollInitParams(Properties pollInitParams) {
		this.pollInitParams = pollInitParams;
	}

	public void set(String key, Object value) {
		try {
			client.set(key, value);
		} catch (Exception e) {
		}
	}

	public Set<String> getCacheKeys() throws UnsupportedEncodingException {
		Set<String> keylist = new HashSet<String>();
		// 遍历statsItems 获取items:2:number=14
		Map<String, Map<String, String>> statsItems = client.statsItems();
		Map<String, String> statsItems_sub = null;
		String statsItems_sub_key = null;
		int items_number = 0;
		String server = null;
		// 根据items:2:number=14，调用statsCacheDump，获取每个item中的key
		Map<String, Map<String, String>> statsCacheDump = null;
		Map<String, String> statsCacheDump_sub = null;
		String statsCacheDumpsub_key = null;

		for (Iterator iterator = statsItems.keySet().iterator(); iterator
				.hasNext();) {
			server = (String) iterator.next();
			statsItems_sub = statsItems.get(server);
			for (Iterator iterator_item = statsItems_sub.keySet().iterator(); iterator_item
					.hasNext();) {
				statsItems_sub_key = (String) iterator_item.next();
				if (statsItems_sub_key.toUpperCase().startsWith(
						"items:".toUpperCase())
						&& statsItems_sub_key.toUpperCase().endsWith(
								":number".toUpperCase())) {
					items_number = Integer.parseInt(statsItems_sub.get(
							statsItems_sub_key).trim());
					statsCacheDump = client.statsCacheDump(
							new String[] { server }, Integer
									.parseInt(statsItems_sub_key.split(":")[1]
											.trim()), items_number);
					for (Iterator statsCacheDump_iterator = statsCacheDump
							.keySet().iterator(); statsCacheDump_iterator
							.hasNext();) {
						statsCacheDump_sub = statsCacheDump
								.get(statsCacheDump_iterator.next());
						for (Iterator iterator_keys = statsCacheDump_sub
								.keySet().iterator(); iterator_keys.hasNext();) {
							statsCacheDumpsub_key = (String) iterator_keys
									.next();
							keylist.add(URLDecoder.decode(
									statsCacheDumpsub_key, "UTF-8"));
						}
					}
				}

			}
		}
		return keylist;
	}
}
