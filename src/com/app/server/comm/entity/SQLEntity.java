package com.app.server.comm.entity;

import java.util.Map;

public class SQLEntity {

	private String sql;
	
	private Map<Integer, Object> paraMap;

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public Map<Integer, Object> getParaMap() {
		return paraMap;
	}

	public void setParaMap(Map<Integer, Object> paraMap) {
		this.paraMap = paraMap;
	}

	public SQLEntity(String sql, Map<Integer, Object> paraMap) {
		super();
		this.sql = sql;
		this.paraMap = paraMap;
	}
	
	
}
