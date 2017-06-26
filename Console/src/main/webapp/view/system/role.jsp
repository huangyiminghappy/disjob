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
	
	<script type="text/javascript" src="<%=path %>/resources/hplus/js/jquery.min.js"></script>
	<script type="text/javascript" src="<%=path %>/resources/hplus/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="<%=path %>/resources/hplus/js/plugins/peity/jquery.peity.min.js"></script>
	<script type="text/javascript" src="<%=path %>/resources/hplus/js/plugins/jqgrid/i18n/grid.locale-cnffe4.js"></script>
	<script type="text/javascript" src="<%=path %>/resources/hplus/js/plugins/jqgrid/jquery.jqGrid.minffe4.js"></script>
	<script type="text/javascript" src="<%=path %>/resources/hplus/js/content.min.js"></script>
	
	<script type="text/javascript" src="<%=path %>/resources/hplus/js/jquery.easyui.min.js"></script>
	<title>角色管理</title>
</head>
<body>
<div class="wrapper wrapper-content  animated fadeInRight">
    <div class="row">
        <div class="col-sm-8">
            <div class="ibox">
                <div class="ibox-content">
                    <div class="input-group">
                        <input type="text" placeholder="查找角色" class="input form-control">
                        <span class="input-group-btn">
                             <button type="button" class="btn btn btn-primary"> <i class="fa fa-search"></i> 搜索</button>
                        </span>
                    </div>
                    <div class="clients-list">
                        <ul class="nav nav-tabs">
                            <li class="active"><a data-toggle="tab" href="#tab-1"><i class="fa fa-user"></i>角色列表</a>
                            </li>
                        </ul>
                        <div class="tab-content">
                        	<div class="jqGrid_wrapper">
                        		<table id="role_list_dg"></table>
								<div id="prole_list_dg"></div>
                        	</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-sm-4">
            <div class="ibox ">
                <div class="ibox-content">
                    <div class="tab-content">
                        <div id="contact-1" class="tab-pane active">
						<h2>角色信息</h2>
						<form class="form-horizontal m-t" id="role_info_form" method="post">
			               <div class="form-group">
			                   <label class="col-sm-3 control-label">角色标识：</label>
			                   <div class="col-sm-8">
			                       <input name="roleId" minlength="1" type="text" class="form-control" required="" aria-required="true">
			                   </div>
			               </div>
			               <div class="form-group">
			                   <label class="col-sm-3 control-label">角色名称：</label>
			                   <div class="col-sm-8">
			                       <input name="roleName" minlength="1" type="text" class="form-control" required="" aria-required="true">
			                   </div>
			               </div>
			               <div class="form-group">
			                   <label class="col-sm-3 control-label">备注：</label>
			                   <div class="col-sm-8">
			                   		<input name="remark" type="text" class="form-control">
			                   </div>
			               </div>
			               <div class="form-group">
			                   <div class="col-sm-4 col-sm-offset-3">
			                       <button class="btn btn-primary" type="button" onclick="saveRole()">保存</button>
			                       <button class="btn btn-primary" type="button" onclick="resetParam()">重置</button>
			                   </div>
			               </div>
			           </form>
                       </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="<%=path %>/resources/hplus/js/content.min.js"></script>
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
		  "timeOut": "1000",
		  "extendedTimeOut": "1000",
		  "showEasing": "swing",
		  "hideEasing": "linear",
		  "showMethod": "fadeIn",
		  "hideMethod": "fadeOut" };
var UrlConfig = {
	SysRoleList: '<%=path %>/app/sys/role/list',
	SysRoleAdd: '<%=path %>/app/sys/role/add',
	SysRoleUpdate: '<%=path %>/app/sys/role/update',
	SysRoleDelete: '<%=path %>/app/sys/role/delete',
	SysRoleMenu: '<%=path %>/app/sys/role/menu',
	SysRoleSaveMenu: '<%=path %>/app/sys/role/saveMenu'
};
var urlChange = UrlConfig.SysRoleAdd;
$(document).ready(function(){
	$.jgrid.defaults.styleUI="Bootstrap";
	updateTable();
});
function updateTable(){
	$("#role_list_dg").jqGrid({
        url: UrlConfig.SysRoleList,
		datatype:"json",/*返回的数据格式*/
		height:600,
		autowidth:true,
		shrinkToFit:true,
		reloadAfterSubmit: true,/*自动加载更新*/
		rowNum:20,
		rowList:[10,20,30],
		colNames:["角色标识","角色名称","备注",""],/*定义列名*/
		colModel:[/*定义列属性*/
		          {name:"roleId",index:"roleId",editable:true,width:100,search:true},
		          {name:"roleName",index:"roleName",editable:true,width:100,search:true},
		          {name:"remark",index:"remark",editable:true,width:100,sortable:false},
		          {name:"act",index:'act',width:50,sortable:false}],
		pager:"#prole_list_dg",
		viewrecords:true,
		onSelectRow: function(id){
            urlChange = UrlConfig.SysRoleUpdate;;/*把url设置为更新*/
             $('#role_info_form').form('load',$('#role_list_dg').getRowData(id));/*把选中的行数据加载到form表单中*/
      	},gridComplete : function() {
            var ids = $("#role_list_dg").jqGrid('getDataIDs');//读取所有数据行
            for ( var i = 0; i < ids.length; i++) {//为行添加处理按钮
              pr = "<button class='btn btn-outline btn-default' type='button' onclick='editPriv(" + ids[i] + ")'>设置权限</button>";
              de = "<button class='btn btn-outline btn-danger' type='button' onclick='deleteRole(" + ids[i] + ")'>删除</button>";
              $("#role_list_dg").jqGrid('setRowData', ids[i],{act:pr+de});
            }
       }});
	jQuery("#role_list_dg").jqGrid('navGrid', "#prole_list_dg", {//屏蔽默认的按钮
		edit : false,
		add : false,
		del : false,
		search:true
	  });
}
function resetParam(){ 
	urlChange = UrlConfig.SysRoleAdd;/* 恢复默认URL */
	$('#role_info_form').form('clear');/* 清空form表单数据 */
}
function saveRole(){ 
	$.post(urlChange, $('#role_info_form').serialize(), function(result){/* 序列化表单数据，定义操作完成函数的处理 */
    	if (result.successful) {
        	toastr["info"]("操作成功","操作结果：");/*显示结果*/
        	$("#role_list_dg").trigger("reloadGrid");/* 重新加载table数据表 */
        	resetParam();/* 重置参数变量*/
        } else {
        	toastr["info"](result.msg,"操作结果：");/*显示结果*/
        }
    },'json');
}
function deleteRole(id) {
	/*确认提示匡 */
	swal({title:"您确定要删除吗",text:"删除后将无法恢复，请谨慎操作！",
		type:"warning",
		showCancelButton:true,
		confirmButtonColor:"#DD6B55",
		confirmButtonText:"删除",
		closeOnConfirm:true},
		function(){
			var row = $('#role_list_dg').getRowData(id);/* 获取选择的行数据 */
            $.post(UrlConfig.SysRoleDelete, {roleId:row.roleId}, function(result){/* 角色ID作为参数，定义操作完成函数的处理 */
            	if (result.successful) {
                	toastr["info"]("操作成功","操作结果：");/*显示结果*/
                	$("#role_list_dg").trigger("reloadGrid");/* 重新加载table数据表 */
                	resetParam();/* 重置参数变量*/
                } else {
                	toastr["info"](result.msg,"操作结果：");/*显示结果*/
                }
            },'json');
	})
}

function editPriv(id) {
	toastr["warning"]("未实现","警告：");
}
</script>

</body>
</html>