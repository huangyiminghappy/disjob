<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String groupName=request.getParameter("groupName");
String jobName=request.getParameter("jobName");
String glimit=request.getParameter("limit");
String goffset=request.getParameter("offset");
String title=request.getParameter("from") == null ? "job执行明细" : "job报警查询";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<link href="<%=path %>/resources/hplus/css/bootstrap.min14ed.css" rel="stylesheet" type="text/css">
	<link href="<%=path %>/resources/hplus/css/font-awesome.min93e3.css" rel="stylesheet" type="text/css">
	<link href="<%=path %>/resources/hplus/css/animate.min.css" rel="stylesheet" type="text/css">
	<link href="<%=path %>/resources/hplus/css/style.min862f.css" rel="stylesheet">
	<link href="<%=path %>/resources/hplus/css/plugins/bootstrap-table/bootstrap-table.min.css" rel="stylesheet">
	<link href="<%=path %>/resources/hplus/css/plugins/toastr/toastr.min.css" rel="stylesheet">
	<link href="<%=path %>/resources/hplus/css/plugins/sweetalert/sweetalert.css" rel="stylesheet">
	<title><%=title %></title>
	<style type="text/css">
		.button_right{ float:right;}
	</style>
</head>
<body>
<div class="modal inmodal fade" id="modalProgress" tabindex="-1" role="dialog"  aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
        	<div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                <h4 class="modal-title"><font color='#FF0000' id="sub_uuid"></font>（执行进度详情）</h4>
            </div>
	        <div class="modal-body">
            </div>
        </div>
    </div>
</div>
<div class="wrapper wrapper-content  animated fadeInRight">
    <div class="row">
            <div class="ibox">
                <div class="ibox-content">
                    <div class="input-group">
                    	<input id="s_jobUuid" type="text" placeholder="输入查询的任务uuid" class="input form-control" style="width:40%">
                    	<input placeholder="执行开始时间" class="form-control layer-date" id="query_start">
                        <input placeholder="执行结束时间" class="form-control layer-date" id="query_end">
                        <button type="button" class="btn btn btn-primary" onclick="tableReload()"> <i class="fa fa-search"></i> 搜索</button>
                        <span class="input-group-btn">
                             <button type="button" class="btn btn btn-primary" onclick="goBack()">返回</button><!-- javascript:history.back(); -->
                        </span>
                    </div>
                    <br>
                    <p>注：<font color='#FF0000'>每行</font>是一个任务的一次执行！</p>
                    <table id="tableList">
                    </table>
                </div>
            </div>
    </div>
</div>
<script type="text/javascript" src="<%=path %>/resources/hplus/js/jquery.min.js"></script>
<script type="text/javascript" src="<%=path %>/resources/hplus/js/bootstrap.min.js"></script>
<script type="text/javascript" src="<%=path %>/resources/hplus/js/content.min.js"></script>
<script type="text/javascript" src="<%=path %>/resources/hplus/js/plugins/layer/laydate/laydate.js"></script>
<script src="<%=path %>/resources/hplus/js/plugins/bootstrap-table/bootstrap-table.min.js"></script>
<script src="<%=path %>/resources/hplus/js/plugins/bootstrap-table/bootstrap-table-mobile.min.js"></script>
<script src="<%=path %>/resources/hplus/js/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
<script src="<%=path %>/resources/hplus/js/plugins/toastr/toastr.min.js"></script>
<script src="<%=path %>/resources/hplus/js/plugins/sweetalert/sweetalert.min.js"></script>
<script type="text/javascript">
toastr.options = {/* 定义操作结果弹出框的属性 */
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
		  "hideMethod": "fadeOut"
	};
	
var gName = "<%=groupName %>";
var jName = "<%=jobName %>";
var limit = "<%=glimit %>";
var offset = "<%=goffset %>";
var UrlConfig = {
	JobDetail: '<%=path %>/app/service/monitor/jobDetail',
	JobDetailQuery: '<%=path %>/app/service/monitor/jobDetailQuery',
	JobProgress: '<%=path %>/app/page/monitor/jobExeProgress',
	PauseJobExecute: '<%=path %>/app/service/monitor/stopJobExecute',
	back: "<%=path %>/app/page/job/info"
};

$(document).ready(function() {
	var start={elem:"#query_start",format:"YYYY-MM-DD hh:mm:ss",min:"2016-07-01 00:00:00",max:laydate.now(),istime:true,istoday:false,choose:function(datas){end.min=datas;end.start=datas}};
	var end={elem:"#query_end",format:"YYYY-MM-DD hh:mm:ss",min:"2016-07-01 00:00:00",max:laydate.now(),istime:true,istoday:false,choose:function(datas){start.max=datas}};laydate(start);laydate(end);
	updateTable();
	if(window.location.href.indexOf("from=main") != -1){
		$('.input-group-btn').css('visibility','hidden');
	}
});

