(function(window, $) {
	
	function responseHandler(res) {
		
		return {"rows":res.rows,"total":res.total};
	};
	
	$('#userActionList').bootstrapTable({
		method: 'post',
		url: ejob.userActionListUrl,
//		height: "auto",
		dataType: "json",
		pagination: true,
		"queryParamsType": "limit",
		contentType: "application/x-www-form-urlencoded",
		singleSelect: false,
		pageSize: 10,
		pageNumber:1,
		clickToSelect:true,
		search: true, //不显示 搜索框
		columns: [
		          {field:'checkbox',checkbox: true/* ,formatter:stateFormatter */},
		          {title: 'id',field: 'id'},
		          {title: '用户名',field: 'username'},
		          {title: '操作',field: 'permitItem'},
		          {title: '操作日期',field: 'actionDate'},
		          {title: '操作参数',field: 'actionParam'},
		          {title: '操作主机',field: 'host'},
		          {title: '操作IP',field: 'addr'}
		],
		showColumns: true, //不显示下拉框（选择显示的列）
		sidePagination: "server", //服务端请求
		responseHandler: responseHandler
	 });
	
})(window, jQuery);