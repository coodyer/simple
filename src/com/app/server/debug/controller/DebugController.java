package com.app.server.debug.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javassist.Modifier;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.app.server.comm.avafinal.CacheFinal;
import com.app.server.comm.base.BaseRespVO;
import com.app.server.comm.cache.CacheTimerHandler;
import com.app.server.comm.controller.BaseController;
import com.app.server.comm.enm.ResCodeEnum;
import com.app.server.comm.entity.BeanEntity;
import com.app.server.comm.util.AspectUtil;
import com.app.server.comm.util.EncryptUtil;
import com.app.server.comm.util.FileUtils;
import com.app.server.comm.util.PrintException;
import com.app.server.comm.util.PropertUtil;
import com.app.server.comm.util.RequestUtil;
import com.app.server.comm.util.SpringContextHelper;
import com.app.server.comm.util.StringUtil;
import com.app.server.comm.util.VerificationCodeUtil;
import com.app.server.debug.entity.CtBeanEntity;
import com.app.server.debug.entity.CtClassEntity;
import com.app.server.debug.entity.CtMethodEntity;
import com.app.server.debug.entity.MonitorEntity;
import com.app.server.debug.entity.MsgEntity;
import com.app.server.debug.entity.WsFileEntity;
import com.app.server.debug.util.SimpleUtil;
import com.app.server.model.SimpleDebugLog;
import com.app.server.model.SimpleUser;
import com.app.server.service.SimpleUserService;

@Controller
public class DebugController extends BaseController {
	private static final String DIR = "simple/";
	@Resource
	SimpleUserService simpleUserService;

