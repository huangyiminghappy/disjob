<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String uuid=request.getParameter("uuid");
String type=request.getParameter("type");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>ECharts</title>
    <script src="<%=path %>/resources/hplus/js/plugins/echarts/echarts-all.js"></script>
</head>
<body>
<div id="main" style="width: 850px;height:300px;"></div>
<script type="text/javascript">
var url = '<%=path %>/app/service/monitor/jobProgress';
var myChart = echarts.init(document.getElementById('main'));
myChart.showLoading();
$.post(url, {uuid:'<%=uuid %>',type:'<%=type %>'}, function(result){
	myChart.hideLoading();
	var option = eval('('+result.data+')');
	myChart.setOption(option);
},'json');
</script>
</body>
</html>