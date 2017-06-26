$(function() {
	var $tableName = $("#groupListTable"), $mode = $('#mode');
	
	$mode.change(function(){
		console.info("mode change");
		if($mode.val() == "bind"){
			
		}else if($mode.val() == "rebind"){
			
		}
	});
	function responseHandler(res) {
		return {"rows":res};
	};
	
	bind = function(method){
		var sessions = $('#choseIps').val();
		var groups = $tableName.bootstrapTable('getSelections');
		if($.isEmptyObject(sessions) || $.isEmptyObject(groups)){
			
			return;
		}
		var groupNames = [];
		for(var i = 0; i < groups.length ; i++){
			groupNames.push(groups[i].groupName);
		}
		var doActionUrl;
		if(method == "bind"){
			doActionUrl = ejob.bindJobUrl;
		}else{
			doActionUrl = ejob.reBindJobUrl;
		}
		ejob.post(doActionUrl,{
			sessions : $.toJSON(sessions),
			groupNames : $.toJSON(groupNames)
		}, function(result){
			ejob.toastr(result, function(){$tableName.bootstrapTable('refresh')});
		});
	}
	loadtoobar = function(){
		ejob.post(ejob.getCanBindSessionList,{},function(bindSessionList){
			var toolbarHtml = '<div class="input-group">'+
			'<button class="btn btn-primary" type="button" id="refresh">刷新</button>' +
			'<button class="btn btn-primary" type="button" id="bindJob">绑定</button>' +
			'<button class="btn btn-primary" type="button" id="reBindJob">解绑再绑定</button>' +
			'<div class="input-group">'+
			'<select id="choseIps" data-placeholder="请选择会话" class="chosen-select" multiple style="width:350px;" tabindex="10">';
			
			for(var i = 0; i < bindSessionList.length; i ++){
				toolbarHtml += '<option value="' + bindSessionList[i] + '">' + bindSessionList[i] + '</option>';
			}
			toolbarHtml +='</select></div><div>';
			$('#tabletoolbar').html("");
			$('#tabletoolbar').append(toolbarHtml);
			
			$("#choseIps").chosen();
			$('#bindJob').click(function(){
				bind("bind");
			});
			$('#reBindJob').click(function(){
				bind("rebind");
			});
			$('#refresh').click(function(){
				$tableName.bootstrapTable('refresh')
			});
		});
	};
	displayTable = function(){
		$tableName.bootstrapTable({
			method: 'post',
			url: ejob.jobGroupPageUrl,
//		height: "auto",
			dataType: "json",
			pagination: false,
			"queryParamsType": "limit",
			contentType: "application/x-www-form-urlencoded",
			singleSelect: false,
			pageSize: 10,
			pageNumber:1,
			clickToSelect:true,
			search: true, //不显示 搜索框
			columns: [
			          {field:'checkbox',checkbox: true/* ,formatter:stateFormatter */},
//			          {title: 'id',field: 'id'},
			          {title: '名称',field: 'groupName'},
			          {title: '是否已绑定',field: 'binded'},
			          {title: '已绑定的会话',field: 'bindSession'},
			          {title: '备注',field: 'remark'}
			          ],
          showColumns: true, //不显示下拉框（选择显示的列）
          sidePagination: "server", //服务端请求
          responseHandler: responseHandler,
          toolbar:'<div id="tabletoolbar"></div>',
          onLoadSuccess : loadtoobar
		});
	};
	displayTable();
});