<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!--[if lt IE 9]>
<script src="http://libs.baidu.com/jquery/1.11.1/jquery.min.js"></script>
<script src="http://cdn.staticfile.org/modernizr/2.8.3/modernizr.js"></script>
<script src="${basePath}simple/assets/js/amazeui.ie8polyfill.min.js"></script>
<![endif]-->

	<!--[if (gte IE 9)|!(IE)]><!-->
	<script src="${basePath}simple/assets/js/jquery.min.js"></script>
	<!--<![endif]-->
<style>
body {
	font-size: 12px;word-break:break-all;
}
static.am-list-border>li {
    padding: 0.2rem;
}

.am-form input[type=number], .am-form input[type=search], .am-form input[type=text], .am-form input[type=password], .am-form input[type=datetime], .am-form input[type=datetime-local], .am-form input[type=date], .am-form input[type=month], .am-form input[type=time], .am-form input[type=week], .am-form input[type=email], .am-form input[type=url], .am-form input[type=tel], .am-form input[type=color], .am-form select, .am-form textarea, .am-form-field {
    font-size: 1.4rem;
}
td{max-width: 500px;word-break:break-all;min-width: 100px;}
.admin-content{
height: 80%;}
footer{text-align: right;width: 100%;height: 25px;} 
hr, ol, p, pre, ul {
    margin: 0 0 0.6rem;
}
.am-padding {
    padding: 1.2rem;
}
.am-list, .am-topbar {
    margin-bottom: 0.6rem;
}
.am-panel {
    margin-bottom: 1px;
}
body, pre {
    line-height: 1.0;
}
</style>
	<script src="${basePath}simple/assets/js/amazeui.min.js"></script>
	<script src="${basePath}simple/assets/js/app.js"></script>
	<script src="${basePath}simple/assets/js/validform.min.js"></script>