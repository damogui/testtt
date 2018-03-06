<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Home</title>
	<link href='/package/font-awesome/css/font-awesome.min.css' rel='stylesheet' type='text/css' />
	<link href='/package/easyui/themes/material/easyui.css' rel='stylesheet' type='text/css' />
	<link href='/package/easyui/themes/easyui.extend.css' rel='stylesheet' type='text/css' />
	<link href='/package/easyui/themes/color.css' rel='stylesheet' type='text/css' />
	<link href='/package/easyui/themes/icon.css' rel='stylesheet' type='text/css' />
	<link href='/panda-bizbase/home.css' rel='stylesheet' type='text/css' />
</head>
<body>
	<div class="page-content page-index">
		<jsp:include page="/panda-bizbase/account-info.jsp"></jsp:include>
		<jsp:include page="portal-statistic.jsp"></jsp:include>
	</div>
</body>
<script src='/package/easyui/jquery.min.js'></script>
<script src='/package/layer/layer.js'></script>
<script src='/package/easyui/jquery.easyui.min.js'></script>
<script src='/package/easyui/locale/easyui-lang-zh_CN.js'></script>
<script src='/package/easyui/jquery.easyui.extend.js'></script>

<script src='/panda-res/js/system.js'></script>
<script src='/panda-res/js/panda.core.js'></script>
<script src='/gsb/supplier/js/portal-statistic.js'></script>

	<script>
		//销售简报
		var brief = new com.gongsibao.crm.web.home.Briefing();
		//跟进统计
		var foolow = new com.gongsibao.crm.web.home.Foolow();
		//预估业绩
		var forecast = new com.gongsibao.crm.web.home.Forecast();
		//漏斗统计
		var funnel = new com.gongsibao.crm.web.home.Funnel(); 
		
		$(function() {
			$("#briefing").find('p >span').eq(0).text("新增任务数：" + brief.briefingCount('getNewTasksCount',null) + "个");
			$("#briefing").find('p >span').eq(1).text("未启动任务数：" + brief.briefingCount('getUnStartTasksCount',null) + "个");
			$("#briefing").find('p >span').eq(2).text("待跟进任务数：" + brief.briefingCount('getUnfoolowTasksCount',null) + "个");
			$("#briefing").find('p >span').eq(3).text("超时任务数：" + brief.briefingCount('getTimeOutTasksCount',null) + "个");
			$("#briefing").find('p >span').eq(4).text("异常待跟进任务数：" + brief.briefingCount('getExceptUntreatedTasksCount',null) + "个");
			$("#briefing").find('p >span').eq(5).text("公海：" + brief.briefingCount('getHighSeasCount',null) + "个");
			
			foolow.foolowCount('getFoolowSatatistic',null);
			
			forecast.forecastAmount('getForecastAmount',1);
			forecast.forecastAmount('getForecastAmount',2);
			forecast.forecastAmount('getForecastAmount',3);
			
			funnel.funnelXSCount('getXSCount'); 
			funnel.funnelCodeCount('getCodeTaskCount');
		});
	</script>
</html>
