<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String groupName=request.getParameter("groupName");
String jobName=request.getParameter("jobName");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>ECharts</title>
    <script src="<%=path %>/resources/hplus/js/plugins/echarts/echarts-all.js"></script>
</head>
<body>
<label style="float:right;">
    <input type="radio" name="search_r" id="0" checked="checked"/>今天
    <input type="radio" name="search_r" id="1"/>昨天
    <input type="radio" name="search_r" id="2"/>近7天
    <input type="radio" name="search_r" id="3"/>本月
    <input type="radio" name="search_r" id="4"/>上个月
    <input type="radio" name="search_r" id="5"/>今年
</label>
<!-- <div><font color='#FF0000'>注释：</font>点击可查看异常信息列表！</div> -->
<div id="main" style="width: 900px;height:300px;"></div>
<script type="text/javascript">
var url = '<%=path %>/app/service/monitor/jobStatistics';
var myChart = echarts.init(document.getElementById('main'));
/* myChart.showLoading(); */
/* myChart.on('click', function (params) {
    alert("点击："+params);
}); */
reload(0);
function reload(mid){
	myChart.showLoading();
	$.post(url, {groupName:'<%=groupName %>',jobName:'<%=jobName %>',id:mid}, function(result){
		myChart.hideLoading();
		var option = eval('('+result.data+')');
		myChart.setOption(option);
	},'json');
}

$("input[type='radio']").click(function(){
	myChart.clear();
    var id= $(this).attr("id");
    reload(id);
});
</script>
</body>
</html>