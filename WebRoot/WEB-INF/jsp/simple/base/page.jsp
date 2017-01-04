<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<link href="${basePath }assets/css/page.css" rel="stylesheet">
<div class="page">
	<div id="kkpager" class="nowrap">
		<div>
			<form method="post" name="pageForm">
				<input name="maxId" value="${maxId }" type="hidden" /> <span
					class="pageBtnWrap"> <a href="javascript:nextPage()"
					${(pager.currentPage<=1)?"class='disabled'":""}>下一页</a>
				</span>
			</form>
			<script>
				function nextPage() {
					document.searchForm.submit();
				}
			</script>
		</div>
		<div style="clear:both;"></div>
	</div>
</div>
<style>
body {
	font-size: 12px;
	word-break: break-all;
}

static.am-list-border>li {
	padding: 0rem;
}

.am-panel {
	margin-bottom: 10px;
}

.pageBtnWrap {
	float: right;
}
</style>