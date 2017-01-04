<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<html class="no-js">
<head>
<jsp:include page="base/head.jsp" />
</head>
<body>
	<header class="am-topbar admin-header">
		<jsp:include page="base/nav.jsp" />
	</header>
	<!-- sidebar end -->
	<jsp:include page="base/menu.jsp" />
	<!-- content start -->
	<div class="admin-content" style="height: auto">
		<iframe src="base.do" name="index" id="index" 
			  style="background-color=transparent;margin-top: 15px; height: 830px;" width="100%" > </iframe>
	</div>
	<%-- <footer>
		<jsp:include page="base/foot.jsp" />
	</footer> --%>
	<jsp:include page="base/js.jsp" />
</body>
<script type="text/javascript" language="javascript">   


</script>
<style>
body{margin:0; padding:0;}
</style>
</html>
