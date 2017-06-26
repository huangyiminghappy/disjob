<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String bgName=request.getParameter("groupName");
if(bgName == null)
	bgName = "";
String bjName=request.getParameter("jobName");
String blimit=request.getParameter("limit");
if(blimit == null)
	blimit = "13";
String boffset=request.getParameter("offset");
if(boffset == null)
	boffset = "0";
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
	
	<title>job信息管理</title>
	<style type="text/css">
		.button_right{ float:right;}
		.results tr[visible='false'],
		.no-result{
		  display:none;
		}
		
		.results tr[visible='true']{
		  display:table-row;
		}
		
		.counter{
		  padding:8px; 
		  color:#ccc;
		}
	</style>
</head>
<body>
<div class="modal inmodal fade" id="modalStatistics" tabindex="-1" role="dialog"  aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
        	<div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                <h6 class="modal-title"><font id="sta_id">*</font></h6>
            </div>
	        <div class="sta-modal-body">
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
      		<div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h2 class="modal-title" id="myModalLabel"><font color='#FF0000'>任务信息</font></h2>
            </div>
            <div class="modal-body">
            <form class="form-horizontal" id="job_info_form" method="post" title="任务信息">
               <div class="form-group">
                   <label class="col-sm-3 control-label">任务名称：<font color='#FF0000'>*</font></label>
                   <div class="col-sm-8">
                       <input readonly="readonly" name="jobName" minlength="1" type="text" class="form-control" required="" aria-required="true">
                   </div>
               </div>
               <div class="form-group">
                   <label class="col-sm-3 control-label">任务组：<font color='#FF0000'>*</font></label>
                   <div class="col-sm-8">
                   		<input readonly="readonly" name="groupName" minlength="1" type="text" class="form-control" required="" aria-required="true">
                   </div>
               </div>
               <div class="form-group">
                   <label class="col-sm-3 control-label">任务分类：<font color='#FF0000'>*</font></label>
                   <div class="col-sm-8">
                   		<input name="category" minlength="1" type="text" class="form-control">
                   </div>
               </div>
               <div class="form-group">
                   <label class="col-sm-3 control-label">cron时间表达式：<font color='#FF0000'>*</font></label>
                   <div class="col-sm-8">
                       <input id="f_cronExp" placeholder="支持直接输入crontab表达式" name="cronExpression" minlength="6" type="text" class="form-control" required="" aria-required="true">
                   </div>
               </div>
               <div class="form-group">
                   <label class="col-sm-3 control-label">任务调用路径：</label>
                   <div class="col-sm-8">
                       <input type="text" class="form-control" name="jobPath">
                   </div>
               </div>
               <div class="form-group">
                   <label class="col-sm-3 control-label">任务参数：</label>
                   <div class="col-sm-8">
                       <input type="text" class="form-control" name="parameters">
                   </div>
               </div>
               <div class="form-group">
                   <label class="col-sm-3 control-label">超时时间：<font color='#FF0000'>(秒)</font></label></label>
                   <div class="col-sm-8">
                  		 <input type="text" class="form-control" name="timeOut" onkeyup='this.value=this.value.replace(/\D/gi,"")'>
                   </div>
               </div>
               <div class="form-group">
                   <label class="col-sm-3 control-label">上次执行时间：</label>
                   <div class="col-sm-8">
                   		<input placeholder="请选择" class="form-control layer-date" id="query_start" name="lastFireTime">
                   </div>
               </div>
               <div class="form-group">
                   <label class="col-sm-3 control-label">任务结束时间：</label>
                   <div class="col-sm-8">
                       <input placeholder="请选择" class="form-control layer-date" id="query_end" name="endTime">
                   </div>
               </div>
			   <input type="hidden" class="form-control" name="jobStatus">
               <input type="hidden" class="form-control" name="slaveIp">
               <input type="hidden" class="form-control" name="filePath">
               <input type="hidden" class="form-control" name="className">
               <input type="hidden" class="form-control" name="methodName">
               <!-- <div class="form-group">
                   <label class="col-sm-3 control-label">开启故障转移：</label>
                   <div class="col-sm-8">
                       <label>
                                   	 <input type="checkbox" class="checkbox" checked="" value="true" id="e_failover" name="failover">
                                </label>
                   </div>
               </div> -->
               <div class="form-group">
                   <label class="col-sm-3 control-label">错过马上执行：</label>
                   <div class="col-sm-8">
                       <label>
                                   	 <input type="checkbox" class="checkbox" checked="" value="true"  id="e_misfire" name="misfire">
                                </label>
                   </div>
               </div>
               <div class="form-group">
                   <label class="col-sm-3 control-label">马上调度：</label>
                   <div class="col-sm-8">
                       <label>
                         	 <input type="checkbox" class="checkbox" value="true"  id="e_fireNow" name="fireNow">
                      </label>
                   </div>
               </div>
               <!-- <div class="form-group">
                   <label class="col-sm-3 control-label">并行执行：</label>
                   <div class="col-sm-8">
                       <label>
                         	 <input type="checkbox" class="checkbox" value="true"  id="e_ifParallel" name="ifParallel">
                      </label>
                   </div>
               </div>	 -->
               <div class="form-group">
                   <label class="col-sm-3 control-label">广播模式：</label>
                   <div class="col-sm-8">
                       <label>
                         	 <input type="checkbox" class="checkbox" value="true"  id="e_ifBroadcast" name="ifBroadcast">
                      </label>
                   </div>
               </div>    
               <div class="form-group">
                   <label class="col-sm-3 control-label">任务描述：</label>
                   <div class="col-sm-8">
                       <input type="text" class="form-control" name="desc">
                   </div>
               </div>
               <div class="form-group">
               		<div class="col-sm-4 col-sm-offset-3">
               			<button class="btn btn-primary" type="button" onclick="saveJob()">保存</button>
                       <button class="btn btn-primary" type="button" onclick="javascript:$('#job_info_form').form('clear')">重置</button>
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
                    	<select id="s_groupName"  class="form-control m-b" name="groupName" onChange="searchReload()"><!-- style="width:20%;" -->
			            	<option value="" selected="selected">请选择组</option>
			            </select>
                        <!-- <input id="s_jobName" type="text" placeholder="输入任务名" class="input form-control" style="width:80%;"> -->
                        <span class="input-group-btn">
                             <button type="button" class="btn btn btn-primary" onclick="searchReload()"> <i class="fa fa-search"></i> 搜索</button>
                        </span>
                    </div>
                    <br>
                    <div id="toolbar" class="input-group">
                    	<span class="input-group-btn">
                    		<button id="batch_resume" class="btn btn-danger" disabled>批量恢复</button>
				        	<button id="batch_pause" class="btn btn-danger" disabled>批量暂停</button>
				        </span>
				        <input id="search_define" type="text" placeholder="--------自定义检索--------" class="search form-control" style="width:200px;"/>
				        <select id="s_jobcategory"  class="form-control m-b" name="jobcategory" onChange="searchReload()" style="width:200px;"><!-- style="width:20%;" -->
			            	<option value="all" selected="selected">全部</option>
			            </select>
			            <button id="job_addnew" class="btn btn-primary">新增job</button>
                    	<h4 style="float:right;">任务总数：<font color='#FF0000' id="job_total_num">0</font></h4>
                    </div>
                    <table id="jobInfo_table_list"  class="table table-hover table-bordered results"></table>
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

