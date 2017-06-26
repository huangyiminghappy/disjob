(function(window, $) {
	

	$().ready(function() {
		// 在键盘按下并释放及提交后验证提交表单
		$("#job-form").validate({
			rules : {
				jobName : "required",
				groupName : "required",
//				filePath : "required",
				className : "required",
				methodName : "required"
			},
			messages : {
				jobName : "请输入任务名",
				groupName : "请选择任务组",
//				filePath : "请输入文件路径",
				className : "请输入类名",
				methodName : "请输入方法名"
			}
		});
	});
	
	_back = function(){
		window.location = ejob.jobInfoUrl + "?groupName=" + $("#groupName").val() + "&jobName=" + $("#jobName").val();// +"&limit="+limit+"&offset="+offset;
	}
	$("#backbtn").click(function(){
		_back();
	});
	

	var start = {
		elem : "#endTime",
		format : "YYYY-MM-DD hh:mm:ss",
		min : "1990-07-01 00:00:00",
		max : "2099-07-01 00:00:00",
		istime : true,
		istoday : false
//		choose : function(datas) {
//			end.min = datas;
//			end.start = datas
//		}
	};
	laydate(start);
	
	$("#saveJobbtn").click(function(){
		var $jobform = $('#job-form');
		if($jobform.valid()){
			var jobformobj = $jobform.ejobSerialize();
			var cronformobj = $('#cron-form').serializeCron();
			jobformobj.groupName = $('#groupName').val();
			if(!beforeSave(jobformobj, cronformobj)){
				return;
			}
			ejob.post(ejob.saveJobDetail,{
				job : $.toJSON(jobformobj),
				cron : $.toJSON(cronformobj),
				method : ejob.getUrlParam("method")
			},function(data){
				if(data.successful){
					_back();
				}else{
					toastr["error"](data.msg,"操作结果：");
				}
			});
		}
	});
	
	fillGroupList = function(){
		ejob.selector(ejob.jobGroupListUrl,{},$("#groupName"), ejob.getUrlParam("groupName"));
	};
	
	fillGroupList();
	
	forUpdatePage = function(){
		if(ejob.getUrlParam("method") == 'edit'){
			$('#groupName').attr('disabled','true');
		}
	}
	
	forUpdatePage();
	
	beforeSave = function(jobformobj, cronformobj){
		if(cronformobj.allSeconds == "false"){
			if(cronformobj.seconds.length ==0){
				toastr["error"]("请选择秒","操作结果：");
				return false;
			}
		}
		if(cronformobj.allMins == "false"){
			if(cronformobj.mins.length ==0){
				toastr["error"]("请选择分","操作结果：");
				return false;
			}
		}
		if(cronformobj.allHours == "false"){
			if(cronformobj.hours.length ==0){
				toastr["error"]("请选择小时","操作结果：");
				return false;
			}
		}
		if(cronformobj.allDays == "false"){
			if(cronformobj.days.length ==0){
				toastr["error"]("请选择天","操作结果：");
				return false;
			}
		}
		if(cronformobj.allMonths == "false"){
			if(cronformobj.months.length ==0){
				toastr["error"]("请选择月","操作结果：");
				return false;
			}
		}
		if(cronformobj.allWeekdays == "false"){
			if(cronformobj.weekdays.length ==0){
				toastr["error"]("请选择周","操作结果：");
				return false;
			}
		}
		if(cronformobj.allDays == "false" &&  cronformobj.allWeekdays == "false"){
			toastr["error"]("不可以同时指定天和周","操作结果：");
			return false;
		}
		return true;
	};
	
	$(document).ready(function(){
		var cronExpression = $('#cronExpression').val()
		if(cronExpression.trim().length = 0){
			return;
		}
		ejob.post(ejob.transferFromCron,{
			cronExpression : cronExpression
		},function(data){
			if(!data.chooseSpecial){
				$('[name="chooseSpecial"][value="false"]').iCheck('check');
				if(data.allSeconds){
					$('[name="allSeconds"][value="true"]').iCheck('check');
				}else{
					$('[name=seconds]').val(data.seconds);
					$('[name="allSeconds"][value="false"]').iCheck('check');
				}
				if(data.allMins){
					$('[name="allMins"][value="true"]').iCheck('check');
				}else{
					$('[name=mins]').val(data.mins);
					$('[name="allMins"][value="false"]').iCheck('check');
				}
				if(data.allHours){
					$('[name="allHours"][value="true"]').iCheck('check');
				}else{
					$('[name=hours]').val(data.hours);
					$('[name="allHours"][value="false"]').iCheck('check');
				}
				if(data.allDays){
					$('[name="allDays"][value="true"]').iCheck('check');
				}else{
					$('[name=days]').val(data.days);
					$('[name="allDays"][value="false"]').iCheck('check');
				}
				if(data.allMonths){
					$('[name="allMonths"][value="true"]').iCheck('check');
				}else{
					$('[name=months]').val(data.months);
					$('[name="allMonths"][value="false"]').iCheck('check');
				}
				if(data.allWeekdays){
					$('[name="allWeekdays"][value="true"]').iCheck('check');
				}else{
					$('[name=weekdays]').val(data.weekdays);
					$('[name="allWeekdays"][value="false"]').iCheck('check');
				}
			}else{
				$('#special').val(data.special);
				$('[name="chooseSpecial"][value="true"]').iCheck('check');
			}
		});
	});
})(window, jQuery)