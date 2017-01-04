<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld"%>
<!-- sidebar start -->
<div class="admin-sidebar am-offcanvas" id="admin-offcanvas">
	<div class="am-offcanvas-bar admin-offcanvas-bar">
		<ul class="am-list admin-sidebar-list">
			<li class="admin-parent"><a class="am-cf" href="#"
				target="index"><span class="am-icon-file"></span> 在线调试<span
					class="am-icon-angle-right am-fr am-margin-right"></span></a>
				<ul class="am-list am-collapse admin-sidebar-sub am-in"
					id="collapse-nav">
					<li><a href="${basePath}debug/base.do" class="am-cf"
						target="index"><span class="am-icon-check"></span>基本信息</a></li>
					<li><a href="${basePath}debug/server.do" class="am-cf"
						target="index"><span class="am-icon-check"></span>资源管理</a></li>
					<li><a href="${basePath}debug/monitors.do" class="am-cf"
						target="index"><span class="am-icon-check"></span>监控列表</a></li>
					<li><a href="${basePath}debug/logs.do" class="am-cf"
						target="index"><span class="am-icon-check"></span>日志管理</a></li>

					<li><a href="${basePath}debug/cache.do" class="am-cf"
						target="index"><span class="am-icon-check"></span>缓存管理</a></li>
				</ul></li>
		</ul>

		<div class="am-panel am-panel-default admin-sidebar-panel">
			<div class="am-panel-bd">
				<p>
					<span class="am-icon-bookmark"></span> 技术支持
				</p>
				<p>代码描绘人生：QQ644556636</p>
			</div>
		</div>

	</div>
</div>