<script type="text/javascript" src="<%=path %>/resources/hplus/js/plugins/layer/laydate/laydate.js"></script>

<script src="<%=path %>/resources/search.js"></script>
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
	JobList: '<%=path %>/app/service/job/info/list',
	JobExecution: '<%=path %>/app/service/job/info/jobExecution',
	JobSearch: '<%=path %>/app/service/job/info/search',
	JobUpdate: '<%=path %>/app/service/job/info/update',
	JobPause: '<%=path %>/app/service/job/info/pause',
	JobResume: '<%=path %>/app/service/job/info/resume',
	JobGroupList: '<%=path %>/app/service/job/group/list',
	JobDetail: '<%=path %>/app/page/monitor/jobExeDetail',
	JobBatchHandle: '<%=path %>/app/service/job/info/batchHandle',
	JobStatistics: '<%=path %>/app/page/monitor/jobExeStatistic',
	JobFireNow : 'service/job/info/fireNow',
	JobDetailInfo : '<%=path %>/app/service/job/info/jobDetailInfo',//新的job编辑界面
	JobRestart: 'service/monitor/restartJob'
};
var gName = "<%=bgName %>";
var plimit = Number("<%=blimit %>");
var poffset = Number("<%=boffset %>");
var isReload = false;

var $table = $('#jobInfo_table_list'),$batch_resume = $('#batch_resume'),$batch_pause = $('#batch_pause');

