<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <title>job绑定</title>
    <link href="<%=path %>/resources/hplus/css/bootstrap.min14ed.css" rel="stylesheet" type="text/css">
	<link href="<%=path %>/resources/hplus/css/font-awesome.min93e3.css" rel="stylesheet" type="text/css">
	<link href="<%=path %>/resources/hplus/css/animate.min.css" rel="stylesheet" type="text/css">
	<link href="<%=path %>/resources/hplus/css/style.min862f.css" rel="stylesheet">
	<link href="<%=path %>/resources/hplus/css/plugins/bootstrap-table/bootstrap-table.min.css" rel="stylesheet">
	<link href="<%=path %>/resources/hplus/css/plugins/toastr/toastr.min.css" rel="stylesheet">
	<link href="<%=path %>/resources/hplus/css/plugins/sweetalert/sweetalert.css" rel="stylesheet">
	<link href="<%=path %>/resources/hplus/css/plugins/chosen/chosen.css" rel="stylesheet">
</head>
<body class="gray-bg">
	<div class="wrapper wrapper-content animated fadeInRight">
		<div class="row">
			<div class="col-sm-12">
				<div class="ibox float-e-margins">
					<div class="ibox-title">
						<!-- <h5>job绑定</h5> -->
						<!-- <select id="mode">
							<option value="bind" selected="selected">绑定</option>
							<option value="rebind">重新绑定</option>
						</select> -->
					</div>
					<div class="ibox-content">
						<div class="table-responsive">
							<table id="groupListTable" class="table table-hover table-bordered results"></table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>


	<script type="text/javascript" src="<%=path%>/resources/hplus/js/jquery.min.js"></script>
	<script type="text/javascript" src="<%=path%>/resources/hplus/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="<%=path%>/resources/hplus/js/content.min.js"></script>
	<script type="text/javascript" src="<%=path %>/resources/hplus/js/jquery.easyui.min.js"></script>
	<script src="<%=path %>/resources/hplus/js/plugins/bootstrap-table/bootstrap-table.min.js"></script>
	<script src="<%=path %>/resources/hplus/js/plugins/bootstrap-table/bootstrap-table-mobile.min.js"></script>
	<script src="<%=path %>/resources/hplus/js/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
	<script src="<%=path %>/resources/hplus/js/plugins/toastr/toastr.min.js"></script>
	<script src="<%=path %>/resources/hplus/js/plugins/sweetalert/sweetalert.min.js"></script>
	
	<script type="text/javascript" src="<%=path %>/resources/hplus/js/plugins/layer/laydate/laydate.js"></script>
    <script type="text/javascript" src="<%=path %>/resources/hplus/js/plugins/chosen/chosen.jquery.js"></script>
    <script type="text/javascript" src="<%=path %>/resources/ejob.js"></script>
    <script type="text/javascript" src="<%=path %>/resources/job/jobBind.js"></script>
</body>
</html>
