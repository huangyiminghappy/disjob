<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <title>cron转换</title>
    <link href="<%=path%>/resources/cron/themes/bootstrap/easyui.min.css" rel="stylesheet" type="text/css" />
    <link href="<%=path%>/resources/cron/themes/icon.css" rel="stylesheet" type="text/css" />
    <link href="<%=path%>/resources/cron/icon.css" rel="stylesheet" type="text/css" />
    <style type="text/css">
        .line
        {
            height: 25px;
            line-height: 25px;
            margin: 3px;
        }
        .imp
        {
            padding-left: 25px;
        }
        .col
        {
            width: 95px;
        }
        ul {
            list-style:none;
            padding-left:10px;
        }
        li {
            height:20px;
        }
    </style>
</head>
<body>
    <center>
        <div class="easyui-layout" style="width:830px;height:560px; border: 1px rgb(202, 196, 196) solid;
            border-radius: 5px;">
            <div style="height: 100%;">
                <div class="easyui-tabs" data-options="fit:true,border:false">
                    <div title="秒">
						<form id = "secondform">
                        <div class="line">
                            <input type="radio" checked="checked" name="crontype" id="everyRadio" value="every">
                            每秒 允许的通配符[, - * /]</div>
                        <div class="line">
                            <input type="radio" name="crontype" id="rangeRadio" value="range" >
                            从
                            <input class="numberspinner" style="width: 60px;" type="number" min="0" max="59" value="0"
                                name="startAt" id="startAt"> 
                            秒开始,到
							<input class="numberspinner" style="width: 60px;" data-options="min:1,max:59" value="1"
                                name="endAt" id="endAt">秒,每
                            <input class="numberspinner" style="width: 60px;" data-options="min:1,max:59" value="1"
                                name="interval" id="interval">
                            秒执行一次</div>
                        <div class="line">
                            <input type="radio" name="crontype" id="specifyRadio" value="specify">
                            指定</div>
                        <div class="imp specifyList">
                            <input type="checkbox" value="0" name="specify0">00
                            <input type="checkbox" value="1" name="specify1">01
                            <input type="checkbox" value="2" name="specify2">02
                            <input type="checkbox" value="3" name="specify3">03
                            <input type="checkbox" value="4" name="specify4">04
                            <input type="checkbox" value="5" name="specify5">05
                            <input type="checkbox" value="6" name="specify6">06
                            <input type="checkbox" value="7" name="specify7">07
                            <input type="checkbox" value="8" name="specify8">08
                            <input type="checkbox" value="9" name="specify9">09
                        </div>
                        <div class="imp specifyList">
                            <input type="checkbox" value="10" name="specify10">10
                            <input type="checkbox" value="11" name="specify11">11
                            <input type="checkbox" value="12" name="specify12">12
                            <input type="checkbox" value="13" name="specify13">13
                            <input type="checkbox" value="14" name="specify14">14
                            <input type="checkbox" value="15" name="specify15">15
                            <input type="checkbox" value="16" name="specify16">16
                            <input type="checkbox" value="17" name="specify17">17
                            <input type="checkbox" value="18" name="specify18">18
                            <input type="checkbox" value="19" name="specify19">19
                        </div>
                        <div class="imp specifyList">
                            <input type="checkbox" value="20" name="specify20">20
                            <input type="checkbox" value="21" name="specify21">21
                            <input type="checkbox" value="22" name="specify22">22
                            <input type="checkbox" value="23" name="specify23">23
                            <input type="checkbox" value="24" name="specify24">24
                            <input type="checkbox" value="25" name="specify25">25
                            <input type="checkbox" value="26" name="specify26">26
                            <input type="checkbox" value="27" name="specify27">27
                            <input type="checkbox" value="28" name="specify28">28
                            <input type="checkbox" value="29" name="specify29">29
                        </div>
                        <div class="imp specifyList">
                            <input type="checkbox" value="30" name="specify30">30
                            <input type="checkbox" value="31" name="specify31">31
                            <input type="checkbox" value="32" name="specify32">32
                            <input type="checkbox" value="33" name="specify33">33
                            <input type="checkbox" value="34" name="specify34">34
                            <input type="checkbox" value="35" name="specify35">35
                            <input type="checkbox" value="36" name="specify36">36
                            <input type="checkbox" value="37" name="specify37">37
                            <input type="checkbox" value="38" name="specify38">38
                            <input type="checkbox" value="39" name="specify39">39
                        </div>
                        <div class="imp specifyList">
                            <input type="checkbox" value="40" name="specify40">40
                            <input type="checkbox" value="41" name="specify41">41
                            <input type="checkbox" value="42" name="specify42">42
                            <input type="checkbox" value="43" name="specify43">43
                            <input type="checkbox" value="44" name="specify44">44
                            <input type="checkbox" value="45" name="specify45">45
                            <input type="checkbox" value="46" name="specify46">46
                            <input type="checkbox" value="47" name="specify47">47
                            <input type="checkbox" value="48" name="specify48">48
                            <input type="checkbox" value="49" name="specify49">49
                        </div>
                        <div class="imp specifyList">
                            <input type="checkbox" value="50" name="specify50">50
                            <input type="checkbox" value="51" name="specify51">51
                            <input type="checkbox" value="52" name="specify52">52
                            <input type="checkbox" value="53" name="specify53">53
                            <input type="checkbox" value="54" name="specify54">54
                            <input type="checkbox" value="55" name="specify55">55
                            <input type="checkbox" value="56" name="specify56">56
                            <input type="checkbox" value="57" name="specify57">57
                            <input type="checkbox" value="58" name="specify58">58
                            <input type="checkbox" value="59" name="specify59">59
                        </div>
						</form>
                    </div>
                    <div title="分">
                    	<form id = "minuteform">
                        <div class="line">
                            <input type="radio" checked="checked" name="crontype" id="everyRadio" value="every">
                            每秒 允许的通配符[, - * /]</div>
                        <div class="line">
                            <input type="radio" name="crontype" id="rangeRadio" value="range" >
                            从
                            <input class="numberspinner" style="width: 60px;" data-options="min:0,max:59" value="0"
                                name="startAt" id="startAt"> 
                            分钟开始,到
							<input class="numberspinner" style="width: 60px;" data-options="min:1,max:59" value="1"
                                name="endAt" id="endAt">分钟,每
                            <input class="numberspinner" style="width: 60px;" data-options="min:1,max:59" value="1"
                                name="interval" id="interval">
                            分钟执行一次</div>
                        <div class="line">
                            <input type="radio" name="crontype" id="specifyRadio" value="specify">
                            指定</div>
                        <div class="imp specifyList">
                            <input type="checkbox" value="0" name="specify0">00
                            <input type="checkbox" value="1" name="specify1">01
                            <input type="checkbox" value="2" name="specify2">02
                            <input type="checkbox" value="3" name="specify3">03
                            <input type="checkbox" value="4" name="specify4">04
                            <input type="checkbox" value="5" name="specify5">05
                            <input type="checkbox" value="6" name="specify6">06
                            <input type="checkbox" value="7" name="specify7">07
                            <input type="checkbox" value="8" name="specify8">08
                            <input type="checkbox" value="9" name="specify9">09
                        </div>
                        <div class="imp specifyList">
                            <input type="checkbox" value="10" name="specify10">10
                            <input type="checkbox" value="11" name="specify11">11
                            <input type="checkbox" value="12" name="specify12">12
                            <input type="checkbox" value="13" name="specify13">13
                            <input type="checkbox" value="14" name="specify14">14
                            <input type="checkbox" value="15" name="specify15">15
                            <input type="checkbox" value="16" name="specify16">16
                            <input type="checkbox" value="17" name="specify17">17
                            <input type="checkbox" value="18" name="specify18">18
                            <input type="checkbox" value="19" name="specify19">19
                        </div>
                        <div class="imp specifyList">
                            <input type="checkbox" value="20" name="specify20">20
                            <input type="checkbox" value="21" name="specify21">21
                            <input type="checkbox" value="22" name="specify22">22
                            <input type="checkbox" value="23" name="specify23">23
                            <input type="checkbox" value="24" name="specify24">24
                            <input type="checkbox" value="25" name="specify25">25
                            <input type="checkbox" value="26" name="specify26">26
                            <input type="checkbox" value="27" name="specify27">27
                            <input type="checkbox" value="28" name="specify28">28
                            <input type="checkbox" value="29" name="specify29">29
                        </div>
                        <div class="imp specifyList">
                            <input type="checkbox" value="30" name="specify30">30
                            <input type="checkbox" value="31" name="specify31">31
                            <input type="checkbox" value="32" name="specify32">32
                            <input type="checkbox" value="33" name="specify33">33
                            <input type="checkbox" value="34" name="specify34">34
                            <input type="checkbox" value="35" name="specify35">35
                            <input type="checkbox" value="36" name="specify36">36
                            <input type="checkbox" value="37" name="specify37">37
                            <input type="checkbox" value="38" name="specify38">38
                            <input type="checkbox" value="39" name="specify39">39
                        </div>
                        <div class="imp specifyList">
                            <input type="checkbox" value="40" name="specify40">40
                            <input type="checkbox" value="41" name="specify41">41
                            <input type="checkbox" value="42" name="specify42">42
                            <input type="checkbox" value="43" name="specify43">43
                            <input type="checkbox" value="44" name="specify44">44
                            <input type="checkbox" value="45" name="specify45">45
                            <input type="checkbox" value="46" name="specify46">46
                            <input type="checkbox" value="47" name="specify47">47
                            <input type="checkbox" value="48" name="specify48">48
                            <input type="checkbox" value="49" name="specify49">49
                        </div>
                        <div class="imp specifyList">
                            <input type="checkbox" value="50" name="specify50">50
                            <input type="checkbox" value="51" name="specify51">51 
                            <input type="checkbox" value="52" name="specify52">52
                            <input type="checkbox" value="53" name="specify53">53
                            <input type="checkbox" value="54" name="specify54">54
                            <input type="checkbox" value="55" name="specify55">55
                            <input type="checkbox" value="56" name="specify56">56
                            <input type="checkbox" value="57" name="specify57">57
                            <input type="checkbox" value="58" name="specify58">58
                            <input type="checkbox" value="59" name="specify59">59
                        </div>
						</form>
					</div>
                    <div title="小时">
						<form id = "hourform">
                        <div class="line">
                            <input type="radio" checked="checked" name="crontype" id="everyRadio" value="every">
                            每小时 允许的通配符[, - * /]</div>
                        <div class="line">
                            <input type="radio" name="crontype" id="rangeRadio" value="range" >
                            从
                            <input class="numberspinner" style="width: 60px;" data-options="min:0,max:23" value="0"
                                name="startAt" id="startAt"> 
                            小时开始,到
							<input class="numberspinner" style="width: 60px;" data-options="min:1,max:23" value="1"
                                name="endAt" id="endAt">小时,每
                            <input class="numberspinner" style="width: 60px;" data-options="min:1,max:23" value="1"
                                name="interval" id="interval">
                            小时执行一次</div>
                        <div class="line">
                            <input type="radio" name="crontype" id="specifyRadio" value="specify">
                            指定</div>
                        <div class="imp specifyList">
                            AM:
                            <input type="checkbox" value="0" name="specify0">00
                            <input type="checkbox" value="1" name="specify1">01
                            <input type="checkbox" value="2" name="specify2">02
                            <input type="checkbox" value="3" name="specify3">03
                            <input type="checkbox" value="4" name="specify4">04
                            <input type="checkbox" value="5" name="specify5">05
                            <input type="checkbox" value="6" name="specify6">06
                            <input type="checkbox" value="7" name="specify7">07
                            <input type="checkbox" value="8" name="specify8">08
                            <input type="checkbox" value="9" name="specify9">09
                            <input type="checkbox" value="10" name="specify10">10
                            <input type="checkbox" value="11" name="specify11">11
                        </div>
                        <div class="imp specifyList">
                            PM:
                            <input type="checkbox" value="12" name="specify12">12
                            <input type="checkbox" value="13" name="specify13">13
                            <input type="checkbox" value="14" name="specify14">14
                            <input type="checkbox" value="15" name="specify15">15
                            <input type="checkbox" value="16" name="specify16">16
                            <input type="checkbox" value="17" name="specify17">17
                            <input type="checkbox" value="18" name="specify18">18
                            <input type="checkbox" value="19" name="specify19">19
                            <input type="checkbox" value="20" name="specify20">20
                            <input type="checkbox" value="21" name="specify21">21
                            <input type="checkbox" value="22" name="specify22">22
                            <input type="checkbox" value="23" name="specify23">23
                        </div>
						</form>
                    </div>
                    <div title="日">
						<form id = "dayform">
                        <div class="line">
                            <input type="radio" checked="checked" name="crontype" id="everyRadio" value="every">
                            每日 允许的通配符[, - * / L W]</div>
                        <div class="line">
                            <input type="radio" name="crontype" id="noneRadio" value="none">
                            不指定</div>
                        <div class="line">
                            <input type="radio" name="crontype" id="rangeRadio" value="range" >
                            周期从
                            <input class="numberspinner" style="width: 60px;" data-options="min:1,max:31" value="1"
                                id="startAt" name="startAt">
                            -
                            <input class="numberspinner" style="width: 60px;" data-options="min:2,max:31" value="2"
                                id="endAt" name="endAt">
                            日,每
                            <input class="numberspinner" style="width: 60px;" data-options="min:1,max:31" value="1"
                                name="interval" id="interval">
                            天执行一次</div>
                        <div class="line">
                            <input type="radio" name="crontype" value="nearestweekday" id="nearestweekdayRadio">
                            每月
                            <input class="numberspinner" style="width: 60px;" data-options="min:1,max:31" value="1"
                                id="nearestweekday" name="nearestweekday">
                            号最近的那个工作日</div>
                        <div class="line">
                            <input type="radio" name="crontype" value="lastday" id="lastdayRadio">
                            本月倒数第
							<input class="numberspinner" style="width: 60px;" data-options="min:1,max:31" value="1"
                                id="lastIndexDay" name="lastIndexDay">
							天</div>
                        <div class="line">
                            <input type="radio" name="crontype" id="specifyRadio" value="specify">
                            指定</div>
                        <div class="imp specifyList">
                            <input type="checkbox" value="1" name="specify1">01
                            <input type="checkbox" value="2" name="specify2">02
                            <input type="checkbox" value="3" name="specify3">03
                            <input type="checkbox" value="4" name="specify4">04
                            <input type="checkbox" value="5" name="specify5">05
                            <input type="checkbox" value="6" name="specify6">06
                            <input type="checkbox" value="7" name="specify7">07
                            <input type="checkbox" value="8" name="specify8">08
                            <input type="checkbox" value="9" name="specify9">09
                            <input type="checkbox" value="10" name="specify10">10
                            <input type="checkbox" value="11" name="specify11">11
                            <input type="checkbox" value="12" name="specify12">12
                            <input type="checkbox" value="13" name="specify13">13
                            <input type="checkbox" value="14" name="specify14">14
                            <input type="checkbox" value="15" name="specify15">15
                            <input type="checkbox" value="16" name="specify16">16
                        </div>
                        <div class="imp specifyList">
                            <input type="checkbox" value="17" name="specify17">17
                            <input type="checkbox" value="18" name="specify18">18
                            <input type="checkbox" value="19" name="specify19">19
                            <input type="checkbox" value="20" name="specify20">20
                            <input type="checkbox" value="21" name="specify21">21
                            <input type="checkbox" value="22" name="specify22">22
                            <input type="checkbox" value="23" name="specify23">23
                            <input type="checkbox" value="24" name="specify24">24
                            <input type="checkbox" value="25" name="specify25">25
                            <input type="checkbox" value="26" name="specify26">26
                            <input type="checkbox" value="27" name="specify27">27
                            <input type="checkbox" value="28" name="specify28">28
                            <input type="checkbox" value="29" name="specify29">29
							<input type="checkbox" value="30" name="specify30">30
                            <input type="checkbox" value="31" name="specify31">31
                        </div>
						</form>
                    </div>
                    <div title="月">
                        <form id = "monthform">
                        <div class="line">
                            <input type="radio" checked="checked" name="crontype" id="everyRadio" value="every">
                            每月 允许的通配符[, - * /]</div>
						<div class="line">
                            <input type="radio" name="crontype" id="noneRadio" value="none">
                            不指定</div>
                        <div class="line">
                            <input type="radio" name="crontype" id="rangeRadio" value="range" >
                            从
                            <input class="numberspinner" style="width: 60px;" data-options="min:1,max:12" value="0"
                                name="startAt" id="startAt"> 
                            月开始,到
							<input class="numberspinner" style="width: 60px;" data-options="min:1,max:12" value="1"
                                name="endAt" id="endAt">月,每
                            <input class="numberspinner" style="width: 60px;" data-options="min:1,max:12" value="1"
                                name="interval" id="interval">
                            月执行一次</div>
                        <div class="line">
                            <input type="radio" name="crontype" id="specifyRadio" value="specify">
                            指定</div>
                        <div class="imp specifyList">
                            <input type="checkbox" value="1" name="specify1">01
                            <input type="checkbox" value="2" name="specify2">02
                            <input type="checkbox" value="3" name="specify3">03
                            <input type="checkbox" value="4" name="specify4">04
                            <input type="checkbox" value="5" name="specify5">05
                            <input type="checkbox" value="6" name="specify6">06
                            <input type="checkbox" value="7" name="specify7">07
                            <input type="checkbox" value="8" name="specify8">08
                            <input type="checkbox" value="9" name="specify9">09
                            <input type="checkbox" value="10" name="specify10">10
                            <input type="checkbox" value="11" name="specify11">11
                            <input type="checkbox" value="12" name="specify12">12
                        </div>
						</form>
                    </div>
                    <div title="周">
						<form id = "weekform">
                        <div class="line">
                            <input type="radio" checked="checked" name="crontype" id="everyRadio" value="every">
                            每周 允许的通配符[, - * / L #]</div>
                        <div class="line">
                            <input type="radio" name="crontype" id="noneRadio" value="none">
                            不指定</div>
                        <div class="line">
                            <input type="radio" name="crontype" id="nthweekdayRadio" value="nthweekday">
                            第<input class="numberspinner" style="width: 60px;" data-options="min:1,max:4" value="1"
                                id="nthweek" name="nthweek">
                            周 的星期<input class="numberspinner" style="width: 60px;" data-options="min:1,max:7"
                                id="weekday" name="weekday" value="1"></div>
                        <div class="line">
                            <input type="radio" name="crontype" id="lastweekdayRadio" value="lastweekday">
                            本月最后一个星期<input class="numberspinner" style="width: 60px;" data-options="min:1,max:7"
                                id="lastweekdayindex" name="lastweekdayindex" value="1"></div>
                        <div class="line">
                            <input type="radio" name="crontype" id="specifyRadio" value="specify">
                            指定</div>
                        <div class="imp weekList">
                            <input type="checkbox" value="1" name="specify1">1
                            <input type="checkbox" value="2" name="specify2">2
                            <input type="checkbox" value="3" name="specify3">3
                            <input type="checkbox" value="4" name="specify4">4
                            <input type="checkbox" value="5" name="specify5">5
                            <input type="checkbox" value="6" name="specify6">6
                            <input type="checkbox" value="7" name="specify7">7
                        </div>
						</form>
                    </div>
                    <div title="年">
                        <form id = "yearform">
                        <div class="line">
                            <input type="radio" name="crontype" id="noneRadio" value="none">
                            不指定 允许的通配符[, - * /] 非必填</div>
                        <div class="line">
                            <input type="radio" checked="checked" name="crontype" id="everyRadio" value="every">
                            每年</div>
							
                        <div class="line">
							<input type="radio" name="crontype" id="rangeRadio" value="range" >
                            周期从
                            <input class="numberspinner" style="width: 60px;" data-options="min:2013,max:3000" value="2016"
                                name="startAt" id="startAt"> 
                            年开始,到
							<input class="numberspinner" style="width: 60px;" data-options="min:2014,max:3000" value="2017"
                                name="endAt" id="endAt">年</div>
						</form>
                    </div>
                </div>
            </div>
            <div data-options="region:'south',border:false" style="height:250px">
                <fieldset style="border-radius: 3px; height: 220px;">
                    <legend>表达式</legend>
                    <table style="height: 100px;">
                        <tbody>
                             <tr>
							    <td>Cron 表达式:</td>
							    <td colspan="6"><input type="text" placeholder="从上方选项勾选生成" name="cronResult" style="width: 100%;" id="cronResult"
                                         /></td>
							    <td><input type="button" value="生成quartz" id="generatorQuartzExpressionBtn"/></td>
							    <td><input type="button" value="重置" id="resetBtn"/></td>
						    </tr>
							<tr>
								<td>crontab 表达式转换:</td>
							    <td colspan="6"><input type="text" placeholder="请输入crontab" name="crontabs" style="width: 100%;" id="crontabs"
                                         /></td>
							    <td><input type="button" value="解析为quartzCron" id="transferCrontabToQuartzExpressionBtn"/></td>
							    <td colspan="6"><input type="text" placeholder="转换结果" name="quartzs" style="width: 100%;" id="quartzs"
                                         /></td>
							</tr>
                            <tr>
                                <td colspan="8" id="runTime"><div id="result"></div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </fieldset>
            </div>
        </div>
    </center>
    <script type="text/javascript" src="<%=path %>/resources/cron/jquery-1.6.2.min.js"></script>
    <script type="text/javascript" src="<%=path %>/resources/cron/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="<%=path %>/resources/cron/cron.js"></script>
</body>
</html>
