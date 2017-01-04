<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld"%>
<%@ taglib prefix="fmt" uri="/WEB-INF/tld/fmt.tld"%>
<!doctype html>
<html class="no-js">
<head>
<jsp:include page="base/head.jsp" />
</head>
<body>
	<form class="am-form am-form-horizontal" method="post" id="dataform" onsubmit="return false"
		name="searchForm">
		<div class="admin-content">
			<div class="am-cf am-padding">
				<div class="am-fl am-cf">
					<strong class="am-text-primary am-text-lg">系统管理</strong> / <small>缓存列表</small>
				</div>
			</div>
			<hr>
			<div class="am-panel am-panel-default">
				<div id="collapse-panel-base" class="am-panel-bd am-collapse am-in">
					<div class="am-form-group am-form-success">
						<label for="doc-vld-name-2"><small>缓存KEY(精准)：</small></label> <input
							class="left" type="text" id="cacheKey" name="cacheKey" style="width:70%">
						<p class="left">&nbsp;</p>
						<button type="submit"  onclick="delCacheTrigger()"
							class="am-btn am-btn-success am-btn-xs left">立即清理</button>
					</div>
					<table class="am-table am-table-bordered">
						<thead>
							<tr>
								<th>缓存KEY</th>
								<th>操作</th>
							</tr>
						</thead>
						<tbody>
							<c:if test="${empty entitys}">
								<tr>
									<td colspan="2" style="text-align:center ">暂无数据</td>
								</tr>
							</c:if>
							<c:forEach items="${entitys }" var="entity">
								<tr>
									<td>${entity.fieldValue}</td>
									<td><a href="javascript:delCache('${entity.fieldValue }')">清理缓存</a></td>
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
<style>
.left {
	
}
</style>
<script>
	function delCache(key) {
		if (!confirm("该KEY所有缓存都将清理,是否继续?")) {
			return;
		}
		$.ajax({
			type : "POST",
			dataType : 'json',
			data : 'key=' + key,
			url : 'cacheClean.do',
			timeout : 60000,
			success : function(json) {
				alert(json.msg);
			},
			error : function() {
				alert("系统繁忙");
			}

		});
	}
	function delCacheTrigger() {
		var key=$("#cacheKey").val();
		delCache(key);
	}
</script>
</html>
