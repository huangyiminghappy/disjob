<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>Ejob - 主页</title>	
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<link href="<%=path %>/resources/hplus/css/bootstrap.min14ed.css" rel="stylesheet" type="text/css">
	<link href="<%=path %>/resources/hplus/css/font-awesome.min93e3.css" rel="stylesheet" type="text/css">
	<link href="<%=path %>/resources/hplus/css/animate.min.css" rel="stylesheet" type="text/css">
	<link href="<%=path %>/resources/hplus/css/style.min862f.css" rel="stylesheet">
	
</head>

<body class="fixed-sidebar full-height-layout gray-bg" style="overflow:hidden">
    <div id="wrapper">
        <!--左侧导航开始-->
        <nav class="navbar-default navbar-static-side" role="navigation">
            <div class="nav-close"><i class="fa fa-times-circle"></i>
            </div>
            <div class="sidebar-collapse">
                <ul class="nav" id="side-menu">
                    <li class="nav-header">
                        <div class="dropdown profile-element">
                            <a data-toggle="dropdown" class="dropdown-toggle" href="#">
                                <span class="clear">
                               <span class="block m-t-xs"><strong class="font-bold">欢迎你：${currentUser.username}</strong></span>
                                <span class="text-muted text-xs block">${currentUser.roleName}<b class="caret"></b></span>
                                </span>
                            </a>
                        </div>
                    </li>
                    <li>
                        <a href="#"><i class="fa fa-home"></i> <span class="nav-label">服务器管理</span><span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level">
                            <li><a class="J_menuItem" href="<%=request.getContextPath() %>/app/page/service/serverInfo" data-index="0">服务器信息</a></li>	
                        </ul>
                    </li>
                    <%-- <li><a href="#"><i class="fa fa-home"></i><span class="nav-label">Ejob</span><span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level">
                            <li><a class="J_menuItem" href="<%=request.getContextPath() %>/resources/hplus/pin_board.html" data-index="0">标签墙</a></li>	
                            <li><a class="J_menuItem" href="<%=request.getContextPath() %>/resources/hplus/calendar.html">日历</a></li>
                        </ul>

                    </li> --%>
                    <%-- <li>
                        <a href="#"><i class="fa fa fa-bar-chart-o"></i><span class="nav-label">图表统计</span><span class="fa arrow"></span></a>
                        <ul class="nav nav-second-level">
                            <li>
                                <a class="J_menuItem" href="<%=request.getContextPath() %>/resources/hplus/graph_echarts.html">百度ECharts</a>
                            </li>
                            <li>
                                <a class="J_menuItem" href="<%=request.getContextPath() %>/resources/hplus/graph_flot.html">Flot</a>
                            </li>
                            <li>
                                <a class="J_menuItem" href="<%=request.getContextPath() %>/resources/hplus/graph_morris.html">Morris.js</a>
                            </li>
                            <li>
                                <a class="J_menuItem" href="<%=request.getContextPath() %>/resources/hplus/graph_rickshaw.html">Rickshaw</a>
                            </li>
                            <li>
                                <a class="J_menuItem" href="<%=request.getContextPath() %>/resources/hplus/graph_peity.html">Peity</a>
                            </li>
                            <li>
                                <a class="J_menuItem" href="<%=request.getContextPath() %>/resources/hplus/graph_sparkline.html">Sparkline</a>
                            </li>
                            <li>
                                <a class="J_menuItem" href="<%=request.getContextPath() %>/resources/hplus/graph_metrics.html">图表组合</a>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <a href="#"><i class="fa fa-gears"></i> <span class="nav-label">system管理</span><span class="fa arrow"></span></a>
                        <ul id="EjobHome" class="nav nav-second-level">
                            <li><a class="J_menuItem" href="<%=request.getContextPath() %>/app/page/sysmenu">菜单管理</a></li>
                            <li><a class="J_menuItem" href="<%=request.getContextPath() %>/app/page/sysrole">角色管理</a></li>
                            <li><a class="J_menuItem" href="<%=request.getContextPath() %>/app/page/sysuser">用户管理</a></li>                     
                        </ul>
                    </li> --%>
                    <%-- <li>
                        <a href="#"><i class="fa fa-magnet"></i> <span class="nav-label">服务器管理</span><span class="fa arrow"></span></a>
                        <ul id="EjobHome" class="nav nav-second-level">
                            <li><a class="J_menuItem" href="<%=request.getContextPath() %>/app/page/service/serverInfo">服务器信息</a></li>	
                        </ul>
                    </li> --%>
					<li>
                        <a href="#"><i class="fa fa-retweet"></i> <span class="nav-label">job管理</span><span class="fa arrow"></span></a>
                        <ul id="EjobHome" class="nav nav-second-level">
                            <li><a class="J_menuItem" href="<%=request.getContextPath() %>/app/page/job/group">job组管理</a></li>	
                            <li><a class="J_menuItem" href="<%=request.getContextPath() %>/app/page/job/alarm">job报警管理</a></li>
                            <li><a class="J_menuItem" href="<%=request.getContextPath() %>/app/page/job/info">job信息管理</a></li>                      
                            <li><a class="J_menuItem" href="<%=request.getContextPath() %>/app/page/monitor/jobExeDetail?from=main">job报警查询</a></li>                      
                            <li><a class="J_menuItem" href="<%=request.getContextPath() %>/app/page/job/bind">job绑定</a></li>
                        </ul>
                    </li>
                    <li>
                        <a href="#"><i class="fa fa fa-bar-chart-o"></i><span class="nav-label">图表统计</span><span class="fa arrow"></span></a>
                        <ul id="EjobHome" class="nav nav-second-level">
                            <li><a class="J_menuItem" href="<%=request.getContextPath() %>/app/page/monitor/minotorInfo">任务监控</a></li>	
                        </ul>
                    </li>
                    <li>
                        <a href="#"><i class="fa fa fa-cutlery"></i><span class="nav-label">工具</span><span class="fa arrow"></span></a>
                        <ul id="EjobHome" class="nav nav-second-level">
                            <li><a class="J_menuItem" href="<%=request.getContextPath() %>/app/page/monitor/cronTransfer">表达式转换</a></li>
                        </ul>
                    </li>
                    <li>
                    	<a href="#"><i class="fa fa fa-cutlery"></i><span class="nav-label">用户管理</span><span class="fa arrow"></span></a>
                        <ul id="EjobHome" class="nav nav-second-level">
                            <li><a class="J_menuItem" href="<%=request.getContextPath() %>/app/page/monitor/auth">授权</a></li>
                            <li><a class="J_menuItem" href="<%=request.getContextPath() %>/app/page/user/userActionList">用户行为</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </nav>
        <!--左侧导航结束-->
        <!--右侧部分开始-->
        <div id="page-wrapper" class="gray-bg dashbard-1">
            <div class="row content-tabs">
                <button class="roll-nav roll-left J_tabLeft navbar-minimalize"><i class="fa fa-bars"></i>
                </button>
                <nav class="page-tabs J_menuTabs">
                    <div class="page-tabs-content">
                        <a href="javascript:;" class="active J_menuTab" data-id="<%=request.getContextPath() %>/resources/hplus/pin_board.html">首页</a>
                    </div>
                </nav>
                <button class="roll-nav roll-right J_tabRight"><i class="fa fa-forward"></i>
                </button>
                <div class="btn-group roll-nav roll-right">
                    <button class="dropdown J_tabClose" data-toggle="dropdown">关闭操作<span class="caret"></span>

                    </button>
                    <ul role="menu" class="dropdown-menu dropdown-menu-right">
                        <li class="J_tabShowActive"><a>定位当前选项卡</a>
                        </li>
                        <li class="divider"></li>
                        <li class="J_tabCloseAll"><a>关闭全部选项卡</a>
                        </li>
                        <li class="J_tabCloseOther"><a>关闭其他选项卡</a>
                        </li>
                    </ul>
                </div>
                <a href="<%=request.getContextPath() %>/app/logout" class="roll-nav roll-right J_tabExit"><i class="fa fa fa-sign-out"></i> 退出</a>
            </div>
            <div class="row J_mainContent" id="content-main">	
                <iframe class="J_iframe" name="iframe0" width="100%" height="100%" src="<%=request.getContextPath() %>/app/page/service/serverInfo" frameborder="0"  data-id="<%=request.getContextPath() %>/resources/hplus/pin_board.html" seamless></iframe>
            </div>
            <div class="footer">
            <!-- <div style="position:absolute; left:expression((this.parentElement.offsetWidth-this.offsetWidth)/2);"> -->
                <div class="pull-right">&copy; 2016-2030 disJob.com</a>
                </div>
            </div>
        </div>
        <!--右侧部分结束-->
    </div>
</body>
<script type="text/javascript" src="<%=path %>/resources/hplus/js/jquery.min.js"></script>
<script type="text/javascript" src="<%=path %>/resources/hplus/js/bootstrap.min.js"></script>
<script type="text/javascript" src="<%=path %>/resources/hplus/js/plugins/metisMenu/jquery.metisMenu.js"></script>
<script type="text/javascript" src="<%=path %>/resources/hplus/js/plugins/slimscroll/jquery.slimscroll.min.js"></script>
<script type="text/javascript" src="<%=path %>/resources/hplus/js/plugins/layer/layer.min.js"></script>
<script type="text/javascript" src="<%=path %>/resources/hplus/js/hplus.min.js"></script>
<script type="text/javascript" src="<%=path %>/resources/hplus/js/contabs.min.js"></script>
<script type="text/javascript" src="<%=path %>/resources/hplus/js/plugins/pace/pace.min.js"></script>
<script type="text/javascript"></script>
</html>