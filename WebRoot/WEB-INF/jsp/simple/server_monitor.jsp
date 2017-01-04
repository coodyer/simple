<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld"%>
<%@ taglib prefix="fmt" uri="/WEB-INF/tld/fmt.tld"%>
<%@ taglib prefix="fn" uri="/WEB-INF/tld/fn.tld"%> 
<!doctype html>
<html class="no-js">
<head>
<jsp:include page="base/head.jsp" />
</head>
<body>
	<div id="dataform">
		<div class="admin-content">
			<div class="am-cf am-padding">
				<div class="am-fl am-cf">
					<strong class="am-text-primary am-text-lg">源码管理</strong> / <small>方法监听</small>
				</div>
			</div>
			<hr>
			<div class="am-panel am-panel-default">
				<div class="am-panel-hd am-cf"
					data-am-collapse="{target: '#collapse-panel-4'}">
					方法监听<span class="am-icon-chevron-down am-fr"></span>
				</div>
				<div class="am-g">
					<fieldset>
						<c:forEach items="${classInfo.annotations }" var="annotation">
							<span class="annotation">@${annotation.annotationType }
								<c:if test="${!empty annotation.fields }">
																(${annotation.fields })
															</c:if>
							</span>
							<br>
						</c:forEach>
						<label for="doc-vld-ta-2"><small><span class="pub">&nbsp;${classInfo.modifier}&nbsp;${field.isFinal?'final':'' }&nbsp;${classInfo.isAbstract?'abstract':''}${classInfo.isInterface?'interface':''}${classInfo.isEnum?'enum':''}</span>&nbsp;${classInfo.name}
								<c:if test="${!empty classInfo.superClass }">&nbsp;<b
										class="pub">extends</b>&nbsp;${classInfo.superClass }</c:if> <c:if
									test="${!empty classInfo.interfaces }">&nbsp;<b
										class="pub">implements</b>&nbsp;
																	${classInfo.interfaces }
											</c:if> </small></label> <br>
						<c:forEach items="${method.annotations }" var="annotation">
							<span class="annotation">@${annotation.annotationType}
								<c:if test="${!empty annotation.fields }">
																(${annotation.fields })
															</c:if>
							</span>
							<br>
						</c:forEach>
						<b class="pub">&nbsp;${method.modifier }&nbsp;${method.isStatic?'static':'' }&nbsp;${method.isFinal?'final':'' }&nbsp;
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
							<c:if test="${index.index+1!=fn:length(method.paramsType)}">,</c:if>
						</c:forEach>
						); <a href="javascript:location.reload(true);"
							class="am-btn am-btn-default am-btn-xs right">拉取数据</a>
						<p class="right">&nbsp;</p>
						<a href="javascript:starMonitor('${key }',${isRun==1?0:1 })"
							class="am-btn am-btn-default am-btn-xs right" id="starMonitor">${isRun==1?'取消监听':'开始监听' }</a>
						<p class="right">&nbsp;</p>
					</fieldset>
					<fieldset>
						<ul
							class="am-list am-list-static am-list-border am-list-striped am-list-xs">
							<li>
								<form class="am-form am-form-horizontal" method="post"
									id="debugForm0">
									<input name="key" value="${key }" type="hidden">
									<div class="am-panel am-panel-default">
										<div class="am-panel-hd am-cf"
											data-am-collapse="{target: '#collapse-panel-5'}">
											方法调试<span class="am-icon-chevron-down am-fr"></span>
										</div>
										<div id="collapse-panel-5" class="padding am-collapse">
											<div class="am-form-group">
												<label for="doc-vld-name-2" style="width: 100%;"><small>入参：<a
														href="javascript:serverDebug(0);"
														class="am-btn am-btn-default am-btn-xs right">执行</a></small></label>
												<textarea name="input">${initParas }</textarea>
											</div>
											<div class="am-form-group">
												<label for="doc-vld-name-2"><small>出参：</small></label>
												<textarea name="result" id="result0"></textarea>
											</div>
										</div>
									</div>
								</form>
							</li>
							<c:forEach items="${monitors }" var="monitor" varStatus="seq">
								<li>
									<form class="am-form am-form-horizontal" method="post"
										id="debugForm${seq.index+1 }">
										<input name="key" value="${key }" type="hidden">
										<div class="am-panel am-panel-default">
											<div class="am-panel-hd am-cf"
												data-am-collapse="{target: '#collapse-panel-li${seq.index+1 }'}">
												方法执行记录(<fmt:formatDate value="${monitor.runTime }"
													pattern="yyyy-MM-dd HH:mm:ss" />)
												<span class="am-icon-chevron-down am-fr"></span>
											</div>
											<div id="collapse-panel-li${seq.index+1 }" class="padding">
												<div class="am-form-group">
													<label for="doc-vld-name-2" style="width: 100%;"><small>入参(<fmt:formatDate value="${monitor.runTime }"
													pattern="yyyy-MM-dd HH:mm:ss:SSS" />)：<a
															href="javascript:serverDebug(${seq.index+1 });"
															class="am-btn am-btn-default am-btn-xs right">DeBUG</a></small></label>
													<textarea name="input">${monitor.input }</textarea>
												</div>
												<div class="am-form-group">
													<label for="doc-vld-name-2"><small>出参(<fmt:formatDate value="${monitor.resultTime }"
													pattern="yyyy-MM-dd HH:mm:ss:SSS" />)：</small></label>
													<textarea name="remark" id="result${seq.index+1 }">${monitor.output }</textarea>
												</div>
											</div>
										</div>
									</form>
								</li>
							</c:forEach>
						</ul>
					</fieldset>
					<fieldset>
					<button type="button" class="am-btn am-btn-default am-fr right" onclick="javascript:history.back()">返回</button>
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

.padding {
	padding: .6rem 1.25rem;
}
</style>
<script>
	$("#dataform").html($("#dataform").html().replace(/\n/g, ""));
	$("#dataform").html($("#dataform").html().replace(/\r\n/g, ""));
	$("#dataform").html($("#dataform").html().replace(/	/g, ""));
	while ($("#dataform").html().indexOf("&nbsp;&nbsp;") > 0) {
		$("#dataform").html(
				$("#dataform").html().replace(/&nbsp;&nbsp;/g, "&nbsp;"));
	}
	function starMonitor(key, isRun) {
		$.ajax({
			type : "POST",
			dataType : 'json',
			data : 'key=' + key + '&isRun=' + isRun,
			url : 'serverDoMonitor.do',
			timeout : 60000,
			success : function(json) {
				alert(json.msg);
				location.reload(true);
			},
			error : function() {
				alert("系统繁忙");
			}
		});
	}

	function serverDebug(index) {
		$.ajax({
			type : "POST",
			dataType : 'json',
			data : $("#debugForm" + index).serialize(),
			url : 'serverDebug.do',
			timeout : 60000,
			success : function(json) {
				$("#result" + index).html(json.datas);
				alert(json.msg);
			},
			error : function() {
				alert("系统繁忙");
			}
		});
	}
</script>
</html>
