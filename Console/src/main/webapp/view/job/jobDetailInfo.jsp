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
                            <span class="help-block m-b-none"><i class="fa fa-info-circle"></i>添加或编辑任务</span>
                        </div>
                        <div class="ibox-content">
                            <div class="form-group">
                                <label class="col-sm-2 control-label">任务名称 <font color='#FF0000'>*</font></label>
                                <div class="col-sm-4">
                                    <input type="text" 
                                    <% if(((Job)request.getAttribute("job")).getJobName() != null){%>
                                    	readonly="readonly"
                                    <%} %>
                                     class="form-control" required aria-required="true" name="jobName" id="jobName" value="${job.jobName}" >
                                </div>
                                <label class="col-sm-1 control-label">任务组<font color='#FF0000'>*</font></label>
                                <div class="col-sm-4">
                                	<select class="form-control" name="groupName" id="groupName" ><!-- style="width:20%;" -->
						            </select>
                                   <%--  <select 
                                    class="form-control" aria-required="true" required="true" name="groupName" id="groupName" value="${job.groupName}"
                                    </select> --%>
                                </div>
                                <div class="col-sm-1"></div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">任务分类</label>
                                <div class="col-sm-4">
                                    <input type="text" class="form-control" name="category" id="category" value="${job.category}">
                                    <span class="help-block m-b-none"><i class="fa fa-info-circle"></i>分类便于您管理任务</span>
                                </div>
                                <label class="col-sm-1 control-label">文件路径</font></label>
                                <div class="col-sm-4">
                                    <input type="text" class="form-control" name="filePath" id="filePath" value="${job.filePath}">
                                    <span class="help-block m-b-none"><i class="fa fa-info-circle"></i><font color='#FF0000'>注意:php任务此项为必填</font></span>
                                </div>
                                <div class="col-sm-1"></div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">类名<font color='#FF0000'>*</font></label>
                                <div class="col-sm-4">
                                    <input type="text" class="form-control" required name="className" id="className" value="${job.className}">
                                    <!-- <span class="help-block m-b-none"><i class="fa fa-info-circle"></i>对应setting.ini的class</span> -->
                                </div>
                                <label class="col-sm-1 control-label">方法名<font color='#FF0000'>*</font></label>
                                <div class="col-sm-4">
                                    <input type="text" class="form-control" required name="methodName" id="methodName" value="${job.methodName}">
                                    <!-- <span class="help-block m-b-none"><i class="fa fa-info-circle"></i>对应setting.ini的method</span> -->
                                </div>
                                <div class="col-sm-1"></div>
                            </div>
                            <div class="form-group">
                            </div>
                            <!-- job新增时添加的属性  end -->
                            <input type="text" hidden="hidden" name = "cronExpression" value = "${job.cronExpression}" id="cronExpression" />
                            <input type="hidden" class="form-control" name="jobStatus" value = "${job.jobStatus}">
               				<input type="hidden" class="form-control" name="slaveIp" value = "${job.slaveIp}">
                            <!-- <div class="hr-line-dashed"></div> -->
                            <div class="form-group">
                                <label class="col-sm-2 control-label">任务调用路径</label>
                                <div class="col-sm-4">
                                    <input type="text" class="form-control" name="jobPath" id="jobPath" value="${job.jobPath}">
                                </div>
                                <label class="col-sm-1 control-label">任务参数</label>
                                <div class="col-sm-4">
                                    <input type="text" class="form-control" name="parameters" id="parameters" value="${job.parameters}">
                                </div>
                                <div class="col-sm-1"></div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">超时时间<font color='#FF0000'>(秒)</font></label>
                                <div class="col-sm-4">
                                    <input type="text" class="form-control" name="timeOut" id="timeOut" value="${job.timeOut}">
                                </div>
                                <label class="col-sm-1 control-label">上次执行时间</label>
                                <div class="col-sm-4">
                                    <input readonly="readonly" class="form-control layer-date" name="lastFireTime" id="lastFireTime" value="${job.lastFireTime}">
                                </div>
                                <div class="col-sm-1"></div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">任务结束时间</label>
                                <div class="col-sm-4">
                                    <input placeholder="请选择" class="form-control layer-date" name="endTime" id="endTime" value="${job.endTime}">
                                </div>
                                <label class="col-sm-1 control-label">错过马上执行</label>
                                <div class="col-sm-4">
                                    <input type="checkbox" class="checkbox" value="true" id="misfire" name="misfire" 
                                    <% if(((Job)request.getAttribute("job")).isMisfire()){%>
                                    	checked
                                    <%} %>
                                    >
                                </div>
                                <div class="col-sm-1"></div>
                            </div>
                            <%-- <div class="form-group">
                                <label class="col-sm-2 control-label">马上调度</label>
                                <div class="col-sm-10">
                                    <input type="checkbox" class="checkbox" value="true" id="fireNow" name="fireNow" 
                                    <% if(((Job)request.getAttribute("job")).isFireNow()){%>
                                    	checked
                                    <%} %>
                                    >
                                </div>
                            </div> --%>
                            <!-- <div class="hr-line-dashed"></div> -->
                            <div class="form-group">
                                <%-- <label class="col-sm-2 control-label">并行执行</label>
                                <div class="col-sm-4">
                                    <input type="checkbox" class="checkbox" value="true" id="ifParallel" name="ifParallel" 
                                    <% if(((Job)request.getAttribute("job")).isIfParallel()){%>
                                    	checked
                                    <%} %>
                                    >
                                </div> --%>
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
                            </div>
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
		</form><form class="form-horizontal" id="cron-form">
            <div class="row">
                <div class="col-sm-12">
                    <div class="ibox float-e-margins">
                        <div class="ibox-title">
                            <h5>执行时间</h5>
                        </div>
                        <div class="ibox-content">
                            <div class="form-group">
                                <div class="col-sm-2">
                                     <div class="radio i-checks">
                                        <label class="pull-right">
                                            <input type="radio" value="true" name="chooseSpecial" checked=""> <i></i> 按选择的时间表运行…
                                        </label>
                                    </div>
                                </div>
                                 <div class="col-sm-2">
                                    <select class="form-control" name="special" id="special" size="false">
                                        <option value="hourly" selected="">每小时</option>
                                        <option value="daily">每天（在午夜）</option>
                                        <option value="weekly">每周（在星期日）</option>
                                        <option value="monthly">每月（在第一天）</option>
                                        <option value="yearly">每年（在1月1日）</option>
                                        <!-- <option value="reboot">当系统启动时</option> -->
                                    </select>
                                </div>
                                <div class="col-sm-3">
                                     <div class="radio i-checks">
                                        <label>
                                            <input type="radio" value="false" name="chooseSpecial"> <i></i> 按下面选择的时间运行…
                                        </label>
                                    </div>
                                </div>
                            </div>
                            <div class="hr-line-dashed"></div>
                            <div class="form-group">
                            <div class="col-sm-2">
                                    <strong class="col-xs-12 text-right">秒</strong>
                                    <div class="form-group">
                                        <div class="col-sm-12">
                                            <div class="radio i-checks pull-right">
                                                <label>
                                                    <input type="radio" value="false" name="allSeconds"> <i></i> 已选择的...
                                                </label>
                                            </div>
                                             <div class="radio i-checks pull-right">
                                                <label>
                                                    <input type="radio" value="true" checked="" name="allSeconds"> <i></i> 全部
                                                </label>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-sm-12 m-l-n pull-right">
                                        <select class="pull-right" name="seconds" size="12" multiple="">
                                            <option value="48">48</option>
                                            <option value="49">49</option>
                                            <option value="50">50</option>
                                            <option value="51">51</option>
                                            <option value="52">52</option>
                                            <option value="53">53</option>
                                            <option value="54">54</option>
                                            <option value="55">55</option>
                                            <option value="56">56</option>
                                            <option value="57">57</option>
                                            <option value="58">58</option>
                                            <option value="59">59</option>
                                        </select>
                                        <select class="pull-right" name="seconds" size="12" multiple="">
                                            <option value="36">36</option>
                                            <option value="37">37</option>
                                            <option value="38">38</option>
                                            <option value="39">39</option>
                                            <option value="40">40</option>
                                            <option value="41">41</option>
                                            <option value="42">42</option>
                                            <option value="43">43</option>
                                            <option value="44">44</option>
                                            <option value="45">45</option>
                                            <option value="46">46</option>
                                            <option value="47">47</option>
                                        </select>
                                        <select class="pull-right" name="seconds" size="12" multiple="">
                                            <option value="24">24</option>
                                            <option value="25">25</option>
                                            <option value="26">26</option>
                                            <option value="27">27</option>
                                            <option value="28">28</option>
                                            <option value="29">29</option>
                                            <option value="30">30</option>
                                            <option value="31">31</option>
                                            <option value="32">32</option>
                                            <option value="33">33</option>
                                            <option value="34">34</option>
                                            <option value="35">35</option>
                                        </select>
                                        <select class="pull-right" name="seconds" size="12" multiple="">
                                            <option value="12">12</option>
                                            <option value="13">13</option>
                                            <option value="14">14</option>
                                            <option value="15">15</option>
                                            <option value="16">16</option>
                                            <option value="17">17</option>
                                            <option value="18">18</option>
                                            <option value="19">19</option>
                                            <option value="20">20</option>
                                            <option value="21">21</option>
                                            <option value="22">22</option>
                                            <option value="23">23</option>
                                        </select>
                                        <select class="pull-right" name="seconds" size="12" multiple="">
                                            <option value="0">0</option>
                                            <option value="1">1</option>
                                            <option value="2">2</option>
                                            <option value="3">3</option>
                                            <option value="4">4</option>
                                            <option value="5">5</option>
                                            <option value="6">6</option>
                                            <option value="7">7</option>
                                            <option value="8">8</option>
                                            <option value="9">9</option>
                                            <option value="10">10</option>
                                            <option value="11">11</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-sm-2">
                                    <strong class="col-xs-12 text-right">分钟</strong>
                                    <div class="form-group">
                                        <div class="col-sm-12">
                                            <div class="radio i-checks pull-right">
                                                <label>
                                                    <input type="radio" value="false" name="allMins"> <i></i> 已选择的...
                                                </label>
                                            </div>
                                             <div class="radio i-checks pull-right">
                                                <label>
                                                    <input type="radio" value="true" checked="" name="allMins"> <i></i> 全部
                                                </label>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-sm-12 m-l-n pull-right">
                                        <select class="pull-right" name="mins" size="12" multiple="">
                                            <option value="48">48</option>
                                            <option value="49">49</option>
                                            <option value="50">50</option>
                                            <option value="51">51</option>
                                            <option value="52">52</option>
                                            <option value="53">53</option>
                                            <option value="54">54</option>
                                            <option value="55">55</option>
                                            <option value="56">56</option>
                                            <option value="57">57</option>
                                            <option value="58">58</option>
                                            <option value="59">59</option>
                                        </select>
                                        <select class="pull-right" name="mins" size="12" multiple="">
                                            <option value="36">36</option>
                                            <option value="37">37</option>
                                            <option value="38">38</option>
                                            <option value="39">39</option>
                                            <option value="40">40</option>
                                            <option value="41">41</option>
                                            <option value="42">42</option>
                                            <option value="43">43</option>
                                            <option value="44">44</option>
                                            <option value="45">45</option>
                                            <option value="46">46</option>
                                            <option value="47">47</option>
                                        </select>
                                        <select class="pull-right" name="mins" size="12" multiple="">
                                            <option value="24">24</option>
                                            <option value="25">25</option>
                                            <option value="26">26</option>
                                            <option value="27">27</option>
                                            <option value="28">28</option>
                                            <option value="29">29</option>
                                            <option value="30">30</option>
                                            <option value="31">31</option>
                                            <option value="32">32</option>
                                            <option value="33">33</option>
                                            <option value="34">34</option>
                                            <option value="35">35</option>
                                        </select>
                                        <select class="pull-right" name="mins" size="12" multiple="">
                                            <option value="12">12</option>
                                            <option value="13">13</option>
                                            <option value="14">14</option>
                                            <option value="15">15</option>
                                            <option value="16">16</option>
                                            <option value="17">17</option>
                                            <option value="18">18</option>
                                            <option value="19">19</option>
                                            <option value="20">20</option>
                                            <option value="21">21</option>
                                            <option value="22">22</option>
                                            <option value="23">23</option>
                                        </select>
                                        <select class="pull-right" name="mins" size="12" multiple="">
                                            <option value="0">0</option>
                                            <option value="1">1</option>
                                            <option value="2">2</option>
                                            <option value="3">3</option>
                                            <option value="4">4</option>
                                            <option value="5">5</option>
                                            <option value="6">6</option>
                                            <option value="7">7</option>
                                            <option value="8">8</option>
                                            <option value="9">9</option>
                                            <option value="10">10</option>
                                            <option value="11">11</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-sm-2">
                                    <strong class="col-xs-12 text-right">小时</strong>
                                    <div class="form-group">
                                        <div class="col-sm-12">
                                            <div class="radio i-checks pull-right">
                                                <label>
                                                    <input type="radio" value="false" name="allHours"> <i></i> 已选择的...
                                                </label>
                                            </div>
                                             <div class="radio i-checks pull-right">
                                                <label>
                                                    <input type="radio" value="true" checked="" name="allHours"> <i></i> 全部
                                                </label>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-sm-12 m-l-n pull-right">
                                        <select class="pull-right" name="hours" size="12" multiple="">
                                            <option value="12">12</option>
                                            <option value="13">13</option>
                                            <option value="14">14</option>
                                            <option value="15">15</option>
                                            <option value="16">16</option>
                                            <option value="17">17</option>
                                            <option value="18">18</option>
                                            <option value="19">19</option>
                                            <option value="20">20</option>
                                            <option value="21">21</option>
                                            <option value="22">22</option>
                                            <option value="23">23</option>
                                        </select>
                                        <select class="pull-right" name="hours" size="12" multiple="">
                                            <option value="0">0</option>
                                            <option value="1">1</option>
                                            <option value="2">2</option>
                                            <option value="3">3</option>
                                            <option value="4">4</option>
                                            <option value="5">5</option>
                                            <option value="6">6</option>
                                            <option value="7">7</option>
                                            <option value="8">8</option>
                                            <option value="9">9</option>
                                            <option value="10">10</option>
                                            <option value="11">11</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-sm-2">
                                    <strong class="col-xs-12 text-right">天</strong>
                                    <div class="form-group">
                                        <div class="col-sm-12">
                                            <div class="radio i-checks pull-right">
                                                <label>
                                                    <input type="radio" value="false" name="allDays"> <i></i> 已选择的...
                                                </label>
                                            </div>
                                             <div class="radio i-checks pull-right">
                                                <label>
                                                    <input type="radio" value="true" checked="" name="allDays"> <i></i> 全部
                                                </label>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-sm-12 m-l-n pull-right">
                                        <select class="pull-right" name="days" size="12" multiple="">
                                            <option value="24">24</option>
                                            <option value="25">25</option>
                                            <option value="26">26</option>
                                            <option value="27">27</option>
                                            <option value="28">28</option>
                                            <option value="29">29</option>
                                            <option value="30">30</option>
                                            <option value="31">31</option>
                                        </select>
                                        <select class="pull-right" name="days" size="12" multiple="">
                                            <option value="12">12</option>
                                            <option value="13">13</option>
                                            <option value="14">14</option>
                                            <option value="15">15</option>
                                            <option value="16">16</option>
                                            <option value="17">17</option>
                                            <option value="18">18</option>
                                            <option value="19">19</option>
                                            <option value="20">20</option>
                                            <option value="21">21</option>
                                            <option value="22">22</option>
                                            <option value="23">23</option>
                                        </select>
                                        <select class="pull-right" name="days" size="12" multiple="">
                                            <option value="0">0</option>
                                            <option value="1">1</option>
                                            <option value="2">2</option>
                                            <option value="3">3</option>
                                            <option value="4">4</option>
                                            <option value="5">5</option>
                                            <option value="6">6</option>
                                            <option value="7">7</option>
                                            <option value="8">8</option>
                                            <option value="9">9</option>
                                            <option value="10">10</option>
                                            <option value="11">11</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-sm-2">
                                    <strong class="col-xs-12 text-right">月</strong>
                                    <div class="form-group">
                                        <div class="col-sm-12">
                                            <div class="radio i-checks pull-right">
                                                <label>
                                                    <input type="radio" value="false" name="allMonths"> <i></i> 已选择的...
                                                </label>
                                            </div>
                                             <div class="radio i-checks pull-right">
                                                <label>
                                                    <input type="radio" value="true" checked="" name="allMonths"> <i></i> 全部
                                                </label>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-sm-12 m-l-n pull-right">
                                        <select class="pull-right" name="months" size="12" multiple="">
                                            <option value="1">一月</option>
                                            <option value="2">二月</option>
                                            <option value="3">三月</option>
                                            <option value="4">四月</option>
                                            <option value="5">五月</option>
                                            <option value="6">六月</option>
                                            <option value="7">七月</option>
                                            <option value="8">八月</option>
                                            <option value="9">九月</option>
                                            <option value="10">十月</option>
                                            <option value="11">十一月</option>
                                            <option value="12">十二月</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-sm-2">
                                    <strong class="col-xs-12 text-right">周</strong>
                                    <div class="form-group">
                                        <div class="col-sm-12">
                                            <div class="radio i-checks pull-right">
                                                <label>
                                                    <input type="radio" value="false" name="allWeekdays"> <i></i> 已选择的...
                                                </label>
                                            </div>
                                             <div class="radio i-checks pull-right">
                                                <label>
                                                    <input type="radio" value="true" checked="" name="allWeekdays"> <i></i> 全部
                                                </label>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-sm-12 m-l-n pull-right">
                                       <select class="pull-right" name="weekdays" size="7" multiple="">
                                            <option value="1">星期日</option>
                                            <option value="2">星期一</option>
                                            <option value="3">星期二</option>
                                            <option value="4">星期三</option>
                                            <option value="5">星期四</option>
                                            <option value="6">星期五</option>
                                            <option value="7">星期六</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            <div class="hr-line-dashed"></div>
                            <p>
                                注意：按Ctrl键并单击（或者在Mac系统中按Command键并单击）来选择秒、分钟、小时、天、月和周或取消选择, 但不可以同时指定天和周。
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </form>
        </form>
            <div class="form-group">
                <div class="col-sm-4 col-sm-offset-2">
                    <button class="btn btn-primary" type="submit" id="saveJobbtn">保存</button>
                    <button class="btn btn-white" type="submit" id="backbtn">返回</button>
                </div>
            </div>
        <!-- </form> -->
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
<script type="text/javascript" src="<%=path %>/resources/job/jobDetailInfo.js"></script>
</body>
</html>
