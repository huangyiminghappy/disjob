<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <title>权限列表</title>
    <link href="<%=path%>/resources/cron/themes/bootstrap/easyui.min.css" rel="stylesheet" type="text/css" />
    <link href="<%=path%>/resources/cron/themes/icon.css" rel="stylesheet" type="text/css" />
    <link href="<%=path%>/resources/cron/icon.css" rel="stylesheet" type="text/css" />
</head>
<body>
	<table data-toggle="table"
	       data-url="http://mikepenz.com/jsfiddle/"
	       data-pagination="true"
	       data-side-pagination="server"
	       data-page-list="[5, 10, 20, 50, 100, 200]"
	       data-search="true"
	       data-height="300">
	    <thead>
	    <tr>
	        <th data-field="state" data-checkbox="true"></th>
	        <th data-field="id" data-align="right" data-sortable="true">Item ID</th>
	        <th data-field="name" data-align="center" data-sortable="true">Item Name</th>
	        <th data-field="price" data-sortable="true">Item Price</th>
	    </tr>
	    </thead>
	</table>
    <script type="text/javascript" src="<%=path %>/resources/cron/jquery-1.6.2.min.js"></script>
    <script type="text/javascript" src="<%=path %>/resources/cron/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="https://rawgit.com/wenzhixin/bootstrap-table/master/src/bootstrap-table.js"></script>
</body>
</html>
