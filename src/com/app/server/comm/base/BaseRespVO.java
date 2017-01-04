package com.app.server.comm.base;

import com.app.server.comm.enm.ResCodeEnum;

@SuppressWarnings("serial")
public class BaseRespVO extends BaseModel{

	private int result=100;
	private boolean state=false;
	private String msg;
	
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public boolean isState() {
		return state;
	}
	public void setState(boolean state) {
		this.state = state;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public BaseRespVO(int result, boolean state, String msg) {
		super();
		this.result = result;
		this.state = state;
		this.msg = msg;
	}
	public BaseRespVO(ResCodeEnum enm) {
		super();
		this.result = enm.getResult();
		this.state = enm.getState();
		this.msg=enm.getMsg();
	}
	public void pushEnum(ResCodeEnum enm) {
		this.result = enm.getResult();
		this.state = enm.getState();
		this.msg=enm.getMsg();
	}
	public static BaseRespVO getBaseRespVO(ResCodeEnum enm){
		BaseRespVO resp=new BaseRespVO();
		resp.pushEnum(enm);
		return resp;
	}
	public BaseRespVO(){
		pushEnum(ResCodeEnum.SUCCESS);
	}
}
