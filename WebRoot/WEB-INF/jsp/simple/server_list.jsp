<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld"%>
<%@ taglib prefix="fmt" uri="/WEB-INF/tld/fmt.tld"%>
<%@ taglib prefix="fn" uri="/WEB-INF/tld/fn.tld" %> 
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
					<strong class="am-text-primary am-text-lg">源码管理</strong> / <small>包目录列表</small>
				</div>
			</div>
			<hr>
			<div class="am-panel am-panel-default">
				<div class="am-panel-hd am-cf"
					data-am-collapse="{target: '#collapse-panel-base'}">
					&nbsp; &nbsp;当前目录：${currFile }<span
						class="am-icon-chevron-down am-fr" style="display: none"></span>
				</div>
				<div id="collapse-panel-base" class="am-panel-bd am-collapse am-in">
					<table class="am-table am-table-bordered">
						<thead>
							<tr>
								<th>文件列表</th>
								<th>文件大小</th>
								<th>创建时间</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td><a href="?file=${parentFile}"> <img width="30px"
										src="${basePath}simple//assets/img/file.png" />上级目录
								</a></td>
								<td>-</td>
								<td>-</td>
							</tr>
							<c:if test="${empty files}">
								<tr>
									<td colspan="3" style="text-align:center ">暂无数据</td>
								</tr>
							</c:if>
							<c:forEach items="${files }" var="file">
								<tr>
									<td><c:if test="${file.type==0 }">
											<a href="?file=${file.path }"> <img width="30px"
												src="${basePath}simple//assets/img/file.png" />${fn:replace(file.path, currFile, '')}
											</a>
										</c:if> <c:if test="${file.type!=0 }">
											<a href="fileInfo.do?file=${file.path }"> <img
												width="30px"
												src="${basePath}simple//assets/img/${file.suffix=='class'?'java':'txt' }.png" />${fn:replace(file.path, currFile, '')}
											</a>
										</c:if></td>
									<td>${file.size==null?'-':file.size }</td>
									<td><fmt:formatDate value="${file.time }"
											pattern="yyyy-MM-dd HH:mm:ss" /></td>
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
</html>
