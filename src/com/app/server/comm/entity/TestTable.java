package com.app.server.comm.entity;

import com.app.server.comm.annotation.Column;
import com.app.server.comm.annotation.Table;

@Table("user_base")
public class TestTable {

	/**
	 * 用户ID
	 */
	@Column("uid")
	private Integer id;
	/**
	 * 手机号码
	 */
	@Column("number")
	private Long mobile;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Long getMobile() {
		return mobile;
	}
	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}
	
	
}
