		function sysLogin() {
			var userName = localStorage.getItem("userName");
			var userPwd = localStorage.getItem("userPwd");
			if (userName != null && userName != '' && userPwd != null
					&& userPwd != '') {
				$("#userName").val(userName);
				$("#userPwd").val(userPwd);
				submitForm(1);
			}
		}
		sysLogin();