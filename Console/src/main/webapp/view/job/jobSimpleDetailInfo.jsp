<%@page import="com.huangyiming.disjob.register.domain.Job"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>job编辑详细</title>
    <link href="<%=path %>/resources/hplus/css/plugins/iCheck/custom.css" rel="stylesheet">
    
    <link href="<%=path %>/resources/hplus/css/bootstrap.min14ed.css" rel="stylesheet" type="text/css">
	<link href="<%=path %>/resources/hplus/css/font-awesome.min93e3.css" rel="stylesheet" type="text/css">
	<link href="<%=path %>/resources/hplus/css/animate.min.css" rel="stylesheet" type="text/css">
	<link href="<%=path %>/resources/hplus/css/style.min862f.css" rel="stylesheet">
	<link href="<%=path %>/resources/hplus/css/plugins/bootstrap-table/bootstrap-table.min.css" rel="stylesheet">
	<link href="<%=path %>/resources/hplus/css/plugins/toastr/toastr.min.css" rel="stylesheet">
	<link href="<%=path %>/resources/hplus/css/plugins/sweetalert/sweetalert.css" rel="stylesheet">
    
    <style>
        .pt5{
            padding-top:5px;
        }
        .pt10{
            padding-top:10px;
        }
    </style>
</head>

<body class="gray-bg">
    <div class="wrapper wrapper-content animated fadeInRight">
        <!-- <form method="get" class="form-horizontal" id="job-detail-form"> -->
        <form class="form-horizontal" id="job-form">
            <div class="row">
                <div class="col-sm-12">
                    <div class="ibox float-e-margins">
                        <div class="ibox-title">
                            <h5>作业详细信息</h5>
                        </div>
                        <div class="ibox-content">
                            <!-- 添加的job属性 -->
                            <!-- <div class="hr-line-dashed"></div> -->
                            <div class="form-group">
                                <label class="col-sm-2 control-label">任务名称<font color='#FF0000'>*</font></label>
                                <div class="col-sm-4">
                                    <input type="text" 
                                    <% if(((Job)request.getAttribute("job")).getJobName() != null){%>
                                    	readonly="readonly"
                                    <%} %>
                                     class="form-control" name="jobName" id="jobName" value="${job.jobName}" >
                                </div>
                                <label class="col-sm-1 control-label">任务组<font color='#FF0000'>*</font></label>
                                <div class="col-sm-4">
                                	<select class="form-control" name="groupName" id="groupName" ><!-- style="width:20%;" -->
						            </select>
                                </div>
                                <div class="col-sm-1"></div>
                            </div>
                            <div class="form-group">
                            </div>
                            <!-- <div class="hr-line-dashed"></div> -->
                            <div class="form-group">
                                <label class="col-sm-2 control-label">任务分类</label>
                                <div class="col-sm-4">
                                    <input type="text" class="form-control" name="category" id="category" value="${job.category}">
                                    <span class="help-block m-b-none"><i class="fa fa-info-circle"></i>分类便于您管理任务</span>
                                </div>
                                <label class="col-sm-1 control-label">文件路径</label>
                                <div class="col-sm-4">
                                    <input type="text" class="form-control" name="filePath" id="filePath" value="${job.filePath}">
                                    <span class="help-block m-b-none"><i class="fa fa-info-circle"></i><font color='#FF0000'>注意:php任务此项为必填</font></span>
                                </div>
                                <div class="col-sm-1"></div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">类名<font color='#FF0000'>*</font></label>
                                <div class="col-sm-4">
                                    <input type="text" class="form-control" name="className" id="className" value="${job.className}">
                                </div>
                                <label class="col-sm-1 control-label">方法名<font color='#FF0000'>*</font></label>
                                <div class="col-sm-4">
                                    <input type="text" class="form-control" name="methodName" id="methodName" value="${job.methodName}">
                                </div>
                                <div class="col-sm-1"></div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">cron表达式<font color='#FF0000'>*</font></label>
                                <div class="col-sm-4">
                                    <input type="text" class="form-control" name="cronExpression" id="cronExpression" required="" value="${job.cronExpression}">
                                </div>
                                <label class="col-sm-1 control-label">任务调用路径</label>
                                <div class="col-sm-4">
                                    <input type="text" class="form-control" name="jobPath" id="jobPath" value="${job.jobPath}">
                                </div>
                                <div class="col-sm-1"></div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">任务参数</label>
                                <div class="col-sm-4">
                                    <input type="text" class="form-control" name="parameters" id="parameters" value="${job.parameters}">
                                </div>
                                <label class="col-sm-1 control-label">超时时间<font color='#FF0000'>(秒)</font></label>
                                <div class="col-sm-4">
                                    <input type="text" class="form-control" name="timeOut" id="timeOut" value="${job.timeOut}">
                                </div>
                                <div class="col-sm-1"></div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">上次执行时间</label>
                                <div class="col-sm-4">
                                    <input readonly="readonly" class="form-control layer-date" name="lastFireTime" id="lastFireTime" value="${job.lastFireTime}">
                                </div>
                                <label class="col-sm-1 control-label">任务结束时间</label>
                                <div class="col-sm-4">
                                    <input placeholder="请选择" class="form-control layer-date" name="endTime" id="endTime" value="${job.endTime}">
                                </div>
                                <div class="col-sm-1"></div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">错过马上执行</label>
                                <div class="col-sm-4">
                                    <input type="checkbox" class="checkbox" value="true" id="misfire" name="misfire" 
                                    <% if(((Job)request.getAttribute("job")).isMisfire()){%>
                                    	checked
                                    <%} %>
                                    >
                                </div>
                                <label class="col-sm-1 control-label">广播模式：</label>
			                   <div class="col-sm-4">
			                       <label>
			                         	 <!-- <input type="checkbox" class="checkbox" value="true"  id="ifBroadcast" name="ifBroadcast"> -->
		                         	 <input type="checkbox" class="checkbox" value="true" id="ifBroadcast" name="ifBroadcast" 
                                    <% if(((Job)request.getAttribute("job")).isIfBroadcast()){%>
                                    	checked
                                    <%} %>
                                    >
			                      </label>
			                    </div>
                                <div class="col-sm-1"></div>
                            </div>
                            <%-- <div class="form-group">
			                   <label class="col-sm-2 control-label">广播模式：</label>
			                   <div class="col-sm-4">
			                       <label>
			                         	 <!-- <input type="checkbox" class="checkbox" value="true"  id="ifBroadcast" name="ifBroadcast"> -->
		                         	 <input type="checkbox" class="checkbox" value="true" id="ifBroadcast" name="ifBroadcast" 
                                    <% if(((Job)request.getAttribute("job")).isIfBroadcast()){%>
                                    	checked
                                    <%} %>
                                    >
			                      </label>
			                   </div>
			               </div> --%>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">任务描述</label>
                                <div class="col-sm-10">
                                    <input type="text" class="form-control" name="desc" id="desc" value="${job.desc}">
                                </div>
                            </div>
                            <!-- 添加的job属性 end -->
                        </div>
                    </div>
                </div>
            </div>
		</form>
            <div class="form-group">
                <div class="col-sm-4 col-sm-offset-2">
                    <button class="btn btn-primary" type="submit" id="saveJobbtn">保存</button>
                    <button class="btn btn-white" type="submit" id="backbtn">返回</button>
                </div>
            </div>
    </div>
    <script src="<%=path %>/resources/hplus/js/jquery.min.js"></script>
    <script src="<%=path %>/resources/hplus/js/bootstrap.min.js"></script>
    <script src="<%=path %>/resources/hplus/js/content.min.js"></script>
    <script src="<%=path %>/resources/hplus/js/plugins/iCheck/icheck.min.js"></script>
    
<script src="<%=path %>/resources/hplus/js/plugins/bootstrap-table/bootstrap-table.min.js"></script>
<script src="<%=path %>/resources/hplus/js/plugins/bootstrap-table/bootstrap-table-mobile.min.js"></script>
<script src="<%=path %>/resources/hplus/js/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
<script src="<%=path %>/resources/hplus/js/plugins/toastr/toastr.min.js"></script>
<script src="<%=path %>/resources/hplus/js/plugins/sweetalert/sweetalert.min.js"></script>
<script src="<%=path %>/resources/hplus/js/plugins/layer/laydate/laydate.js"></script>
<script src="<%=path %>/resources/hplus/js/plugins/validate/jquery.validate.min.js"></script>
<script src="<%=path %>/resources/hplus/js/plugins/validate/messages_zh.min.js"></script>
<script type="text/javascript" src="<%=path %>/resources/ejob.js"></script>
<script type="text/javascript" src="<%=path %>/resources/job/jobSimpleDetailInfo.js"></script>
</body>
</html>
