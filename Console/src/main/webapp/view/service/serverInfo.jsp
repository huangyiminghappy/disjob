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
	<link href="<%=path %>/resources/hplus/css/animate.min.css" rel="stylesheet" type="text/css">
	<link href="<%=path %>/resources/hplus/css/style.min862f.css" rel="stylesheet">
	<link href="<%=path %>/resources/hplus/css/plugins/bootstrap-table/bootstrap-table.min.css" rel="stylesheet">
	<link href="<%=path %>/resources/hplus/css/plugins/toastr/toastr.min.css" rel="stylesheet">
	<link href="<%=path %>/resources/hplus/css/plugins/sweetalert/sweetalert.css" rel="stylesheet">
	<title>服务信息</title>
</head>
<body>
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
      		<div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h2 class="modal-title" id="myModalLabel"><font color='#FF0000' id="sub_ip"></font>-任务信息列表</h2>
            </div>
            <div class="modal-body">
            		<table id="sub_list_dg"></table>
					<!-- <div id="psub_list_dg"></div> -->
			</div>
        </div>
    </div>
</div>
<div class="wrapper wrapper-content  animated fadeInRight">
   <div class="row">
       <div class="ibox">
           <div class="ibox-content">
               <div class="input-group">
                	<input id="s_serviceName"  type="text" placeholder="输入服务器名" class="input form-control">
               	<span class="input-group-btn">
                    <button type="button" class="btn btn btn-primary" onclick="reloadMasterTable()"> <i class="fa fa-search"></i> 搜索</button>
              	</span>
               </div>
               <br>
               <button class='btn btn-danger' type='button' onclick='divideJob()'>均分任务到集群</button>
               <table id="service_list_dg"></table>
               <!-- <div class="clients-list">
                   <ul class="nav nav-tabs">
                       <li class="active"><a data-toggle="tab" href="#tab-1"><i class="fa fa-user"></i>服务列表</a>
                       </li>
                   </ul>
                   <div class="tab-content">
                   	<div class="jqGrid_wrapper">
                   		<table id="service_list_dg"></table>
						<div id="pservice_list_dg"></div>
                   	</div>
                   </div>
               </div> -->
           </div>
       </div>
   </div>
</div>
<script type="text/javascript" src="<%=path %>/resources/hplus/js/jquery.min.js"></script>
<script type="text/javascript" src="<%=path %>/resources/hplus/js/bootstrap.min.js"></script>
<script type="text/javascript" src="<%=path %>/resources/hplus/js/content.min.js"></script>
<script type="text/javascript" src="<%=path %>/resources/hplus/js/jquery.easyui.min.js"></script>
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
		  "hideMethod": "fadeOut" };
var UrlConfig = {
	SerList: '<%=path %>/app/service/ser/basic/list',
	subJob: '<%=path %>/app/service/ser/basic/subJob',
	divideJob: '<%=path %>/app/service/job/info/divideJob'
};
var $mastertable = $('#service_list_dg'),$subtable = $('#sub_list_dg');

$(document).ready(function(){
	masterTable();
	subJobTable();
});

function masterTable(){
	$mastertable.bootstrapTable({
		method: 'post',
		url: UrlConfig.SerList,
		/* height: 700, */
		dataType: "json",
		pagination: true,
		"queryParamsType": "limit",
		contentType: "application/x-www-form-urlencoded",
		singleSelect: false,
		pageSize: 14,
		pageNumber:1,
		search: false, //不显示 搜索框
		uniqueId: "jobName",//以任务名为唯一索引
		columns: [
		          {title: '服务器名',field: 'hostName'},
		          {title: 'IP',field: 'ip'},
		          {title: '权重',field: 'weight'},
		          {title: '有效权重',field: 'effectiveWeight'},
		          {title: '当前权重',field: 'currentWeight'},
		          {
		        	  title: '是否master',
		        	  field: 'master',
		        	  formatter:function(value,row,index){  
	                 		if(value) 
	                      		return "<font color='#FF0000'>是</font>";
	                 		return '否';
                      } 
		          },
		          {
		        	  title: '是否在线',
		        	  field: 'active',
		        	  formatter:function(value,row,index){  
	                 		if(value) 
	                      		return "<font color='#FF0000'>是</font>";
	                 		return '否';
                      }
		          },
		          {
                      title: '操作',
                      field: 'act',
                      align: 'center',
                      width: 70,
                      formatter:function(value,row,index){  
                    	  	scan = "<button class='btn btn-primary' data-toggle='modal' data-target='#myModal' type='button' onclick='scanJob(\"" + row.ip + "\")'>查看任务信息</button>";
                   			return scan;  
                    }
                  }
		],
		showColumns: false, //不显示下拉框（选择显示的列）
		sidePagination: "server", //服务端请求
		queryParams: function(params){
			return {
				name: $("#s_serviceName").val(),/* 服务器名 */
				limit: params.limit,/* 页面大小 */
				offset: params.offset/* 偏移量 */
			};
		},
		responseHandler: function(res){
			return {
				"rows": res.rows,
				"total": res.total
			};
		}
		//onClickRow: showProgress
	 });
}
function subJobTable(){
	$subtable.bootstrapTable({
		method: 'post',
		url: UrlConfig.subJob,
		dataType: "json",
		pagination: true,
		"queryParamsType": "limit",
		contentType: "application/x-www-form-urlencoded",
		singleSelect: false,
		pageSize: 8,
		pageNumber:1,
		search: false, //不显示 搜索框
		uniqueId: "jobName",//以任务名为唯一索引
		columns: [
		          {title: '组名',field: 'group'},
		          {title: '任务名',field: 'name'}
		],
		showColumns: false, //不显示下拉框（选择显示的列）
		sidePagination: "server", //服务端请求
		queryParams: function(params){
			return {
				ip: $("#sub_ip").text(),/* 服务器ip */
				limit: params.limit,/* 页面大小 */
				offset: params.offset/* 偏移量 */
			};
		},
		responseHandler: function(res){
			return {
				"rows": res.rows,
				"total": res.total
			};
		}
		//onClickRow: showProgress
	 });
}
function reloadMasterTable() {
	$mastertable.bootstrapTable('refresh');
}
function scanJob(ip){
	$("#sub_ip").text(ip);
	$subtable.bootstrapTable('refresh');
	$subtable.bootstrapTable('selectPage', 1);//恢复到首页
}
function divideJob(){
	/*确认提示匡 */
	swal({title:"确定操作？",text:"请谨慎操作！",
		type:"warning",
		showCancelButton:true,
		confirmButtonColor:"#DD6B55",
		confirmButtonText:"均分任务",
		closeOnConfirm:true},
		function(){
			$.post(UrlConfig.divideJob, {}, function(result){/* 序列化表单数据，定义操作完成函数的处理 */
		    	if (result.successful) {/* 是否成功 */
		        	toastr["info"]("操作成功","操作结果：");/* 显示结果 */
		        } else {
		        	toastr["info"](result.msg,"操作结果：");/* 显示结果 */
		        }
		    },'json');
	})
}
</script>
</body>
</html>