function updateTable(){
	$('#tableList').bootstrapTable({
		method: 'post',
		url: UrlConfig.JobDetailQuery,
		height: 700,
		dataType: "json",
		pagination: true,
		"queryParamsType": "limit",
		contentType: "application/x-www-form-urlencoded",
		singleSelect: false,
		pageSize: 20,
		pageNumber:1,
		search: false, //不显示 搜索框
		columns: [
		          {title: 'uuid',field: 'uuid'},
		          {title: '调度服务器IP',field: 'scheduleSip'},
		          {title: '业务服务器IP',field: 'businessSip'},
		          {title: '调度开始时间',field: 'scheduleStart'},
		          {title: '调度结束时间',field: 'scheduleEnd'},
		          {title: '执行开始时间',field: 'executeStart'},
		          {title: '执行结束时间',field: 'executeEnd'},
		          {title: '耗时(秒)',field: 'timeConsuming'},
		          {
		        	  title: '状态',
		        	  field: 'currentStatus',
		        	  formatter:function(value,row,index){  
                 		if(value == 1) 
                      		return '成功';
                 		else if(value == 0)
                 			return '失败';
                 		else
                 			return '未知';
                      } 
		          },
		          {
		        	  title: '是否强制终止',
		        	  field: 'killprocess',
		        	  formatter:function(value,row,index){  
                 		if(value == 1) 
                      		return '是';
                 		else
                 			return '否';
                      } 
		          },
		          {title: '创建时间',field: 'createdAt'},
		          {title: '更新时间',field: 'updatedAt'},
		          {title: '异常位置',field: 'errorLocation'},
		          {title: '异常类型',field: 'errorType'},
		          {title: '异常原因',field: 'errorReason'},
		          {
                      title: '操作',
                      field: 'id',
                      align: 'center',
                      formatter:function(value,row,index){  
                    		var e = '<button type="button" class="btn btn-primary" onclick="showProgress(\''+ row.uuid +"','"+ row.errorType + '\')">执行详情</button>'
                   			/* var e = '<a href="#" mce_href="#" onclick="showProgress(\''+ row.uuid +"','"+ row.errorType + '\')">详情</a> '; */
                   			var rdisable = row.executeEnd==null?"<button type=‘button' class='btn btn-danger' onclick='pauseExecute(\""+ row.uuid + "\")'>终止执行</button>":"";
                   			/* var rdisable = row.executeEnd==null?"<a href='#' onclick='pauseExecute(\""+ row.uuid + "\")'>终止</a>":""; */
                   			/* return e; */  
                        	/* op = "<div class='btn-group'>"+
                            "<button data-toggle='dropdown' class='btn btn-primary dropdown-toggle'>操作 <span class='caret'></span>"+
                            "</button>"+
                            "<ul class='dropdown-menu'>"+
                                "<li>"+e+"</li>"+rdisable+
                            "</ul>"+
                          "</div>"; */
                          return e+'<br>'+rdisable;
                    } 
                  }
		],
		showColumns: false, //不显示下拉框（选择显示的列）
		sidePagination: "server", //服务端请求
		queryParams: queryParams,
		responseHandler: responseHandler
		//onClickRow: showProgress
	 });
}
//ajax返回的数据处理
function responseHandler(res) {
	 if (res.successful) { 
		return {
			"rows": res.rows,
			"total": res.total
		};
	 }else{
		 toastr["error"](res.msg,"操作结果：");/* 显示结果 */
		 return {
				"rows": [],
				"total": 0
			};
	 }
	/* } else {
		return {
			"rows": [],
			"total": 0
		};
	} */
}
//传递的参数
function queryParams(params) {
	return {
		groupName: gName,//组名
		jobName: jName,//任务名
		uuid: $("#s_jobUuid").val(),/* 要查询的UUID */
		start: $("#query_start").val(),/* 要查询的起始时间 */
		end: $("#query_end").val(),/* 要查询的结束时间 */
		limit: params.limit,/* 页面大小 */
		offset: params.offset/* 偏移量 */
	};
}
//展示进度图表
function showProgress(uuid,errorType){/* 从后台获取进度信息，加载数据后，打开modal模态框进行展示 */
	$("#sub_uuid").text(uuid);
	$(".modal-body").load(UrlConfig.JobProgress+"?uuid="+uuid+"&type="+errorType);
	$('#modalProgress').modal('show');
}
function pauseExecute(uid){
	$.post(UrlConfig.PauseJobExecute, {uuid:uid}, function(result) {/* 查询所有的组信息*/
		if (result.successful) {/* 是否成功 */
			toastr["info"]("操作成功","操作结果：");/* 显示结果 */
		}else {
			toastr["info"](result.msg,"操作结果：");/* 显示结果 */
		}		
	}, 'json');
}
//刷新
function tableReload(){/* 刷新表格数据 */
	$('#tableList').bootstrapTable('refresh');
}

function goBack(){
	window.location = UrlConfig.back + "?groupName=" + gName +"&jobName="+jName +"&limit="+limit+"&offset="+offset;
}
</script>
</body>
</html>