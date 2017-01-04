package com.app.server.debug.entity;

import com.app.server.comm.base.BaseModel;

/**
 * @remark 消息机制容器。
 * @author WebSOS
 * @email 644556636@qq.com
 * @blog bkkill.com
 */
public class MsgEntity extends BaseModel {

	public Integer code;
	public String msg;
	public Object datas;

	public MsgEntity(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public MsgEntity() {
	}

	public MsgEntity(Integer code, String msg, Object datas) {
		super();
		this.code = code;
		this.msg = msg;
		this.datas = datas;
	}

	public Object getDatas() {
		return datas;
	}

	public void setDatas(Object datas) {
		this.datas = datas;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
