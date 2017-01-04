package com.app.server.model;
// default package


import java.util.Date;

import com.app.server.comm.base.BaseModel;

/**
 * SimpleDebugLog entity. @author MyEclipse Persistence Tools
 */

@SuppressWarnings("serial")
public class SimpleDebugLog extends BaseModel{

	// Fields

	private Integer id;
	private String method;
	private String log;
	private Long threadId;
	private Date runTime;

	// Constructors

	/** default constructor */
	public SimpleDebugLog() {
	}

	/** full constructor */
	public SimpleDebugLog(String method, String log, Long threadId,
			Date runTime) {
		this.method = method;
		this.log = log;
		this.threadId = threadId;
		this.runTime = runTime;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMethod() {
		return this.method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getLog() {
		return this.log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public Long getThreadId() {
		return this.threadId;
	}

	public void setThreadId(Long threadId) {
		this.threadId = threadId;
	}

	public Date getRunTime() {
		return this.runTime;
	}

	public void setRunTime(Date runTime) {
		this.runTime = runTime;
	}

}