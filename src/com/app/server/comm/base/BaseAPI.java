package com.app.server.comm.base;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Cache;

import org.springframework.beans.factory.InitializingBean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.app.server.comm.annotation.ApiHandle;
import com.app.server.comm.annotation.LoginCheck;
import com.app.server.comm.annotation.ParamCheck;
import com.app.server.comm.avafinal.CacheFinal;
import com.app.server.comm.cache.CacheTimerHandler;
import com.app.server.comm.enm.ResCodeEnum;
import com.app.server.comm.entity.BeanEntity;
import com.app.server.comm.entity.Header;
import com.app.server.comm.util.PrintException;
import com.app.server.comm.util.PropertUtil;
import com.app.server.comm.util.SpringContextHelper;
import com.app.server.comm.util.StringUtil;

public class BaseAPI implements InitializingBean{
	
	// 初始化接口
	protected Map<String, Method> apiMap = new HashMap<String, Method>();

	private void initAPI(Class<?> cla) {
		if (!StringUtil.isNullOrEmpty(apiMap)) {
			return;
		}
		Method[] methods = cla.getDeclaredMethods();
		for (Method method : methods) {
			ApiHandle api = method.getAnnotation(ApiHandle.class);
			if (StringUtil.isNullOrEmpty(api)) {
				continue;
			}
			String apiName = api.value();
			if (StringUtil.isNullOrEmpty(apiName)) {
				apiName = method.getName();
			}
			method.setAccessible(true);
			logger.info("初始化接口:"+method.getClass().getName()+"."+apiName);
			apiMap.put(apiName, method);
		}
	}

	
	static final String encode="UTF-8";
	final BaseLogger logger=BaseLogger.getLogger(this.getClass());

	public String execute(Header header,String postData){
		try {
			String action=header.getAction();
			Method method=apiMap.get(action);
			//登陆验证
			LoginCheck loginCheck = method.getAnnotation(LoginCheck.class);
			if (!StringUtil.isNullOrEmpty(loginCheck)) {
				String userId=header.getUserId();
				String token =header.getToken();
				if (!checkLogin(userId, token)) {
					return JSON.toJSONString(new BaseRespVO(ResCodeEnum.LOGIN_OUT));
				}
			}
			//参数验证
			Object para=getAPIParas(method,postData);
			Object[] paras=null;
			if(para!=null){
				if(BaseRespVO.class.isAssignableFrom(para.getClass())){
					return JSON.toJSONString(para);
				}
				if(para instanceof Object[]){
					paras=(Object[])para;
				}
			}
			BaseAPI api=SpringContextHelper.getBean(this.getClass());
			//方法执行
			Object result = method.invoke(api, paras);
			if(ResCodeEnum.class.isAssignableFrom(result.getClass())){
				result=JSON.toJSONString(new BaseRespVO((ResCodeEnum)result));
			}
			if(!(result instanceof String)){
				result=JSON.toJSONString(result);
			}
			String json=StringUtil.toString(result);
			return json;
		} catch (Exception e) {
			PrintException.printException(logger, e);
		}
		return null;
	}
	
	
	private boolean checkLogin(String userId, String token) {
		String key=CacheFinal.USER_LOGIN_TOKEN+userId;
		String sysToken=(String) CacheTimerHandler.getCache(key);
		if(token.equals(sysToken)){
			return true;
		}
		return false;
	}


