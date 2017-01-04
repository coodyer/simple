<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld"%>
<%@ taglib prefix="fmt" uri="/WEB-INF/tld/fmt.tld"%>
<!doctype html>
<html class="no-js">
<head>
<jsp:include page="base/head.jsp" />
</head>
<body>
	<form class="am-form am-form-horizontal" method="post" id="dataform"
		name="searchForm">
		<div class="admin-content">
			<div class="am-cf am-padding">
				<div class="am-fl am-cf">
					<strong class="am-text-primary am-text-lg">源码管理</strong> / <small>监控列表</small>
				</div>
			</div>
			<hr>
			<div class="am-panel am-panel-default">
				<div class="am-panel-hd am-cf"
					data-am-collapse="{target: '#collapse-panel-base'}">
					&nbsp; &nbsp;监控列表<span class="am-icon-chevron-down am-fr"
						style="display: none"></span>
				</div>
				<div id="collapse-panel-base" class="am-panel-bd am-collapse am-in">
					<table class="am-table am-table-bordered">
						<thead>
							<tr>
								<th>监控KEY</th>
								<th>操作</th>
							</tr>
						</thead>
						<tbody>
							<c:if test="${empty keys}">
								<tr>
									<td colspan="3" style="text-align:center ">暂无数据</td>
								</tr>
							</c:if>
							<c:forEach items="${keys }" var="key">
								<tr>
									<td>${key }</td>
									<td><a href="serverMonitor.do?key=${key }">查看详情</a>&nbsp;&nbsp;<a href="javascript:cancelMonitor('${key }')">取消监控</a></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		</form>
</body>
<jsp:include page="base/js.jsp" />
<script>
function cancelMonitor(key) {
		$.ajax({
			type : "POST",
			dataType : 'json',
			data : "isRun=0&key="+key,
			url : 'serverDoMonitor.do',
			timeout : 60000,
			success : function(json) {
				alert(json.msg);
				if (json.code == 0) {
					location.reload(true);
				}
			},
			error : function() {
				alert("系统繁忙");
			}
		});
	}
</script>
</html>
