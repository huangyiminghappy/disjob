(function(window, $) {
	initSelector();
	
	function initSelector() {
		ejob.selector("service/auth/getUserList", {}, $("#username"));
		ejob.selector("service/auth/getJobgroup", {}, $("#jobgroup"));
		ejob.selector("service/permit/getUserList", {}, $("#username_permit"));
		ejob.post("service/permit/getPermitList", {}, function(data){
			var selectorHtml = "";
			for (var i = 0, size = data.length; i < size; i++) {
				selectorHtml += '<option value="' + data[i].id + '">'
				+ data[i].desciption + '</option>';
			}
			$('#permit_item').append(selectorHtml);
		});
		
	};
	function getPermitInfo(){
		var permititem = $('#permit_item').val();
		var username = $('#username_permit').val();
		if (permititem.trim().length == 0 || username.trim().length == 0) {
			return;
		}
		ejob.post(ejob.getPermitInfo, {
			permititem : permititem,
			username : username
		},function(data){
			$("#permit").prop('checked',data);
		});
	};
	function getAuthInfo() {
		var $jobgroup = $("#jobgroup");
		var jobgroup = $('#jobgroup').val();
		var username = $('#username').val();
		if (jobgroup.trim().length == 0 || username.trim().length == 0) {
			return;
		}
		
		ejob.ajax("service/auth/getAuthInfos", {
			jobgroup : jobgroup,
			username : username
		}, function(data){
			if (data[0]) {
				$('#reader').prop('checked', true);
			} else {
				$('#reader').prop('checked', false);
			}
			if (data[1]) {
				$('#owner').prop('checked', true);
			} else {
				$('#owner').prop('checked', false);
			}
		});
		
	};

	$("#username").change(function() {
		getAuthInfo();
	});
	$("#jobgroup").change(function() {
		getAuthInfo();
	});

	function authAction($authtype) {
		var jobgroup = $('#jobgroup').val(), username = $('#username').val(), url, checked = $(
				$authtype).is(":checked"), authtype = $($authtype).attr('id');
		if(username.trim().length == 0 || jobgroup.trim().length == 0){
			return;
		}
		$($authtype).prop('disabled',true);
		var timeoutID = window.setTimeout(function(){$($authtype).prop('disabled',false);window.clearTimeout(timeoutID)},ejob.btnsleeptime);
		if (checked) {
			url = "service/auth/auth";
		} else {
			url = "service/auth/unAuth";
		}
		
		ejob.ajax(url, {
			username : username,
			jobgroup : jobgroup,
			authtype : authtype
		}, function(data){
			var operator = checked ? "授权" : "取消授权";
			var msg = "用户[" + username + "],任务组[" + jobgroup + "], 权限 [" + authtype + "] " + operator;
			if(data.successful){
				toastr["info"](msg + "成功", "操作结果：");
			}else{
				toastr["error"](msg + "失败, 原因 : " + data.msg, "操作结果：");					
			}
		});
	};
	$("#reader").change(function() {
		authAction(this);
	});
	$("#owner").change(function() {
		authAction(this);
	});
	$("#permit").change(function() {
		permitAction(this);
	});
	function permitAction($ele){
		var checked = $($ele).is(":checked");
		var url;
		if(checked){
			url = ejob.permiturl;
		}else{
			url = ejob.unpermiturl;
		}
		$($ele).prop('disabled',true);
		var timeoutID = window.setTimeout(function(){$($ele).prop('disabled',false);window.clearTimeout(timeoutID)},ejob.btnsleeptime);
		var permititem = $('#permit_item').val();
		var username = $('#username_permit').val();
		if(username.trim().length == 0 || permititem.trim().length == 0){
			return;
		}
		ejob.post(url,{permititem:permititem,username:username},function(data){
			var operator = checked ? "授权" : "取消授权";
			var msg = "用户[" + username + "],权限[" + $("#permit_item option:selected").text() + "] " + operator;
			if(data.successful){
				toastr["info"](msg + "成功", "操作结果：");
			}else{
				toastr["info"](msg + "失败", "操作结果：");				
			}
		});
	};
	$("#username_permit").change(function() {
		getPermitInfo();
	});
	$("#permit_item").change(function() {
		getPermitInfo();
	});
})(window, jQuery);