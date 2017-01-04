package com.app.server.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.app.server.comm.entity.Where;
import com.app.server.comm.handle.JdbcHandle;
import com.app.server.comm.page.Pager;
import com.app.server.comm.util.StringUtil;
import com.app.server.model.SimpleDebugLog;
import com.app.server.model.SimpleUser;

@Service
public class SimpleUserService {

	@Resource
	JdbcHandle jdbcHandle;
	public SimpleUser loadSimpleUser(String userName){
		return (SimpleUser) jdbcHandle.findBeanFirst(SimpleUser.class, "user", userName);
	}
	public void saveSimpleUser(SimpleUser user){
		jdbcHandle.saveOrUpdateAuto(user);
	}
	public void saveLogs(List<SimpleDebugLog> logs){
		if(StringUtil.isNullOrEmpty(logs)){
			return;
		}
		logs=new ArrayList<SimpleDebugLog>(logs);
		for(SimpleDebugLog log:logs){
			try {
				jdbcHandle.insert(log); 
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<SimpleDebugLog> getLogs(Integer maxId,String methodKey){
		Pager pager=new Pager();
		pager.setPageSize(20);
		Where where=new Where();
		if(!StringUtil.isNullOrEmpty(methodKey)){
			where.set("method", methodKey);
		}
		if(maxId==null||maxId==0){
			maxId=getMaxId();
		}
		where.set("id", "<",maxId);
		return (List<SimpleDebugLog>) jdbcHandle.findBean(SimpleDebugLog.class, where, pager, "id", true);
	}
	private Integer getMaxId(){
		String sql="select max(id) from simple_debug_log";
		return jdbcHandle.doQueryInteger(sql);
	}
}
