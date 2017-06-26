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
	
	<title>job报警管理</title>
</head>
<body>
<div class="modal fade" id="editModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
      		<div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h2 class="modal-title" id="myModalLabel"><font color='#FF0000'>编辑报警信息</font></h2>
            </div>
            <div class="modal-body">
            <form class="form-horizontal" id="alarm_edit_form" method="post" title="任务信息">
               <div class="form-group">
                   <label class="col-sm-3 control-label">任务组：<font color='#FF0000'>*</font></label>
                   <div class="col-sm-8">
                       <input readonly="readonly" name="groupName" minlength="1" type="text" class="form-control" required="" aria-required="true">
                   </div>
               </div>
              <div class="form-group">
                   <label class="col-sm-3 control-label">是否开启：</label>
                   <div class="col-sm-8">
                       <label>
                           <input type="checkbox" class="checkbox" checked="" value="true" name="onOff">
                       </label>
                   </div>
               </div>           
               <div class="form-group">
                   <label class="col-sm-3 control-label">rtx报警接收人列表：<font color='#FF0000'>(以,分隔)</font></label>
                   <div class="col-sm-8">
                       <input type="text" class="form-control" name="alarmRtx">
                   </div>
               </div>
               <div class="form-group">
               		<div class="col-sm-4 col-sm-offset-3">
               			<button class="btn btn-primary" type="button" onclick="saveJob()">保存</button>
                       <button class="btn btn-primary" type="button" onclick="javascript:$('#alarm_edit_form').form('clear')">重置</button>
                   	</div>
               </div>
            </form>
            </div>
        </div>
    </div>
</div>
<div class="wrapper wrapper-content  animated fadeInRight">
    <div class="row">
            <div class="ibox">
                <div class="ibox-content">
                    <div class="input-group">
                        <input id="s_groupName" type="text" placeholder="输入任务组" class="input form-control">
                        <span class="input-group-btn">
                             <button type="button" class="btn btn btn-primary" onclick="searchReload()"> <i class="fa fa-search"></i> 搜索</button>
                        </span>
                    </div>
                    <br>
                    <table id="jobAlarm_table_list"  class="table table-hover table-bordered results"></table>
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
	  "hideMethod": "fadeOut"
};
var UrlConfig = {
	alarmList: '<%=path %>/app/service/job/alarm/list',
	alarmAdd: '<%=path %>/app/service/job/alarm/add',
	alarmUpdate: '<%=path %>/app/service/job/alarm/update',
	alarmDelete: '<%=path %>/app/service/job/alarm/delete'
};

var $table = $('#jobAlarm_table_list');

$(document).ready(function(){
	initTable();
});

function initTable(num){
	$table.bootstrapTable({
		method: 'post',
		url: UrlConfig.alarmList,
		height: 800,
		dataType: "json",
		pagination: true,
		"queryParamsType": "limit",
		contentType: "application/x-www-form-urlencoded",
		singleSelect: false,
		search: false, //不显示 搜索框
		pageSize: 13,
		pageNumber:1,
		uniqueId: "groupName",//以任务名为唯一索引
		columns: [
		          {title: '任务组',field: 'groupName'},
		          {title: '创建时间',field: 'createdAt'},
		          {title: '最近修改时间',field: 'updatedAt'},
		          {
		        	  title: '是否开启报警',
		        	  field: 'onOff',
		        	  formatter:function(value,row,index){  
                 		if(value) 
                      		return "<font color='#FF0000'>是</font>";
                 		return '否';
                      } 
		          },
		          {title: 'rtx报警接收人列表',field: 'alarmRtx'},
		          {
                      title: '操作',
                      field: 'act',
                      align: 'center',
                      formatter:function(value,row,index){  
                   		op = "<div class='btn-group'>"+
	                            "<button data-toggle='dropdown' class='btn btn-primary dropdown-toggle'>操作 <span class='caret'></span>"+
	                            "</button>"+
	                            "<ul class='dropdown-menu'>"+
	                                "<li><a href='#' data-toggle='modal' data-target='#editModal' onclick='editAlarm(\""+ row.groupName + "\")'>编辑</a></li>"+
	                                "<li><a href='#' onclick='deleteAlarm(\""+ row.groupName + "\")'>删除</a></li>"+
	                            "</ul>"+
	                          "</div>";
                        return op;  
                    }
                  }
		],
		showColumns: false, //不显示下拉框（选择显示的列）
		sidePagination: "server", //服务端请求
		queryParams: queryParams,
		responseHandler: responseHandler
		//onClickRow: showProgress
	 });
	//窗口大小改变
	$(window).resize(function () {
	    $table.bootstrapTable('resetView', {
	        height: 600
	    });
	});
}
//ajax返回的数据处理
function responseHandler(res) {
	return {
		"rows": res.rows,
		"total": res.total
	};
}
//传递的参数
function queryParams(params) {
	return {
		groupName:$("#s_groupName").val(),/* 组名 */
		limit: params.limit,/* 页面大小 */
		offset: params.offset/* 偏移量 */
	};
}
//搜索
function searchReload(){
	tableReload();
	$table.bootstrapTable('selectPage', 1); //恢复到首页
}
//刷新
function tableReload(){/* 刷新表格数据 */
	$table.bootstrapTable('refresh'); //刷新
}
function saveJob(){
	$.post(UrlConfig.alarmUpdate, $('#alarm_edit_form').serialize(), function(result){/* 序列化表单数据，定义操作完成函数的处理 */
    	if (result.successful) {/* 是否成功 */
    		$('#editModal').modal('hide');
        	toastr["info"]("操作成功","操作结果：");/* 显示结果 */
        	tableReload();
        	$('#alarm_edit_form').form('clear'); /* 清空form表单数据 */
        } else {
        	toastr["info"](result.msg,"操作结果：");/* 显示结果 */
        }
    },'json');
}
function editAlarm(groupName){
	$('#alarm_edit_form').form('load',$table.bootstrapTable("getRowByUniqueId",groupName));
}
function deleteAlarm(groupName){
	/*确认提示匡 */
	toastr["warning"]("你没有这个权限！","警告：");
	/* swal({title:"您确定要删除吗",text:"删除后将无法恢复，请谨慎操作！",
		type:"warning",
		showCancelButton:true,
		confirmButtonColor:"#DD6B55",
		confirmButtonText:"删除",
		closeOnConfirm:true},
		function(){
            $.post(UrlConfig.alarmDelete, {groupName:groupName}, function(result){
            	if (result.successful) {
                	toastr["info"]("操作成功","操作结果：");
                	tableReload();
                } else {
                	toastr["info"](result.msg,"操作结果：");
                }
            },'json');
	}) */
}
</script>

</body>
</html>