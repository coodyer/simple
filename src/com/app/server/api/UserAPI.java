package com.app.server.api;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.app.server.comm.annotation.ApiHandle;
import com.app.server.comm.annotation.ParamCheck;
import com.app.server.comm.avafinal.FormatFinal;
import com.app.server.comm.base.BaseAPI;
import com.app.server.comm.enm.ResCodeEnum;
import com.app.server.comm.util.EncryptUtil;
import com.app.server.model.SimpleUser;
import com.app.server.service.SimpleUserService;
import com.app.server.vo.UserReqVO;
import com.app.server.vo.UserRespVO;

@Service("userAPI")
public class UserAPI extends BaseAPI{

	@Resource
	SimpleUserService simpleUserService;
	
	@ApiHandle
	public Object login(UserReqVO reqVo){
		SimpleUser user=simpleUserService.loadSimpleUser(reqVo.getUserName());
		if(user==null){
			return ResCodeEnum.USER_NOT_FOUND;
		}
		if(!user.getPwd().equals(EncryptUtil.customEnCode(reqVo.getPassword()))){
			return ResCodeEnum.USER_PWD_ERROR;
		}
		UserRespVO respVo=new UserRespVO();
		respVo.setUserInfo(user);
		return respVo;
	}
	
	@ApiHandle
	public Object login(@ParamCheck(format=FormatFinal.USER_NAME) String userName,
			String password){
		SimpleUser user=simpleUserService.loadSimpleUser(userName);
		if(user==null){
			return ResCodeEnum.USER_NOT_FOUND;
		}
		if(!user.getPwd().equals(EncryptUtil.customEnCode(password))){
			return ResCodeEnum.USER_PWD_ERROR;
		}
		UserRespVO respVo=new UserRespVO();
		respVo.setUserInfo(user);
		return respVo;
	}
}
