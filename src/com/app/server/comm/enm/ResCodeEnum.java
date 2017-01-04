package com.app.server.comm.enm;

/**
 * 消息响应码枚举
 * 
 * @author deng
 *
 */
public enum ResCodeEnum {

	SUCCESS(0, true, "操作成功"),// 成功标志
	LOGIN_OUT(1, false, "登录超时"),// 登录超时
	API_NOT_EXISTS(2, false, "请求action不存在"),// 登录超时
	PARA_ERROR(3, false, "参数验证不通过"), // 参数有误
	SYSTEM_ERROR(4, false, "系统繁忙，请稍后再试"), //系统繁忙
	PARA_IS_NULL(5,false,"参数为空"),//自动验参标志
	PARAS_IS_NULL(6,false,"参数不能同时为空"),//自动验参标志
	USER_NOT_FOUND(7,false,"用户不存在"),
	USER_PWD_ERROR(8,false,"密码有误"),
	OTHER(-1,false,"其他错误"),
	;
	private int result;
	private boolean state;
	private String msg;

	public int getResult() {
		return result;
	}
	public boolean getState() {
		return state;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg=msg;
	}
	ResCodeEnum(int result, boolean state, String msg) {
		this.result = result;
		this.state = state;
		this.msg = msg;
	}

}