$(document).ready(function(){
	var start={elem:"#query_start",format:"YYYY-MM-DD hh:mm:ss",min:"1990-07-01 00:00:00",max:"2099-07-01 00:00:00",istime:true,istoday:false,choose:function(datas){end.min=datas;end.start=datas}};
	var end={elem:"#query_end",format:"YYYY-MM-DD hh:mm:ss",min:"1990-07-01 00:00:00",max:"2099-07-01 00:00:00",istime:true,istoday:false,choose:function(datas){start.max=datas}};laydate(start);laydate(end);
	selectJobGroup(gName);
	/* if(jName != null)
		$("#s_jobName").val(jName); */
	initTable(poffset/plimit+1);//记住第几页，为了从执行明细返回时落在原先的选择行中
});

function initTable(num){
	$table.bootstrapTable({
		method: 'post',
		url: UrlConfig.JobSearch,
		height: 700,
		dataType: "json",
		/* pagination: true, */
		"queryParamsType": "limit",
		contentType: "application/x-www-form-urlencoded",
		singleSelect: false,
		/* pageSize: plimit, */
		/* pageNumber:num, */
		search: false, //不显示 搜索框
		uniqueId: "jobName",//以任务名为唯一索引
		columns: [
		          {field: 'state',checkbox: true/* ,formatter:stateFormatter */},
		          {title: '任务名称',field: 'jobName'},
		          {title: '任务组',field: 'groupName',visible: false},
		          {title: '任务分类',field: 'category',visible: false},
		          {title: '文件路径',field: 'filePath',visible: false},
		          {title: 'cron时间表达式',field: 'cronExpression'},
		          {title: '任务调用路径',field: 'jobPath'},
		          {title: '任务参数',field: 'parameters'},
		          {title: '超时时间(秒)',field: 'timeOut'},
		          {title: '上次执行时间',field: 'lastFireTime'},
		          {title: '任务结束时间',field: 'endTime'},
		          /* {title: '任务分片数',field: 'shardingCount'},
		          {title: '提取数据量',field: 'fetchDataCount'},
		          {title: '分片参数',field: 'shardingItemParameters'}, */
		          {
		        	  title: '故障转移',
		        	  field: 'failover',
		        	  formatter:function(value,row,index){  
                 		if(value) 
                      		return '是';
                 		return '否';
                      } 
		          },
		          {
		        	  title: '错过马上执行',
		        	  field: 'misfire',
		        	  formatter:function(value,row,index){  
                 		if(value) 
                      		return '是';
                 		return '否';
                      } 
		          },
		          {
		        	  title: '马上调度',
		        	  field: 'fireNow',
		        	  formatter:function(value,row,index){  
                 		if(value) 
                      		return '是';
                 		return '否';
                      } 
		          },
		          {
		        	  title: '并行执行',
		        	  field: 'ifParallel',
		        	  formatter:function(value,row,index){  
		        		  if(value) 
	                      		return '是';
	                 	  return '否';
                      } 
		          },
		          {
		        	  title: '任务状态',
		        	  field: 'jobStatus',
		        	  formatter:function(value,row,index){  
                 		if(value == "1")
                      		return "正在运行";
                 		else if(value == "2")
                 			return "正在运行";
                 		else if(value == "3")
                 			return "暂停";
                 		else
                 			return "未激活";
                      } 
		          },
		          {title: '任务描述',field: 'desc'},
		          {title: '子节点Ip',field: 'slaveIp'},
		          {
                      title: '操作',
                      field: 'act',
                      align: 'center',
                      formatter:function(value,row,index){  
                   			var pdisable = "";
                            if(row.jobStatus==1 || row.jobStatus==2)
                            	pdisable = "<li><a href='#' onclick='pauseJob(\""+ row.jobName + "\")'>调度暂停</a></li>";
                            var rdisable = row.jobStatus==3?"<li><a href='#' onclick='resumeJob(\""+ row.jobName + "\")'>调度恢复</a></li>":"";/* javascript:0; */
                            
                         op = "<div class='btn-group'>"+
	                            "<button data-toggle='dropdown' class='btn btn-primary dropdown-toggle'>操作 <span class='caret'></span>"+
	                            "</button>"+
	                            "<ul class='dropdown-menu'>"+
	                                "<li><a href='#' data-toggle='modal' data-target='#modalProgress' onclick='statistics(\""+ row.jobName + "\")'>统计</a></li>"+
	                                "<li><a href='#' data-toggle='modal' data-target='#modalProgress' onclick='scanProgressDetail(\""+ row.jobName + "\")'>执行明细</a></li>"+
	                                "<li><a href='#' data-toggle='modal' data-target='#myModal' onclick='editJob(\""+ row.jobName + "\")'>编辑</a></li>"+
	                                "<li><a href='#' data-toggle='modal' onclick='firenow(\""+ row.jobName + "\")'>立即执行</a></li>"+
	                                "<li><a href='#' data-toggle='modal' onclick='restartJob(\""+ row.groupName + "\",\""+ row.jobName + "\")'>重新加载Job</a></li>"+
	                                pdisable+rdisable+
	                            "</ul>"+
	                          "</div>";
                            
                           /*  stic = "<button class='btn btn-outline btn-default' data-toggle='modal' data-target='#modalProgress' type='button' onclick='statistics(\"" + row.jobName + "\")'>统计</button>";
                            pr = "<button class='btn btn-outline btn-default' data-toggle='modal' data-target='#modalProgress' type='button' onclick='scanProgressDetail(\"" + row.jobName + "\")'>执行明细</button>";
                            ed = "<button class='btn btn-outline btn-default' data-toggle='modal' data-target='#myModal' type='button' onclick='editJob(\"" + row.jobName + "\")'>编辑</button>";
                            pa = "<button class='btn btn-outline btn-default' "+pdisable+" type='button' onclick='pauseJob(\"" + row.jobName + "\")'>调度暂停</button>";
                            re = "<button class='btn btn-outline btn-default' "+rdisable+" type='button' onclick='resumeJob(\"" + row.jobName + "\")'>调度恢复</button>"; */
                   			
                   			return op;  
                    }
                  }
		],
		showColumns: false, //不显示下拉框（选择显示的列）
		sidePagination: "server", //服务端请求
		queryParams: queryParams,
		responseHandler: responseHandler,
		rowStyle: function (row, index) {
            //这里有5个取值代表5中颜色['active', 'success', 'info', 'warning', 'danger'];
            var name = "<%=bjName %>";
            var strclass = '';
            if(row.jobName == name)
            	strclass = 'active';
            return { classes: strclass }
        },
		onClickRow: gotoJobDetailPage
	 });
	
	$table.on('check.bs.table uncheck.bs.table ' +
	        'check-all.bs.table uncheck-all.bs.table', function () {
	    $batch_resume.prop('disabled', !$table.bootstrapTable('getSelections').length);
	    $batch_pause.prop('disabled', !$table.bootstrapTable('getSelections').length);
	}); 
	$table.on('post-body.bs.table', function(){
		var $active = $(".active");
		if($active.length != 0){
			$(".fixed-table-body").scrollTop(($active.index()-5) * $active.height())
		}
	});
	//批量恢复操作
	$batch_resume.click(function () {
		batchHandle(1);
	});
	//批量暂停操作
	$batch_pause.click(function () {
		batchHandle(0);
	});
	//窗口大小改变
	$(window).resize(function () {
	    $table.bootstrapTable('resetView', {
	        height: 600
	    });
	});
}
function gotoJobDetailPage(row, $element,field){
	if($element.context.cellIndex > 0 && $element.context.cellIndex < 15){
		location.href=UrlConfig.JobDetailInfo+"?groupName="+row.groupName+"&jobName="+row.jobName+"&method=edit";
	}
}
//ajax返回的数据处理
function responseHandler(res) {
	if(res.successful){
		$("#job_total_num").text(res.rows.length);
		fillCategorySelector(res.categories);
		return {
			"rows": res.rows,
			"total": res.total
		};
	}else{
		toastr["error"](res.msg,"操作结果：");
		return {"rows":[],"total":0};
	}
}
//传递的参数
function queryParams(params) {
	plimit = params.limit;
	if(isReload){
		isReload = false;
		poffset = 0;
	}else{
		poffset = params.offset;
	}
	return {
		groupName: gName,//组名
		category : category
	};
	/* return {
		groupName: gName,//组名
		limit: params.limit,//页面大小
		offset: poffset//偏移量
	}; */
}
//搜索
function searchReload(){
	isReload = true;//重新加载
	tableReload();
	$table.bootstrapTable('selectPage', 1);//恢复到首页
}
//刷新
var category;
function tableReload(){/* 刷新表格数据 */
	gName = $("#s_groupName").val();//读取任务组信息
	category = $("#s_jobcategory").val();//分组信息
	$batch_resume.prop('disabled', 1);//使能批量恢复按钮不可用
    $batch_pause.prop('disabled', 1);//使能批量暂停按钮不可用
	$table.bootstrapTable('refresh'); //刷新
}
function categoryInit(){
	$s_jobcategory = $('#s_jobcategory');
	$('#s_jobcategory').empty();
	var all = '<option value="all">全部</option>';
	$s_jobcategory.append(all);
}
//在查询数据的时候,同步查询jobGroup下的分组信息
function fillCategorySelector(categories){
	$s_jobcategory = $('#s_jobcategory');
	var currentValue = $s_jobcategory.val();
	$('#s_jobcategory').empty();
	var all = '<option value="all">全部</option>';
	if(categories){
		if(currentValue == "all"){
			all = '<option value="all" selected="selected" >全部</option>'
		}
		var s_jobcategory = all;
		for (var i = 0, size = categories.length; i < size; i++) {
			var category = categories[i];
			var select = "";
			if(currentValue == category){
				select = 'selected="selected"';
			}
			//$s_jobcategory.append('<option value="' + category + '"'+select+'>' + category + '</option>');/* 添加属性*/
			s_jobcategory += '<option value="' + category + '"'+select+'>' + category + '</option>';
		}
		$s_jobcategory.append(s_jobcategory);
	}else{
		$s_jobcategory.append(all);
	}
}
//获取任务组信息列表
function selectJobGroup(gName) {
	var $s_groupName = $('#s_groupName');/* 下拉框属性更新*/
	$.post(UrlConfig.JobGroupList, null, function(result) {/* 查询所有的组信息*/
		for (var i = 0; i < result.length; i++) {
			var group = result[i];
			var select = "";
			if(gName == group.groupName){
				select = 'selected="selected"';
			}
			$s_groupName.append('<option value="' + group.groupName + '"'+select+'>' + group.groupName + '</option>');/* 添加属性*/
		}
	}, 'json');
}
function saveJob(){
	var cron = $("#f_cronExp").val();
	if(cron == ""){
		toastr["warning"]("请输入cron表达式","警告：");/* 显示结果 */
		return;
	}
	$.post(UrlConfig.JobUpdate, $('#job_info_form').serialize(), function(result){/* 序列化表单数据，定义操作完成函数的处理 */
    	if (result.successful) {/* 是否成功 */
    		$('#myModal').modal('hide');
        	toastr["info"]("操作成功","操作结果：");/* 显示结果 */
        	categoryInit(); /* 在编辑job信息时,有可能会编辑掉分类信息,这时候需要判断当前查询的分类是否还存在数据,否则按照全部来查询,这里仍然要访问两次zk节点,所以这里简单处理为初始化为all的话就不需要校验当前所选的分类是否还存在, 主要矛盾点在,分类的信息是从zk节点来的,并不是额外配置生成*/
        	tableReload();
        	//$("#jobInfo_table_list").trigger("reloadGrid");/*重新加载table数据表*/
        	$('#job_info_form').form('clear'); /* 清空form表单数据 */
        } else if(!result.successful){
        	toastr["error"](result.msg,"操作结果：");/* 显示结果 */
        }else{
        	toastr["info"](result.msg,"操作结果：");/* 显示结果 */
        }
    },'json');
}
function editJob(jobName){
	$('#job_info_form').form('load',$table.bootstrapTable("getRowByUniqueId",jobName,category));
}
function firenow(jobName){
	ejob.post(UrlConfig.JobFireNow,{
		jobName : jobName,
		jobGroup : $("#s_groupName").val()
	},function(data){
		if(data.successful){
			toastr["info"]("立即执行请求已提交","操作结果：");
		}else{
			toastr["error"]("立即执行请求提交失败 :" + data.msg ,"操作结果：");
		}
	});
}
function restartJob(groupName,jobName){
 	ejob.post(UrlConfig.JobRestart,{
		groupName : groupName,
		jobName : jobName,
		jobGroup : $("#s_groupName").val()
	},function(data){
		if(data.successful){
			toastr["info"]("重新加载成功","操作结果：");
		}else{
			toastr["error"]("重新加载失败 :" + data.msg ,"操作结果：");
		}
	});
}
 function pauseJob(jobName) {
	/*确认提示匡 */
	swal({title:"暂停？",text:"请谨慎操作！",
		type:"warning",
		showCancelButton:true,
		confirmButtonColor:"#DD6B55",
		confirmButtonText:"暂停任务",
		closeOnConfirm:true},
		function(){
			$('#job_info_form').form('load',$table.bootstrapTable("getRowByUniqueId",jobName));
            $.post(UrlConfig.JobPause, $('#job_info_form').serialize(), function(result){/* 序列化表单数据，定义操作完成函数的处理 */
            	if (result.successful) {/* 是否成功 */
                	toastr["info"]("操作成功","操作结果：");/* 显示结果 */
                	tableReload();
                } else {
                	toastr["error"](result.msg,"操作结果：");/* 显示结果 */
                }
            },'json');
	})
}
function resumeJob(jobName) {
	/*确认提示匡 */
	swal({title:"恢复？",text:"请谨慎操作！",
		type:"warning",
		showCancelButton:true,
		confirmButtonColor:"#DD6B55",
		confirmButtonText:"恢复任务",
		closeOnConfirm:true},
		function(){
			$('#job_info_form').form('load',$table.bootstrapTable("getRowByUniqueId",jobName));
            $.post(UrlConfig.JobResume, $('#job_info_form').serialize(), function(result){/* 序列化表单数据，定义操作完成函数的处理 */
            	if (result.successful) {/* 是否成功 */
                	toastr["info"]("操作成功","操作结果：");/* 显示结果 */
                	tableReload();
                } else {
                	toastr["info"](result.msg,"操作结果：");/* 显示结果 */
                }
            },'json');
	})
}
function statistics(jobName){
	var cgName = $("#s_groupName").val()||"";
	$("#sta_id").text("组:"+cgName+"（任务:"+jobName+"）");
	$(".sta-modal-body").load(UrlConfig.JobStatistics+"?groupName=" + cgName +"&jobName="+jobName);
	$('#modalStatistics').modal('show');
}
function scanProgressDetail(jobName){
	var cgName = $("#s_groupName").val()||"";
	location.href=UrlConfig.JobDetail+"?groupName="+cgName+"&jobName="+jobName+"&limit="+plimit+"&offset="+poffset;
}
function batchHandle(ptype){
	var gName = $("#s_groupName").val()||"";
	
	var arr = $table.bootstrapTable('getSelections');
    if(arr.length == 0){
   		toastr["warning"]("未选择任何任务！","警告：");
   		return;
   	}
   	var jNames = "";
   	var isReture = false;
   	var retureJobs = null;
   	for(var index=0;index<arr.length;index++){
   		/* 恢复是1，暂停是0 */
   		if(arr[index].jobStatus == 0 || (arr[index].jobStatus == 3 && ptype == 0) || (arr[index].jobStatus == 1 && ptype == 1) || (arr[index].jobStatus == 2 && ptype == 1)){
   			isReture = true;
   			if(retureJobs==null)
   				retureJobs = arr[index].jobName;
   			else
   				retureJobs += ("," + arr[index].jobName);
   		}
   		if(index == 0)
   			jNames += arr[index].jobName;
   		else
   			jNames += ("," + arr[index].jobName);
   	}
   	if(isReture){
   		toastr["warning"]("任务："+retureJobs+" 不能执行此操作！","操作结果：");/* 显示结果 */
   		return;
   	}
   	$.post(UrlConfig.JobBatchHandle, {type:ptype,groupName:gName,jobNames:jNames}, function(result){/* 序列化表单数据，定义操作完成函数的处理 */
    	if (result.successful) {/* 是否成功 */
        	toastr["info"]("操作成功","操作结果：");/* 显示结果 */
        	tableReload();
        } else {
        	toastr["info"](result.msg,"操作结果：");/* 显示结果 */
        }
    },'json');
}
</script>
<script src="../../../resources/ejob.js"></script>
<script src="../../../resources/job/jobInfo.js"></script>
</body>
</html>