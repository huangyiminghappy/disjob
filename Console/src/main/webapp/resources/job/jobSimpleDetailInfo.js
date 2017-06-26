(function(window, $) {
	_back = function(){
		window.location = ejob.jobInfoUrl + "?groupName=" + $("#groupName").val() + "&jobName=" + $("#jobName").val();// +"&limit="+limit+"&offset="+offset;		
	}
	$("#backbtn").click(function(){
		_back();
	});
	
	$().ready(function() {
		// 在键盘按下并释放及提交后验证提交表单
		$("#job-form").validate({
			rules : {
				jobName : "required",
				groupName : "required",
//				filePath : "required",
				className : "required",
				methodName : "required",
				cronExpression : "required"
			},
			messages : {
				jobName : "请输入任务名",
				groupName : "请选择任务组",
//				filePath : "请输入文件路径",
				className : "请输入类名",
				methodName : "请输入方法名",
				cronExpression : "请输入时间表达式"
			}
		});
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
	
	forUpdatePage = function(){
		if(ejob.getUrlParam("method") == 'edit'){
			$('#groupName').attr('disabled','true');
		}
	}
	
	forUpdatePage();
	
	$("#saveJobbtn").click(function(){
		var $jobform = $('#job-form');
		if($jobform.valid()){
			var jobformobj = $jobform.ejobSerialize();
			jobformobj.groupName = $('#groupName').val();
			ejob.post(ejob.saveJobDetail,{
				job : $.toJSON(jobformobj),
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
	
})(window, jQuery)