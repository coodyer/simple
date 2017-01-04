package com.app.server.comm.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.app.server.comm.annotation.LogHead;
import com.app.server.comm.avafinal.CacheFinal;
import com.app.server.comm.entity.BeanEntity;
import com.app.server.comm.entity.Record;
import com.app.server.model.SimpleDebugLog;

public class AspectUtil {
	
	public static ThreadLocal<Record> moduleThread = new ThreadLocal<Record>();
	
	public static  String getMethodLog(Method method){
		LogHead handle = method.getAnnotation(LogHead.class);
		if(handle==null){
			return null;
		}
		return handle.value();
	}
	
	public static  String getClassLog(Class<?> clazz){
		LogHead handle = clazz
				.getAnnotation(LogHead.class);
		if(handle==null){
			return clazz.getSimpleName();
		}
		return handle.value();
	}
	public static  void writeLog(String module){
		Record record=getCurrRecord();
		record.put(CacheFinal.LOGGER_WRAPPER, module);
		moduleThread.set(record);
	}
	public static Record getCurrRecord(){
		Record record=moduleThread.get();
		if(StringUtil.isNullOrEmpty(record)){
			record=new Record();
		}
		return record;
	}
	public static  String getCurrLog(){
		Record record=getCurrRecord();
		String logHead=(String) record.get(CacheFinal.LOGGER_WRAPPER);
		if(logHead==null){
			return "";
		}
		return logHead;
	}
	public static  String minusLog(){
		Record record=getCurrRecord();
		String logHead=(String) record.get(CacheFinal.LOGGER_WRAPPER);
		if(logHead==null){
			return "";
		}
		String tabs[]=logHead.split("_");
		if(tabs.length==1){
			record.put(CacheFinal.LOGGER_WRAPPER, "");
			moduleThread.set(record);
			return "";
		}
		StringBuilder sb=new StringBuilder();
		for (int i = 0; i < tabs.length-1; i++) {
			if(!StringUtil.isNullOrEmpty(sb)){
				sb.append("_");
			}
			sb.append(tabs[i]);
		}
		record.put(CacheFinal.LOGGER_WRAPPER, sb.toString());
		moduleThread.set(record);
		return sb.toString();
	}
	
	public static String getParaKey(String fieldName, Method method, Object[] args) {
		Object para =getMethodPara(fieldName, method, args);
		String key = JSONWriter.write(para);
		if (StringUtil.isNullOrEmpty(key)) {
			return "";
		}
		return EncryptUtil.md5Code(key);
	}
	
	public static Object getMethodPara(String fieldName, Method method, Object[] args){
		List<BeanEntity> beanEntitys = PropertUtil.getMethodParas(method);
		if (StringUtil.isNullOrEmpty(beanEntitys)) {
			return "";
		}
		String[] fields=fieldName.split("\\.");
		BeanEntity entity = (BeanEntity) PropertUtil.getObjectByList(
				beanEntitys, "fieldName", fields[0]);
		if (StringUtil.isNullOrEmpty(entity)) {
			return "";
		}
		Object para = args[beanEntitys.indexOf(entity)];
		if(fields.length>1&&para!=null){
			for (int i = 1; i < fields.length; i++) {
				para=PropertUtil.getFieldValue(para, fields[i]);
			}
		}
		return para;
	}
	
	@SuppressWarnings("unchecked")
	public static String getCurrDBTemplate(){
		Record record=getCurrRecord();
		List<String> dbTemplates=(List<String>) record.get(CacheFinal.JDBC_MASTER_WRAPPER);
		if(StringUtil.isNullOrEmpty(dbTemplates)){
			return null;
		}
		return dbTemplates.get(dbTemplates.size()-1);
	}
	
	@SuppressWarnings("unchecked")
	public static void writeDBTemplate(String dbTemplate){
		Record record=getCurrRecord();
		List<String> dbTemplates=(List<String>) record.get(CacheFinal.JDBC_MASTER_WRAPPER);
		if(StringUtil.isNullOrEmpty(dbTemplates)){
			dbTemplates=new ArrayList<String>();
		}
		dbTemplates.add(dbTemplate);
		record.put(CacheFinal.JDBC_MASTER_WRAPPER, dbTemplates);
		moduleThread.set(record);
	}
	@SuppressWarnings("unchecked")
	public static void minusDBTemplate(){
		Record record=getCurrRecord();
		List<String> dbTemplates=(List<String>) record.get(CacheFinal.JDBC_MASTER_WRAPPER);
		if(dbTemplates==null){
			return;
		}
		if(dbTemplates.size()<=1){
			record.put(CacheFinal.JDBC_MASTER_WRAPPER, null);
			moduleThread.set(record);
			return;
		}
		dbTemplates.remove(dbTemplates.size()-1);
		record.put(CacheFinal.JDBC_MASTER_WRAPPER, dbTemplates);
		moduleThread.set(record);
	}
	public static void writeDebugLogger(SimpleDebugLog log){
		Record record=getCurrRecord();
		List<SimpleDebugLog> logs=getDebugLoggers();
		if(StringUtil.isNullOrEmpty(logs)){
			logs=new ArrayList<SimpleDebugLog>();
		}
		logs.add(log);
		record.put(CacheFinal.SIMPLE_DEBUG_LOG_WRAPPER, logs);
		moduleThread.set(record);
	}
	@SuppressWarnings("unchecked")
	public static List<SimpleDebugLog> getDebugLoggers(){
		Record record=getCurrRecord();
		return (List<SimpleDebugLog>) record.get(CacheFinal.SIMPLE_DEBUG_LOG_WRAPPER);
	}
	public static void createDebugKey(String method){
		Record record=getCurrRecord();
		record.put(CacheFinal.SIMPLE_LOG_WRAPPER, method);
		moduleThread.set(record);
	}
	public static String getDebugKey(){
		Record record=getCurrRecord();
		Object method=record.get(CacheFinal.SIMPLE_LOG_WRAPPER);
		return (String)method;
	}
	public static void cleanDebugKey(){
		Record record=getCurrRecord();
		record.remove(CacheFinal.SIMPLE_LOG_WRAPPER);
		record.remove(CacheFinal.SIMPLE_DEBUG_LOG_WRAPPER);
		moduleThread.set(record);
	}
	public static void main(String[] args) {
		Map<Thread, StackTraceElement[]> map=Thread.getAllStackTraces();
	}
}
