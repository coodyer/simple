package com.app.server.test;

import com.app.server.comm.annotation.Command;
import com.app.server.comm.util.JUUIDUtil;
import com.app.server.vo.UserReqVO;

@Command("userAPI")
public class UserAPITest extends BaseTest {

	
	public void login() throws Exception{
		UserReqVO reqVo=new UserReqVO();
		reqVo.setUserName("simple");
		reqVo.setPassword("123456");
		String resp=pushAPI(reqVo);
		System.out.println(resp);
	}
	public static void main(String[] args) {
		System.out.println(JUUIDUtil.createUuid());
	}
}
