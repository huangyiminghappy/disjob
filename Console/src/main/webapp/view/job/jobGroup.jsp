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
	<title>job组管理</title>
</head>
<body>
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
      		<div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h2 class="modal-title" id="myModalLabel"><font color='#FF0000'>组信息</font></h2>
            </div>
            <div class="modal-body">
            <form class="form-horizontal m-t" id="group_info_form" method="post">
               <div class="form-group">
                   <label class="col-sm-3 control-label">组名：</label>
                   <div class="col-sm-8">
                       <input name="groupName" minlength="1" type="text" class="form-control" required="" aria-required="true">
                   </div>
               </div>
               <div class="form-group">
                   <label class="col-sm-3 control-label">备注：</label>
                   <div class="col-sm-8">
                       <input type="text" class="form-control" name="remark">
                   </div>
               </div>
               <div class="form-group">
                   <div class="col-sm-4 col-sm-offset-3">
                       <button class="btn btn-primary" type="button" onclick="saveGroup()">保存</button>
                       <button class="btn btn-primary" type="button" onclick="javascript:$('#group_info_form').form('clear')">重置</button>
                   </div>
               </div>
           </form>
            </div>
        </div>
    </div>
</div>
<div class="wrapper wrapper-content  animated fadeInRight">
    <div class="row">
    <div class="col-sm-8">
       <div class="ibox">
           <div class="ibox-content">
               <div class="input-group">
                   <input id="s_GroupName" type="text" placeholder="查找组名（或使用正则表达式匹配）" class="input form-control">
                   <span class="input-group-btn">
                        <button type="button" class="btn btn btn-primary" onclick="searchInfo()"> <i class="fa fa-search"></i> 搜索</button>
                   </span>
               </div>
               <br>
               <table id="jobGroup_table_list"></table>
           </div>
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
		SysGroupList: '<%=path %>/app/service/job/group/listPage',
		SysGroupAdd: '<%=path %>/app/service/job/group/add',
		SysGroupUpdate: '<%=path %>/app/service/job/group/update',
		SysGroupDelete: '<%=path %>/app/service/job/group/delete',
		SysGroupSearch: '<%=path %>/app/service/job/group/search'
};
var $table = $('#jobGroup_table_list');
$(document).ready(function(){
	$table.bootstrapTable({
		method: 'post',
		url: UrlConfig.SysGroupList,
		/* height: 750, */
		dataType: "json",
		pagination: true,
		"queryParamsType": "limit",
		contentType: "application/x-www-form-urlencoded",
		singleSelect: false,
		pageSize: 14,
		pageNumber:1,
		search: false, //不显示 搜索框
		uniqueId: "groupName",//以任务名为唯一索引
		columns: [		       
		          {title: '任务组名称',field: 'groupName'},
		          {title: '备注',field: 'remark'},
		          {
                      title: '操作',
                      field: 'act',
                      align: 'center',
                      width: 130,
                      formatter:function(value,row,index){  
                    	  op = "<div class='btn-group'>"+
                          "<button data-toggle='dropdown' class='btn btn-primary dropdown-toggle'>操作 <span class='caret'></span>"+
                          "</button>"+
                          "<ul class='dropdown-menu'>"+
                              /* "<li><a href='#' onclick='addNewGroup()'>新建</a></li>"+ */
                              /* "<li><a href='#' data-toggle='modal' data-target='#myModal' onclick='editGroup(\""+ row.groupName + "\")'>编辑</a></li>"+ */
                              "<li><a href='#' onclick='deleteGroup(\""+ row.groupName + "\")'>删除</a></li>"+
                          "</ul>"+
                        "</div>";
                    	  /* ed = "<button class='btn btn-outline btn-default' data-toggle='modal' data-target='#myModal' type='button' onclick='editGroup(\"" + row.groupName + "\")'>编辑</button>";
                          de = "<button class='btn btn-outline btn-danger' type='button' onclick='deleteGroup(\"" + row.groupName + "\")'>删除</button>"; */
                          return op;
                    }
                  }
		],
		showColumns: false, //不显示下拉框（选择显示的列）
		sidePagination: "server", //服务端请求
		queryParams: queryParams,
		responseHandler: responseHandler,
		toolbar:'<div id="tabletoolbar"><button class="btn btn-primary" type="button" onclick="addNewGroup()" id="bindJob">新增</button></div>',
		//onClickRow: showProgress
	});
});
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
		limit: params.limit,/* 页面大小 */
		offset: params.offset/* 偏移量 */
	};
}
function addNewGroup(){
	$('#myModal').modal('show');
}

function saveGroup(){ 
	$.post(UrlConfig.SysGroupAdd, $('#group_info_form').serialize(), function(result){/* 序列化表单数据，定义操作完成函数的处理 */
    	if (result.successful) {/* 是否成功 */
    		$('#myModal').modal('hide');
        	toastr["info"]("操作成功","操作结果：");/* 显示结果 */
        	reloadTable();
        	$('#group_info_form').form('clear');/* 清空form表单数据 */
        } else {
        	toastr["info"](result.msg,"操作结果：");/* 显示结果 */
        }
    },'json');
}

function editGroup(gName) {
	$('#group_info_form').form('load',$table.bootstrapTable("getRowByUniqueId",gName));/*把选中的行数据加载到form表单中*/
}

function deleteGroup(gName) {
	/*确认提示匡 */
	swal({title:"您确定要删除吗",text:"删除后将无法恢复，请谨慎操作！",
		type:"warning",
		showCancelButton:true,
		confirmButtonColor:"#DD6B55",
		confirmButtonText:"删除",
		closeOnConfirm:true},
		function(){
            $.post(UrlConfig.SysGroupDelete, {groupName:gName}, function(result){/* 把组名作为参数 */
            	if (result.successful) {
                	toastr["info"]("操作成功","操作结果：");/* 显示结果 */
                	reloadTable();
                	$('#group_info_form').form('clear');/* 清空form表单数据 */
                } else {
                	toastr["info"](result.msg,"操作结果：");/*显示结果*/
                }
            },'json');
	})
}
function reloadTable(){
	$table.bootstrapTable('refresh');
}
function searchInfo(){
	toastr["warning"]("暂时未实现！","警告：");/* 显示结果 */
	//-----------------------------目前不支持，暂时屏蔽实现-----------------------------------
	//var gName = $("#s_GroupName").val()||"";/*读取搜索的组名*/
	//$("#jobGroup_table_list").jqGrid('setGridParam', {/*更新查询参数，重新加载table数据*/
	//    url : UrlConfig.SysGroupSearch+"?groupName=" + gName,
	//}).trigger("reloadGrid");
}
</script>

</body>
</html>