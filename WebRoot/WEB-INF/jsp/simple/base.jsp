<%@page import="com.app.server.comm.util.DateUtils"%>
<%@page import="com.app.server.comm.util.RequestUtil"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!doctype html>
<html class="no-js">
<head>
<jsp:include page="base/head.jsp" />
</head>
<body>
	<div class="am-cf am-padding">
		<div class="am-
fl am-cf">
			<strong class="am-text-primary am-text-lg">首页</strong> / <small>index</small>
		</div>
	</div>
	<div class="am-u-sm-12">
		<table class="am-table am-table-bordered">
			<thead>
				<tr class="am-disabled">
					<td colspan="2" style="text-align: center">网站基本信息</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>用户名:<span class="t4"> ${curr_login_admin.user }</span></td>
					<td>IP：<span class="t4"> <%=RequestUtil.getIpAddr(request) %></span></td>
				</tr>
				<tr>
					<td>身份过期：<font class="t4">30 分钟</font></td>
					<td>现在时间：<font class="t4"><%=DateUtils.dateToString(new Date(),
					DateUtils.DATETIME_PATTERN)%></font></td>
				</tr>
				<tr>
					<td>服务器域名： <font class="t4">${basePath }</font>
					</td>
					<td>浏览器版本：<font class="t4"><%=request.getHeader("User-Agent")%></font>
					</td>
				</tr>
				<tr>
					<td>FSO文本读写： <b>√</b>
					</td>
					<td>数据库使用： <b>√</b>
					</td>
				</tr>
				<tr>
					<td>Jmail组件支持： <b>√</b>
					</td>
					<td>CDONTS组件支持： <b>√</b>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	<br />
	<br />
	<jsp:include page="base/js.jsp" />
</body>
<style>
.t4 {
	font: 12px 宋体;
	color: #800000;
}
</style>
</html>
