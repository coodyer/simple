package com.app.server.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.app.server.comm.base.BaseAPI;
import com.app.server.comm.entity.Header;
import com.app.server.comm.util.EncryptUtil;
import com.app.server.comm.util.RequestUtil;
import com.app.server.comm.util.SpringContextHelper;
import com.app.server.comm.util.StringUtil;
import com.app.server.service.ChannelService;

@Controller
public class ApiController {
	final Logger logger=Logger.getLogger(this.getClass());
	static final String encode="UTF-8";
	
	@Resource
	ChannelService channelService;
	
	@RequestMapping
	public void doApi(HttpServletRequest req,HttpServletResponse resp) throws Exception{
		String channel=req.getHeader("channel");
		if(StringUtil.isNullOrEmpty(channel)){
			resp.setStatus(404);
			logger.info("渠道不存在:"+channel);
			return ;
		}
		logger.info("收到接口请求,来源渠道:"+channel);
		String key=channelService.getChannelKey(channel);
		if(StringUtil.isNullOrEmpty(key)){
			resp.setStatus(404);
			logger.info("key不存在:"+key);
			return ;
		}
		String head=req.getHeader("common");
		String json=EncryptUtil.decryptMessage(head, encode, key);
		Header header=JSON.parseObject(json, Header.class);
		logger.info("命令:"+header.getCommand());
		BaseAPI api=(BaseAPI) SpringContextHelper.getBean(header.getCommand());
		if(StringUtil.isNullOrEmpty(api)){
			resp.setStatus(404);
			logger.info("命令不存在:"+header.getCommand());
			return ;
		}
		String postData=RequestUtil.getPostData(req, encode);
		postData=EncryptUtil.decryptMessage(postData, encode, key);
		String resData=api.execute(header,postData);
		resp.getWriter().print(resData);
	}
}
