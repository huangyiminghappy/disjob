<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<link href="../../../resources/hplus/css/bootstrap.min14ed.css" rel="stylesheet" type="text/css">
	<link href="../../../resources/hplus/css/font-awesome.min93e3.css" rel="stylesheet" type="text/css">
	<link href="../../../resources/hplus/css/animate.min.css" rel="stylesheet" type="text/css">
	<link href="../../../resources/hplus/css/style.min862f.css" rel="stylesheet">
	<link href="../../../resources/hplus/css/plugins/bootstrap-table/bootstrap-table.min.css" rel="stylesheet">
	<link href="../../../resources/hplus/css/plugins/toastr/toastr.min.css" rel="stylesheet">
	<link href="../../../resources/hplus/css/plugins/sweetalert/sweetalert.css" rel="stylesheet">
	<title>权限授予</title>
</head>
<body>
<form id="form" class="form-inline">
<select id="username"  class="form-control m-b" name="username" ><!-- style="width:20%;" -->
	<option value="" selected="selected">请选择用户</option>
</select>
<select id="jobgroup"  class="form-control m-b" name="jobgroup" ><!-- style="width:20%;" -->
	<option value="" selected="selected">请选择jobgroup</option>
</select>
<input type="checkbox" id="reader"  class="form-control m-b" name="reader" >读权限<!-- style="width:20%;" -->
<input type="checkbox" id="owner"  class="form-control m-b" name="owner" >所有者权限<!-- style="width:20%;" -->
</form>

<form id="form2" class="form-inline">
<select id="username_permit"  class="form-control m-b" name="username_permit" ><!-- style="width:20%;" -->
	<option value="" selected="selected">请选择用户</option>
</select>
<select id="permit_item"  class="form-control m-b" name="permit_item" ><!-- style="width:20%;" -->
	<option value="" selected="selected">请选择权限</option>
</select>
<input type="checkbox" id="permit"  class="form-control m-b" name="permit" >权限<!-- style="width:20%;" -->
</form>
<script src="../../../resources/hplus/js/jquery.min.js"></script>
<script src="../../../resources/hplus/js/plugins/toastr/toastr.min.js"></script>
<script src="../../../resources/ejob.js"></script>
<script src="../../../resources/auth/auth.js"></script>
</body>
</html>