	@RequestMapping(value = "/debug/loginOut.do")
	public void loginOut(HttpServletRequest req, HttpServletResponse res) {
		RequestUtil.setAdmin(req, null);
		try {
			res.sendRedirect(getAttribute("basePath") + "debug/login.do");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/debug/modifyAdmin.do")
	public String modifyAdmin(HttpServletRequest req, HttpServletResponse res) {
		return DIR + "modify_admin";
	}

	@RequestMapping(value = "/debug/index.do")
	public String index(HttpServletRequest req, HttpServletResponse res) {
		return DIR + "index";
	}

	@RequestMapping(value = "/debug/base.do")
	public String base(HttpServletRequest req, HttpServletResponse res) {
		return DIR + "base";
	}

	@RequestMapping(value = "/debug/login.do")
	public String login(HttpServletRequest req, HttpServletResponse res) {
		return DIR + "login";
	}

	@RequestMapping(value = "/debug/saveCurrAdmin.do")
	public void saveCurrAdmin(HttpServletRequest req, HttpServletResponse res) {
		SimpleUser currAdmin = (SimpleUser) RequestUtil.getAdmin(req);
		String newUsername = getPara("username");
		String password = getPara("password");
		String newPwd = getPara("newPwd");
		if (StringUtil.findEmptyIndex(newUsername, password, newPwd) > -1) {
			printMsg(res, new MsgEntity(1, "用户名或密码为空"));
			return;
		}
		password = EncryptUtil.customEnCode(password);
		if (!password.equals(currAdmin.getPwd())) {
			printMsg(res, new MsgEntity(4, "旧密码有误"));
			return;
		}
		SimpleUser admin = simpleUserService.loadSimpleUser(newUsername);
		if (!StringUtil.isNullOrEmpty(admin)
				&& admin.getId().intValue() != currAdmin.getId().intValue()) {
			printMsg(res, new MsgEntity(6, "该用户名已被使用"));
			return;
		}
		newPwd = EncryptUtil.customEnCode(newPwd);
		currAdmin.setUser(newUsername);
		currAdmin.setPwd(newPwd);
		simpleUserService.saveSimpleUser(currAdmin);
		RequestUtil.setAdmin(req, currAdmin);
		printMsg(res, new MsgEntity(0, "操作成功"));
		return;
	}

	@RequestMapping(value = "/debug/doLogin.do")
	public void doLogin(HttpServletResponse res) {
		String username = getPara("username");
		String password = getPara("password");
		String verCode = getPara("verCode");
		if (StringUtil.isNullOrEmpty(verCode)) {
			printMsg(res, new MsgEntity(3, "验证码为空"));
			return;
		}
		if (StringUtil.findEmptyIndex(username, password) > -1) {
			printMsg(res, new MsgEntity(1, "用户名或密码为空"));
			return;
		}
		String sessionCode = (String) getSessionPara("piccode");
		setSessionPara("piccode", null);
		if (sessionCode == null || !sessionCode.equals(verCode)) {
			printMsg(res, new MsgEntity(4, "验证码有误"));
			return;
		}
		SimpleUser user = simpleUserService.loadSimpleUser(username);
		if (StringUtil.isNullOrEmpty(user)) {
			printMsg(res, new MsgEntity(2, "该用户不存在"));
			return;
		}
		password = EncryptUtil.customEnCode(password);
		if (!password.equals(user.getPwd())) {
			printMsg(res, new MsgEntity(3, "密码有误"));
			return;
		}
		RequestUtil.setAdmin(request, user);
		setSessionPara("loginTime", new Date());
		printMsg(res, new MsgEntity(0, "登录成功"));
		return;
	}

	@RequestMapping(value = "/debug/verCode.do")
	public void verCode(HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("image/gif");
		String verCode = VerificationCodeUtil.getCodeStr(4);
		ServletOutputStream out = response.getOutputStream();
		ImageIO.write(VerificationCodeUtil.outCode(120, 42, 4, 28, verCode),
				"png", out);
		request.getSession().setAttribute("piccode", verCode);
		out.flush();
		out.close();
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/debug/server.do")
	public String server(HttpServletRequest req, HttpServletResponse res) {
		String path = getPara("file");
		if (StringUtil.isNullOrEmpty(path)) {
			path = Thread.currentThread().getContextClassLoader()
					.getResource("").getPath();
		}
		path = new File(path).getPath().replace("\\", "/");
		String basePath = Thread.currentThread().getContextClassLoader()
				.getResource("").getPath();
		basePath = new File(basePath).getPath().replace("\\", "/");
		if (!path.startsWith(basePath)) {
			return DIR + "server_list";
		}
		File[] files = new File(path).listFiles();
		List<WsFileEntity> fileEntitys = FileUtils.parseWsFile(files);
		fileEntitys = (List<WsFileEntity>) PropertUtil.doSeq(fileEntitys,
				"suffix",true);
		fileEntitys = (List<WsFileEntity>) PropertUtil.doSeq(fileEntitys,
				"path");
		fileEntitys = (List<WsFileEntity>) PropertUtil.doSeq(fileEntitys,
				"type");
		setAttribute("files", fileEntitys);
		String currFile=new File(path).getPath() + "/";
		if(SimpleUtil.isWindows()){
			currFile=currFile.replace("/", "\\");
		}
		System.out.println(currFile);
		setAttribute("currFile",currFile );
		setAttribute("parentFile", new File(path).getParent());
		return DIR + "server_list";
	}

	@RequestMapping(value = "/debug/fileInfo.do")
	public String fileInfo(HttpServletRequest req, HttpServletResponse res) {
		loadClassEntity();
		return DIR + "server_info";
	}

	@RequestMapping(value = "/debug/monitors.do")
	public String monitors(HttpServletRequest req, HttpServletResponse res) {
		/**
		 * 加载我的监听列表
		 */
		List<String> keys = CacheTimerHandler
				.getKeysFuzz(CacheFinal.SYSTEM_RUN_INFO);
		setAttribute("keys", keys);
		return DIR + "monitor_list";
	}

	private CtClassEntity loadClassEntity() {
		String path = getPara("file");
		keepParas();
		if (StringUtil.isNullOrEmpty(path)) {
			return null;
		}
		String basePath = Thread.currentThread().getContextClassLoader()
				.getResource("").getPath();
		basePath = new File(basePath).getPath().replace("\\", "/") + "/";
		path = path.replace("\\", "/");
		if (!path.startsWith(basePath)) {
			return null;
		}
		while (path.contains("../")) {
			path = path.replace("../", "");
		}
		if (!path.endsWith(".class")) {
			File file = new File(path);
			if (file.length() < 1048576) {
				String info = FileUtils.readFile(path);
				setAttribute("context", info);
			}
			return null;
		}
		try {
			String packet = path.replace(basePath, "");
			packet = packet.replace("/", ".");
			packet = packet.replace(".class", "");
			Class<?> clazz = Class.forName(packet);
			CtClassEntity classInfo = SimpleUtil.getClassEntity(clazz);
			setAttribute("classInfo", classInfo);
			return classInfo;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/debug/serverMonitor.do")
	public String serverMonitor(HttpServletRequest req, HttpServletResponse res) {
		keepParas();
		String key = getPara("key");
		Method sourceMethod = SimpleUtil.getMethodByKey(key);
		CtClassEntity classInfo = SimpleUtil.getClassEntity(PropertUtil
				.getMethodClass(sourceMethod));
		CtMethodEntity method = (CtMethodEntity) PropertUtil.getObjectByList(
				classInfo.getMethods(), "key", key);
		setAttribute("method", method);
		setAttribute("isRun", 0);
		if (CacheTimerHandler.contains(key)) {
			setAttribute("isRun", 1);
			List<MonitorEntity> monitors = (List<MonitorEntity>) CacheTimerHandler
					.getCache(key);
			monitors = (List<MonitorEntity>) PropertUtil.doSeq(monitors,
					"runTime", true);
			setAttribute("monitors", monitors);
		}
		setAttribute("classInfo", classInfo);
		/**
		 * 初始化方法参数
		 */
		Object obj = SimpleUtil.initMethodParas(method.getSourceMethod());
		String initParas = JSON.toJSONString(obj);
		setAttribute("initParas", initParas);
		return DIR + "server_monitor";
	}

	@RequestMapping(value = "/debug/serverDebug.do")
	public void serverDebug(HttpServletRequest req, HttpServletResponse res) {
		keepParas();
		String key = getPara("key");
		String runData = getPara("input");
		Method method = SimpleUtil.getMethodByKey(key);
		if (method == null) {
			printMsg(res, new MsgEntity(-1, "方法未找到"));
			return;
		}
		method.setAccessible(true);
		Object[] paras = null;
		if (!StringUtil.isNullOrEmpty(runData)) {
			paras = JSON.parseObject(runData, Object[].class);
		}
		List<BeanEntity> entitys = PropertUtil.getMethodParas(method);
		if (!StringUtil.isNullOrEmpty(paras)) {
			if (paras.length != entitys.size()) {
				printMsg(res, new MsgEntity(-1, "参数数量有误"));
				return;
			}
			for (int i = 0; i < paras.length; i++) {
				BeanEntity entity = entitys.get(i);
				Object value = null;
				try {
					value = PropertUtil.parseValue(paras[i],
							entity.getFieldType());
					if (JSONObject.class.isAssignableFrom(value.getClass())) {
						value = JSON.parseObject(paras[i].toString(),
								entity.getFieldType());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				paras[i] = value;
			}
		}
		try {
			AspectUtil.createDebugKey(key);
			Class<?> clazz = PropertUtil.getMethodClass(method);
			if (Modifier.isStatic(method.getModifiers())) {
				Object result = method.invoke(null, paras);
				if (ResCodeEnum.class.isAssignableFrom(result.getClass())) {
					result = new BaseRespVO((ResCodeEnum) result);
				}
				printMsg(res,
						new MsgEntity(0, "操作成功", JSON.toJSONString(result)));
				return;
			}
			Object bean = SpringContextHelper.getBean(clazz);
			if (!StringUtil.isNullOrEmpty(bean)) {
				Class<?> sourceClass = SimpleUtil.getMethodClassByKey(key);
				if (!sourceClass.getName().equals(bean.getClass().getName())) {
					bean = sourceClass.cast(bean);
					PropertUtil.setProperties(method, "clazz", sourceClass);
				}
				Object result = method.invoke(bean, paras);
				if (result != null
						&& ResCodeEnum.class
								.isAssignableFrom(result.getClass())) {
					result = new BaseRespVO((ResCodeEnum) result);
				}
				printMsg(res,
						new MsgEntity(0, "操作成功", JSON.toJSONString(result)));
				return;
			}
			bean = clazz.newInstance();
			Class<?> sourceClass = SimpleUtil.getMethodClassByKey(key);
			if (!sourceClass.getName().equals(bean.getClass().getName())) {
				bean = sourceClass.cast(bean);
				PropertUtil.setProperties(method, "clazz", sourceClass);
			}
			Object result = method.invoke(bean, paras);
			printMsg(res, new MsgEntity(0, "操作成功", JSON.toJSONString(result)));
			return;
		} catch (Exception e) {
			e.printStackTrace();
			printMsg(
					res,
					new MsgEntity(-1, "执行出错", PrintException
							.getErrorStack(e, 0)));
			return;
		} finally {
			List<SimpleDebugLog> logs = AspectUtil.getDebugLoggers();
			simpleUserService.saveLogs(logs);
			AspectUtil.cleanDebugKey();
		}
	}

	@RequestMapping(value = "/debug/serverDoMonitor.do")
	public void serverDoMonitor(HttpServletRequest req, HttpServletResponse res) {
		String key = getPara("key");
		Integer isRun = getParaInteger("isRun");
		keepParas();
		if (StringUtil.findNull(key, isRun) > -1) {
			printMsg(res, new MsgEntity(-1, "参数有误"));
			return;
		}
		Class<?> clazz = PropertUtil.getMethodClass(SimpleUtil
				.getMethodByKey(key));
		if (StringUtil.isNullOrEmpty(clazz)) {
			printMsg(res, new MsgEntity(-1, "该方法不能监听"));
			return;
		}
		Object bean = SpringContextHelper.getBean(clazz);
		if (StringUtil.isNullOrEmpty(bean)) {
			printMsg(res, new MsgEntity(-1, "该方法不能监听,未找到Bean"));
			return;
		}
		if (1 == isRun) {
			CacheTimerHandler.addCache(key, new ArrayList<MonitorEntity>());
		} else {
			CacheTimerHandler.removeCache(key);
		}
		printMsg(res, new MsgEntity(0, "操作成功"));
		return;
	}

	@RequestMapping(value = "/debug/cache.do")
	public String cache(HttpServletRequest req, HttpServletResponse res) {
		List<CtBeanEntity> entitys = SimpleUtil.getBeanFields(CacheFinal.class);
		setAttribute("entitys", entitys);
		return DIR + "cache_list";
	}

	@RequestMapping(value = "/debug/cacheClean.do")
	public void cacheClean(HttpServletRequest req, HttpServletResponse res) {
		String key = getPara("key");
		if (StringUtil.isNullOrEmpty(key)) {
			printMsg(res, new MsgEntity(-1, "参数有误"));
			return;
		}
		CacheTimerHandler.removeCacheFuzzy(key);
		printMsg(res, new MsgEntity(0, "操作成功"));
		return;
	}
	@RequestMapping(value = "/debug/logs.do")
	public String logs(HttpServletRequest req, HttpServletResponse res) {
		Integer maxId=getParaInteger("maxId");
		String method=getPara("method");
		List<SimpleDebugLog> logs=simpleUserService.getLogs(maxId, method);
		setAttribute("logs", logs);
		keepParas();
		if(!StringUtil.isNullOrEmpty(logs)){
			maxId=logs.get(logs.size()-1).getId();
			setAttribute("maxId", maxId);
		}
		return DIR + "logs";
	}
	
	public static void main(String[] args) {
	}
}
