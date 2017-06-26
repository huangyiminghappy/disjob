<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<link href="<%=path %>/resources/hplus/css/bootstrap.min14ed.css" rel="stylesheet" type="text/css">
	<link href="<%=path %>/resources/hplus/css/font-awesome.min93e3.css" rel="stylesheet" type="text/css">
	<link href="<%=path %>/resources/hplus/css/plugins/jqgrid/ui.jqgridffe4.css" rel="stylesheet" type="text/css">
	<link href="<%=path %>/resources/hplus/css/animate.min.css" rel="stylesheet" type="text/css">
	<link href="<%=path %>/resources/hplus/css/style.min862f.css" rel="stylesheet">
	<link href="<%=path %>/resources/hplus/css/plugins/toastr/toastr.min.css" rel="stylesheet">
	<link href="<%=path %>/resources/hplus/css/plugins/sweetalert/sweetalert.css" rel="stylesheet">
	
	<title>服务信息</title>
</head>
<body class="gray-bg">
<div class="wrapper wrapper-content animated">
	 <div class="row">
	 	<div class="ibox float-e-margins">
            <div class="ibox-content">
            	<select id="s_queryCondition"  class="form-control m-b" name="queryCondition">
		        	<option value="" selected="selected">请选择显示任务数</option>
                       <option value="1">10</option>
                       <option value="2">20</option>
                       <option value="3">30</option>
                       <option value="3">所有</option>
		        </select>
                <div class="echarts" id="job_progress"></div>
            </div>
        </div>
	 </div>
	 <div class="row">
	 	<div class="ibox float-e-margins">
             <div class="ibox-content">
             	<select id="s_queryCondition"  class="form-control m-b" name="queryCondition">
		        	<option value="" selected="selected">请选择查询条件</option>
                       <option value="1">今天</option>
                       <option value="2">昨天</option>
                       <option value="3">近7天</option>
                       <option value="4">本月</option>
                       <option value="5">上个月</option>
		        </select>
                <div class="echarts" id="job_mastics"></div>
             </div>
         </div>
	 </div>
</div>

<script type="text/javascript" src="<%=path %>/resources/hplus/js/jquery.min.js"></script>
<script type="text/javascript" src="<%=path %>/resources/hplus/js/bootstrap.min.js"></script>
<script type="text/javascript" src="<%=path %>/resources/hplus/js/plugins/peity/jquery.peity.min.js"></script>
<script type="text/javascript" src="<%=path %>/resources/hplus/js/plugins/jqgrid/i18n/grid.locale-cnffe4.js"></script>
<script type="text/javascript" src="<%=path %>/resources/hplus/js/plugins/jqgrid/jquery.jqGrid.minffe4.js"></script>
<script type="text/javascript" src="<%=path %>/resources/hplus/js/content.min.js"></script>
<script type="text/javascript" src="<%=path %>/resources/hplus/js/jquery.easyui.min.js"></script>
<script src="<%=path %>/resources/hplus/js/plugins/toastr/toastr.min.js"></script>
<script src="<%=path %>/resources/hplus/js/plugins/sweetalert/sweetalert.min.js"></script>
<script src="<%=path %>/resources/hplus/js/plugins/echarts/echarts-all.js"></script>
<script type="text/javascript">
<%-- toastr.options = {// 定义操作结果弹出框的属性 
		  "closeButton": true,
		  "debug": true,
		  "progressBar": false,
		  "positionClass": "toast-bottom-full-width",
		  "showDuration": "400",
		  "hideDuration": "1000",
		  "timeOut": "3000",
		  "extendedTimeOut": "1000",
		  "showEasing": "swing",
		  "hideEasing": "linear",
		  "showMethod": "fadeIn",
		  "hideMethod": "fadeOut" };
var UrlConfig = {
	SerSearch: '<%=request.getContextPath() %>/app/service/ser/basic/search',
	SerList: '<%=request.getContextPath() %>/app/service/ser/basic/list',
	subJob: '<%=request.getContextPath() %>/app/service/ser/basic/subJob'
};
$(document).ready(function(){
}); --%>
$(function(){	
	var pv=echarts.init(document.getElementById("job_progress")),
	p = {/* 指定图表的配置项和数据*/
		tooltip: {
			trigger: 'axis',
	        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
	            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
	        }
	    },
	    title: {
	        left: 'center',
	        text: '正在执行任务--进度表',
	    },
	    legend:{data:["执行进度"]},
	    grid: {
	        left: '3%',
	        right: '4%',
	        bottom: '3%',
	        containLabel: true},
	    xAxis : [{
	            type : 'category',
	            data : ['job-0','job-1','job-2','job-3','job-4','job-5','job-6','job-7','job-8','job-9','job-10']}],
	    yAxis : [{type : 'value',
		    	axisLabel: {
	                formatter: '{value} %'
	            }
	    }],
	    series : [
	        {
	            name:'执行进度',
	            type:'bar',
	            barWidth : 50,
	            data:[90, 60, 20, 90, 70, 10, 20, 100, 70, 10, 100]
	        }]
	};
	pv.setOption(p),$(window).resize(pv.resize);
	
	var mv=echarts.init(document.getElementById("job_mastics")),
	m = {
	    title: {
	        text: '任务执行次数统计图'
	    },
	    tooltip : {
	        trigger: 'axis'
	    },
	    legend: {
	        data:['成功次数','失败次数']
	    },
	    toolbox: {
	        feature: {
	            saveAsImage: {}
	        }
	    },
	    grid: {
	        left: '3%',
	        right: '4%',
	        bottom: '3%',
	        containLabel: true
	    },
	    xAxis : [
	        {
	            type : 'category',
	            boundaryGap : false,
	            data : ["2016-07-07 01:41:23","2016-07-07 02:30:00","2016-07-07 03:51:06","2016-07-07 04:54:12","2016-07-07 05:32:45","2016-07-07 06:07:43","2016-07-07 07:17:43","2016-07-07 08:57:03"]
	        }
	    ],
	    yAxis : [
	        {
	            type : 'value',
            	axisLabel: {
                    formatter: '{value} 次'
                }
	        }
	    ],
	    series : [
	        {
	            name:'成功次数',
	            type:'line',
	            data:[400, 450, 201, 624, 500, 630, 410, 1000]
	        },
	        {
	            name:'失败次数',
	            type:'line',
	            data:[120, 221, 501, 134, 280, 130, 320, 600]
	        }
	    ]
	};
	mv.setOption(m),$(window).resize(mv.resize);
});
</script>
</body>
</html>