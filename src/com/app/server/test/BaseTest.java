package com.app.server.test;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.alibaba.fastjson.JSON;
import com.app.server.comm.annotation.Command;
import com.app.server.comm.base.BaseReqVO;
import com.app.server.comm.entity.Header;
import com.app.server.comm.entity.HttpEntity;
import com.app.server.comm.util.EncryptUtil;
import com.app.server.comm.util.HttpUtil;

public class BaseTest extends TestCase{
	
	private String url="http://127.0.0.1:8080/AppServer/api/doApi.do";
	private String channel="ME_TEST";
	
	public String pushAPI(BaseReqVO reqVo) throws Exception{
		String action=Thread.currentThread().getStackTrace()[2].getMethodName();
		String command=getCommand();
		String postData=JSON.toJSONString(reqVo);
		String key=getChannelKey(postData);
		Map<String, String> headMap=new HashMap<String, String>();
		headMap.put("channel", channel);
		Header header=new Header();
		header.setAction(action);
		header.setCommand(command);
		String common=JSON.toJSONString(header);
		common=EncryptUtil.encryptMessage(common, key);
		System.out.println("请求头部:"+common);
		postData=EncryptUtil.encryptMessage(postData, key);
		System.out.println("请求内容:"+postData);
		headMap.put("common", common);
		HttpEntity entity=	HttpUtil.Post(url, postData,headMap);
		return entity.getHtml();
	}
	
	private String getCommand(){
		Command command=this.getClass().getAnnotation(Command.class);
		return command.value();
	}
	
	private String getChannelKey(String channel){
		return "5bb98b1ac39842afa42f769f9e5cda46";
	}
}
