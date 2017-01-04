package com.app.server.comm.base;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;
import com.app.server.comm.util.PropertUtil;

@SuppressWarnings("serial")
public class BaseModel implements Serializable{

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
	public String table(){
		String domainName=this.getClass().getSimpleName();
		String table=PropertUtil.unParsParaName(domainName);
		return table;
	}

}
