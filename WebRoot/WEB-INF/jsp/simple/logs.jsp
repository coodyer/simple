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
					<strong class="am-text-primary am-text-lg">日志管理</strong> / <small>日志列表</small>
				</div>
			</div>
			<hr>
			<div class="am-panel am-panel-default">
				<div class="am-panel-hd am-cf"
					data-am-collapse="{target: '#collapse-panel-base'}">
					&nbsp; &nbsp;日志列表<span class="am-icon-chevron-down am-fr"
						style="display: none"></span>
				</div>
				<div id="collapse-panel-base" class="am-panel-bd am-collapse am-in">
					<table class="am-table am-table-bordered">
						<thead>
							<tr>
								<th>编号</th>
								<th>所属方法</th>
								<th>日志内容</th>
								<th>时间</th>
								<th>线程ID</th>
							</tr>
						</thead>
						<tbody>
							<c:if test="${empty logs}">
								<tr>
									<td colspan="5" style="text-align:center ">暂无数据</td>
								</tr>
							</c:if>
							<c:forEach items="${logs }" var="log">
								<tr>
									<td>${log.id }</td>
									<td>${log.method }</td>
									<td>${log.log }</td>
									<td><fmt:formatDate value="${log.runTime }"	pattern="yyyy-MM-dd HH:mm:ss" /></td>
									<td>${log.threadId }</td>
								</tr>
							</c:forEach>
						</tbody>
						<thead>
							<tr class="am-disabled">
								<th colspan="6"><jsp:include page="base/page.jsp" /></th>
							</tr>
						</thead>
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
