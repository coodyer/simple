<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<div class="am-topbar-brand">
	<strong>Simple</strong> <small>在线调试平台</small>
</div>

<button
	class="am-topbar-btn am-topbar-toggle am-btn am-btn-sm am-btn-success am-show-sm-only"
	data-am-collapse="{target: '#topbar-collapse'}">
	<span class="am-sr-only">导航切换</span> <span class="am-icon-bars"></span>
</button>

<div class="am-collapse am-topbar-collapse" id="topbar-collapse">

	<ul
		class="am-nav am-nav-pills am-topbar-nav am-topbar-right admin-header-list">
		<li class="am-dropdown" data-am-dropdown><a
			class="am-dropdown-toggle" data-am-dropdown-toggle
			href="#"> <span class="am-icon-users"></span> ${curr_login_admin.user } <span
				class="am-icon-caret-down"></span>
		</a>
			<ul class="am-dropdown-content">
				<li><a href="${basePath }debug/modifyAdmin.do" target="index"><span class="am-icon-cog"></span> 修改密码</a></li>

				<li><a href="${basePath }debug/loginOut.do"><span class="am-icon-power-off"></span> 注销登录</a></li>
			</ul></li>
		<li class="am-hide-sm-only"><a href="javascript:;"
			id="admin-fullscreen"><span class="am-icon-arrows-alt"></span> <span
				class="admin-fullText">开启全屏</span></a></li>
	</ul>
</div>