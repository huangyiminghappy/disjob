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
	<link href="<%=path %>/resources/hplus/css/plugins/toastr/toastr.min.css" rel="stylesheet">
	<script type="text/javascript" src="<%=path %>/resources/hplus/js/jquery.min.js"></script>
	<script type="text/javascript" src="<%=path %>/resources/hplus/js/bootstrap.min.js"></script>
	
    <script>if(window.top !== window.self){ window.top.location = window.location;}</script>
    <style type="text/css">
     ul{list-style-type: decimal; }
  	ul li{ display: list-item;}
    </style>
</head>
<body>
<div class="middle-box text-center loginscreen  animated fadeInDown">
	<div>
        <div>
              <h1 class="logo-name">Ejob</h1>
        </div>
        <h3>welcome</h3>
		<form id="loginform" method="post" action="<%=request.getContextPath() %>/login">
              <div class="form-group">
                  <input type="text"  name="username" class="form-control" placeholder="用户名" required="true">
              </div>
              <div class="form-group">
                  <input type="password"  name="password" class="form-control" placeholder="密码" required="true">
              </div>
              <button type="submit" class="btn btn-primary block full-width m-b">登 录</button>
		</form>
    </div>
</div>
<script src="<%=path %>/resources/hplus/js/plugins/toastr/toastr.min.js"></script>
<script type="text/javascript">
$(document).ready(function(){
    $('#loginform').bind('submit', function(){
    	var dataPara = getFormJson(this);
        $.ajax({
            url: '<%=request.getContextPath() %>/app/login',
            type: this.method,
            data: dataPara,
            success: function(result){
    			result = eval('(' + result + ')');
    			
    			if (result.successful) {
    				window.location.href="<%=request.getContextPath() %>";	
    			} else {
    				toastr.options = {
      					  "closeButton": true,
      					  "debug": true,
      					  "progressBar": false,
      					  "positionClass": "toast-bottom-full-width",
      					  "showDuration": "400",
      					  "hideDuration": "1000",
      					  "timeOut": "5000",
      					  "extendedTimeOut": "1000",
      					  "showEasing": "swing",
      					  "hideEasing": "linear",
      					  "showMethod": "fadeIn",
      					  "hideMethod": "fadeOut"
      				};
      				toastr["warning"](result.msg,"操作结果：");
    			}
    		}
        });
        return false;
    });
});

//将form中的值转换为键值对。
function getFormJson(frm) {
    var o = {};
    var a = $(frm).serializeArray();
    $.each(a, function () {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });

    return o;
}
</script>

</body>
</html>