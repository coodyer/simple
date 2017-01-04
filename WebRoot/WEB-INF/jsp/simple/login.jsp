<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>管理员登录</title>
<link href="${basePath }simple/assets/css/login.css" rel="stylesheet"
	type="text/css" />
</head>

<body>
	<div id="adminwindow"
		style="width:100%;height:100%;background:#ffffff;text-align:center;">
		<div
			style="width:533px;height:248px;margin:160px auto;background:url(${basePath }simple/assets/images/loginbg.gif)">
			<form action="" method="post" id="loginForm" name="loginForm"
				onsubmit="return false;">
				<table width="90%" height="248" border="0" align="center">
					<tr>
						<td width="155" align="right" style="padding-right:30px"><img
							src="${basePath}simple/assets/images/2.png" width="103" height="114"></td>
						<td><div id="notice" style="color: red"></div>
							<table border="0" cellpadding="1" cellspacing="1"
								style="margin:10px 0px">

								<tr>
									<td align="center" width="80">管理员帐号</td>
									<td rowspan="4" width="3"></td>
									<td><font color="#FFFFFF"> <input type="text"
											id="user" name="username" size="18" class="input"
											style="width:130px" datatype="*4-32"
											errormsg="请输入正确的用户名(4-16位)" sucmsg="输入正确" nullmsg="请输入用户名" />
									</font></td>
								</tr>
								<tr>
									<td align="center" width="80">管理员密码</td>
									<td><font color="#FFFFFF"> <input type="password"
											id="password" name="password" size="18" class="input"
											style="width:130px" datatype="*6-16"
											errormsg="请输入正确的密码(6-16位)！" sucmsg="输入正确" nullmsg="请输入密码" />
									</font></td>
								</tr>
								<tr>
									<td width="80" align="center">图形验证码</td>
									<td><font color="#FFFFFF"> </font>
										<table border="0" cellspacing="0" cellpadding="0" height="20">
											<tr>
												<td width="1"><font color="#FFFFFF"> <input
														datatype="n4-6" errormsg="请输入正确的验证码(4位)" sucmsg="输入正确"
														nullmsg="请输入验证码" type="text" id="ImgCode" name="verCode"
														style="width:65px" class="input" />
												</font></td>
												<td style="padding:0px 3px"><img onclick="refVerCode()"
													id="currVerCode" src="${basePath}debug/verCode.do"
													width="60" height="18"
													style="border:1px #dddddd solid;cursor:pointer" /></td>
											</tr>
										</table> <font color="#FFFFFF"> </font></td>
								</tr>
								<tr>
									<td width="80" height="39" align="center"><input
										name="act" type="hidden" id="act" value="adminlogin"></td>
									<td height="30"><input id="Submit" type="submit"
										name="Submit" value="管理员登录" class="button" /></td>
								</tr>
							</table></td>
					</tr>
				</table>
			</form>
		</div>
	</div>
</body>
<!--[if lt IE 9]>
<script src="http://libs.baidu.com/jquery/1.11.1/jquery.min.js"></script>
<script src="http://cdn.staticfile.org/modernizr/2.8.3/modernizr
.js"></script>
<script src="http://127.0.0.1:8080/XssApp/assets/js/amazeui.ie8polyfill.min.js"></script>
<![endif]-->
<script src="${basePath}simple/assets/js/jquery.min.js"></script>
<script src="${basePath}simple/assets/js/validform.min.js"></script>
<script type="text/javascript">
	function refVerCode() {
		var imgSrc = "${basePath }debug/verCode.do?"
				+ Math.round(Math.random() * 1000000);
		setTimeout(function() {
			document.getElementById("currVerCode").src = imgSrc;
		}, 0);

	}
	$("#loginForm").Validform({
		label : ".lable",
		showAllError : false,
		postonce : true,
		tiptype : function(msg, o, cssctl) {
			var objtip = $("#notice");
			cssctl(objtip, o.type);
			objtip.text(msg);
		},
		beforeSubmit : function(curform) {
			ajaxlogin();
			return false;
		}
	});
	function ajaxlogin() {
		$
				.ajax({
					target : 'div#notice',
					async : true,
					cache : false,
					type : "POST",
					dataType : 'json',
					data : $("#loginForm").serialize(),
					url : 'doLogin.do',
					timeout : 60000,
					success : function(json) {
						$("#notice").html(json.msg);
						$("#Submit").removeAttr("disabled");
						if (json.code == 0) {
							$("#notice").html("");
							window.location.href = 'index.do';
						} else {
							$("#ImgCode").val("");
							refVerCode();
						}
					},
					error : function() {
						$("#Submit").removeAttr("disabled");
						$("#notice").html("系统繁忙！");
						$("#ImgCode").val("");
						refVerCode();
					}
				});
	}
</script>
</html>
