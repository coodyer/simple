<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld"%>
<%@ taglib prefix="fn" uri="/WEB-INF/tld/fn.tld"%>
<!doctype html>
<html class="no-js">
<head>
<jsp:include page="base/head.jsp" />
</head>
<body>
	<div class="admin-content">
		<div class="am-cf am-padding">
			<div class="am-fl am-cf">
				<strong class="am-text-primary am-text-lg">源码管理</strong> / <small>文件信息预览</small>
			</div>
		</div>
		<hr>
		<div class="am-panel am-panel-default">
			<div class="am-panel-hd am-cf"
				data-am-collapse="{target: '#collapse-panel-4'}">
				&nbsp; &nbsp;当前文件：${file }<span class="am-icon-chevron-down am-fr"></span>
			</div>
			<div id="collapse-panel-4" class="am-panel-bd am-collapse am-in">
				<div class="am-g">
					<fieldset>
						<c:if test="${!empty context}">
							<form class="am-form am-form-horizontal" onsubmit="return false">
								<div class="am-form-group">
									<label for="doc-vld-ta-2"><small>文件内容：</small></label>
									<textarea id="doc-vld-ta-2" name="remark" rows="25">${context}</textarea>
								</div>
							</form>
						</c:if>
						<c:if test="${!empty classInfo }">
							<div class="am-form-group" id="classInfo">
								<c:forEach items="${classInfo.annotations }" var="annotation">
									<span class="annotation">@${annotation.annotationType }
										<c:if test="${!empty annotation.fields }">
																(${annotation.fields })
															</c:if>

									</span>
									<br>
								</c:forEach>
								<label for="doc-vld-ta-2"><small><span
										class="pub">${classInfo.modifier}&nbsp;${field.isFinal?'final':'' }&nbsp;${classInfo.isAbstract?'abstract':''}${classInfo.isInterface?'interface':''}${classInfo.isEnum?'enum':''}</span>&nbsp;${classInfo.name}
										<c:if test="${!empty classInfo.superClass }">&nbsp;<b
												class="pub">extends</b>&nbsp;${classInfo.superClass }</c:if> <c:if
											test="${!empty classInfo.interfaces }">&nbsp;<b
												class="pub">implements</b>&nbsp;
												<c:forEach items="${classInfo.interfaces }" var="interf"
												varStatus="index">
												${interf.name }
												<c:if
													test="${index.index+1!=fn:length(classInfo.interfaces) }">,</c:if>
											</c:forEach>
										</c:if> </small></label>
								<c:if test="${!empty classInfo.enumInfo }">
									<table class="am-table am-table-bordered">
										<c:forEach items="${classInfo.enumInfo }" var="enumInfo">
											<tr>
												<td>
													<span class="blue">${enumInfo.key }</span>(${enumInfo.value.getMap() })
												</td>
											</tr>
										</c:forEach>
									</table>
								</c:if>
								<table class="am-table am-table-bordered">
									<thead>
										<tr>
											<th>字段列表</th>
										</tr>
									</thead>
									<tbody>
										<c:if test="${empty classInfo.fields}">
											<tr>
												<td colspan="1" style="text-align:center ">暂无字段</td>
											</tr>
										</c:if>
										<c:forEach items="${classInfo.fields}" var="field">
											<tr>
												<td><c:forEach items="${field.annotations }"
														var="annotation">
														<span class="annotation">@${annotation.annotationType }
															<c:if test="${!empty annotation.fields }">
																(${annotation.fields })
															</c:if>
														</span>
														<br>
													</c:forEach><b class="pub">${field.modifier }&nbsp;${field.isStatic?'static':'' }&nbsp;${field.isFinal?'final':'' }&nbsp;</b>
													${field.fieldType.name}&nbsp;&nbsp;<span class="para">${field.fieldName }</span>
													<c:if test="${!empty field.fieldValue }">&nbsp;=&nbsp;<span
															class="blue">${field.fieldValue }</span>
													</c:if>;</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
								<table class="am-table am-table-bordered">
									<thead>
										<tr>
											<th>方法列表</th>
										</tr>
									</thead>
									<tbody>
										<c:if test="${empty classInfo.methods}">
											<tr>
												<td colspan="1" style="text-align:center ">暂无方法</td>
											</tr>
										</c:if>
										<c:forEach items="${classInfo.methods}" var="method"
											varStatus="seq">
											<tr>
												<td>
													<form name="form${seq.index+1 }" action="serverMonitor.do"
														method="POST">
														<input type="hidden" name="file" value="${file }"><input
															type="hidden" name="key" value="${method.key }">
														<c:forEach items="${method.annotations }" var="annotation">
															<span class="annotation">@${annotation.annotationType }
																<c:if test="${!empty annotation.fields }">
																(${annotation.fields })
															</c:if>
															</span>
															<br>
														</c:forEach>
														<b class="pub">${method.modifier }&nbsp;${method.isStatic?'static':'' }&nbsp;${method.isFinal?'final':'' }&nbsp;
															${method.isAbstract?'abstract':'' }&nbsp;${method.isSynchronized?'synchronized':'' }</b>&nbsp;${method.returnType.name }&nbsp;${method.name }(
														<c:forEach items="${method.paramsType }" var="para"
															varStatus="index">
															<c:forEach items="${para.annotations }" var="annotation">
																<span class="annotation">@${annotation.annotationType }
																	<c:if test="${!empty annotation.fields }">
																(${annotation.fields })
															</c:if>
																</span>
															&nbsp;
														</c:forEach> 
														${para.fieldType }&nbsp; <span class="para">${para.fieldName }</span>
															<c:if
																test="${index.index+1!=fn:length(method.paramsType) }">,</c:if>
														</c:forEach>
														); <a
															href="javascript:document.form${seq.index+1 }.submit()"
															class="am-btn am-btn-default am-btn-xs right">方法监听</a>
													</form>
												</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</c:if>
					</fieldset>
					<fieldset>
						<button type="button" class="am-btn am-btn-default am-fr right"
							onclick="javascript:history.back()">返回</button>
					</fieldset>
				</div>
			</div>
		</div>
	</div>
</body>
<jsp:include page="base/js.jsp" />
<style>
.right {
	float: right;
}

.annotation {
	color: #949494;
}

.blue {
	color: blue;
}

.pub {
	color: #B74040;
}

.para {
	color: #776A6A;
}
</style>
<script>
	$("#classInfo").html($("#classInfo").html().replace(/\n/g, ""));
	$("#classInfo").html($("#classInfo").html().replace(/\r\n/g, ""));
	$("#classInfo").html($("#classInfo").html().replace(/	/g, ""));
	while ($("#classInfo").html().indexOf("&nbsp;&nbsp;") > 0) {
		$("#classInfo").html(
				$("#classInfo").html().replace(/&nbsp;&nbsp;/g, "&nbsp;"));
	}
</script>
</html>
