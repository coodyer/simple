<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!doctype html>
<html class="no-js">
<head>
<jsp:include page="base/head.jsp" />
</head>
<body>
	<form class="am-form am-form-horizontal" method="post" id="dataform">
		<div class="admin-content">
			<div class="am-cf am-padding">
				<div class="am-fl am-cf">
					<strong class="am-text-primary am-text-lg">管理员</strong> / <small>修改密码</small>
				</div>
			</div>
			<hr>
			<div class="am-panel am-panel-default">
				<div class="am-panel-hd am-cf"
					data-am-collapse="{target: '#collapse-panel-4'}">
					后台用户编辑<span class="am-icon-chevron-down am-fr"></span>
				</div>
				<div id="collapse-panel-4" class="am-panel-bd am-collapse am-in">
					<div class="am-g">
						<div class="am-u-sm-12 am-u-md-4 am-u-md-push-8"></div>
						<div class="am-u-sm-12 am-u-md-8 am-u-md-pull-4">

							<div class="am-form-group">
								<label for="user-name" class="am-u-sm-3 am-form-label"><small>用户名</small></label>
								<div class="am-u-sm-9">
									<input type="text" id="userName" placeholder="用户名"
										value="${curr_login_admin.user}" name="username"
										datatype="s4-16" errormsg="请输入正确的用户名(4-16位)" sucmsg="输入正确"
										nullmsg="请输入用户名"> <small>请输入用户名</small>
								</div>
							</div>

							<div class="am-form-group">
								<label for="user-phone" class="am-u-sm-3 am-form-label"><small>请输入旧密码</small></label>
								<div class="am-u-sm-9">
									<input type="password" id="userPwd" placeholder="请输入旧密码"
										name="password" datatype="*6-16" errormsg="请输入正确的密码(6-16位)！"
										sucmsg="输入正确" nullmsg="请输入密码"> <small>请输入旧密码</small>
								</div>
							</div>

							<div class="am-form-group">
								<label class="am-u-sm-3 am-form-label"><small>请输入新密码</small></label>
								<div class="am-u-sm-9">
									<input type="password" id="newPwd" placeholder="请输入新密码"
										name="newPwd"> <small>请输入新密码</small>
								</div>
							</div>
							<div class="am-form-group">
								<label class="am-u-sm-3 am-form-label"><small>请重新输入新密码</small></label>
								<div class="am-u-sm-9">
									<input type="password" id="newPwd" placeholder="请输入新密码"
										 datatype="*" recheck="newPwd" errormsg="两次输入的密码不一致"
										sucmsg="输入正确" nullmsg="请重新输入密码" placeholder="请重新输入密码">
									<small>请输入新密码</small>
								</div>
							</div>
							<div class="am-form-group" style="height: 10px;">
								<span style="color: red;height: 20px;float: right" id="msg"></span>
							</div>
						</div>
					</div>
					<div class="am-form-group">
						<div class="am-u-sm-9 am-u-sm-push-3">
							<button type="submit" class="am-btn am-btn-primary">保存修改</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</form>
</body>
<jsp:include page="base/js.jsp" />
<script>
	$("#dataform").Validform({
		label : ".lable",
		showAllError : false,
		postonce : true,
		tiptype : function(msg, o, cssctl) {
			var objtip = $("#msg");
			cssctl(objtip, o.type);
			objtip.text(msg);
		},
		beforeSubmit : function(curform) {
			submitForm();
			return false;
		}
	});
	function submitForm() {
		$.ajax({
			type : "POST",
			dataType : 'json',
			data : $("#dataform").serialize(),
			url : 'saveCurrAdmin.do',
			timeout : 60000,
			success : function(json) {
				alert(json.msg);
				if (json.code == 0) {
					location.reload(true);
					return;
				}

			},
			error : function() {
				alert("系统繁忙");
			}

		});
	}
</script>
</html>