	private Object getAPIParas(Method api ,String json) throws InstantiationException, IllegalAccessException{
		List<BeanEntity> entitys=PropertUtil.getMethodParas(api);
		if(StringUtil.isNullOrEmpty(entitys)){
			return null;
		}
		Object []paras=new Object[entitys.size()];
		Map<String, Object> paraMap=null;
		for (int i = 0; i <entitys.size(); i++) {
			try {
				if(Map.class.isAssignableFrom(entitys.get(i).getFieldType())){
					paras[i]= JSON.parseObject(json,new TypeReference<Map<String, Object>>(){});
					continue;
				}
				//请求bean装载
				if(BaseModel.class.isAssignableFrom(entitys.get(i).getFieldType())){
					paras[i]=JSON.parseObject(json, entitys.get(i).getFieldType());
					if(!StringUtil.isNullOrEmpty(paras[i])){
						BaseRespVO respVO=checkPara((BaseModel)paras[i]);
						if(!StringUtil.isNullOrEmpty(respVO)){
							return respVO;
						}
					}
					continue;
				}
				if(paraMap==null){
					paraMap=JSON.parseObject(json,new TypeReference<Map<String, Object>>(){});
				}
				//其他零散参数装载
				Object value=paraMap.get(entitys.get(i).getFieldName());
				ParamCheck check= (ParamCheck) entitys.get(i).getAnnotation(ParamCheck.class);
				BaseRespVO respVO=checkPara(check, entitys.get(i).getFieldName(), value, paraMap);
				if(!StringUtil.isNullOrEmpty(respVO)){
					return respVO;
				}
				Object obj=PropertUtil.parseValue(value, entitys.get(i).getFieldType());
				paras[i]=obj;
			} catch (Exception e) {
				PrintException.printException(logger,e);
			}
		}
		return paras;
	}
	protected boolean checkLogin(Long uid, String loginKey) {
		if (StringUtil.findEmptyIndex(uid, loginKey) > -1) {
			return false;
		}
		return true;
	}
	private BaseRespVO checkPara(BaseModel reqVO){
		List<BeanEntity> entitys=PropertUtil.getBeanFields(reqVO);
		for (BeanEntity entity:entitys) {
			ParamCheck check= entity.getSourceField().getAnnotation(ParamCheck.class);
			if(StringUtil.isNullOrEmpty(check)){
				continue;
			}
			Object obj=PropertUtil.getFieldValue(reqVO, entity.getFieldName());
			String error=check.errorMsg();
			//数据可空验证
			if(!check.allowNull()){
				if(StringUtil.isNullOrEmpty(obj)){
					return getParaErrResp(ResCodeEnum.PARA_IS_NULL,entity.getFieldName(),error);
				}
				if(!StringUtil.isNullOrEmpty(check.orNulls())){
					List<Object> values=PropertUtil.getFieldValues(reqVO, check.orNulls());
					if(!StringUtil.isAllNull(values)){
						return getParaErrResp(ResCodeEnum.PARAS_IS_NULL,entity.getFieldName()+":"+check.orNulls().toString(),error);
					}
				}
			}
			//数据格式验证
			if(!StringUtil.isNullOrEmpty(obj)){
				if(!StringUtil.isNullOrEmpty(check.format())){
					if(!StringUtil.isMatcher(obj.toString(), check.format())){
						return getParaErrResp(ResCodeEnum.PARA_ERROR,entity.getFieldName()+":"+obj.toString(),error);
					}
				}
			}
		}
		return null;
	}
	private BaseRespVO checkPara(ParamCheck check,String fieldName,Object fieldValue,Map<String, Object> allParas){
		if(StringUtil.isNullOrEmpty(check)){
			return null;
		}
		String error=check.errorMsg();
		//数据可空验证
		if(!check.allowNull()){
			if(StringUtil.isNullOrEmpty(fieldValue)){
				return getParaErrResp(ResCodeEnum.PARA_IS_NULL,fieldName,error);
			}
			if(!StringUtil.isNullOrEmpty(check.orNulls())){
				List<Object> values=PropertUtil.getFieldValues(allParas, check.orNulls());
				if(!StringUtil.isAllNull(values)){
					return getParaErrResp(ResCodeEnum.PARAS_IS_NULL,fieldName+":"+check.orNulls().toString(),error);
				}
			}
		}
		//数据格式验证
		if(!StringUtil.isNullOrEmpty(fieldValue)){
			if(!StringUtil.isNullOrEmpty(check.format())){
				if(!StringUtil.isMatcher(fieldValue.toString(), check.format())){
					return getParaErrResp(ResCodeEnum.PARA_ERROR,fieldName+":"+fieldValue.toString(),error);
				}
			}
		}
		return null;
	}
	private BaseRespVO getParaErrResp(ResCodeEnum enm,String msg,String error){
		BaseRespVO respVO=new BaseRespVO(enm);
		respVO.setMsg(respVO.getMsg()+"，"+msg);
		if(!StringUtil.isNullOrEmpty(error)){
			respVO.setMsg(respVO.getMsg()+"("+error+")");
		}
		return respVO;
	}
	@Override
	public void afterPropertiesSet() throws Exception {
		initAPI(this.getClass());
	}
}
