(function(window, $) {
	var disjob = {};
	window.disjob = disjob; 
	$.disjob = disjob;
	disjob.projectname = "disjob";
	disjob.baseurl = "/" + disjob.projectname + "/app/";
	disjob.btnsleeptime = 200;
	
	disjob.jobInfoUrl = disjob.baseurl + "page/job/info";
	disjob.permiturl = "service/permit/auth";
	disjob.unpermiturl = "service/permit/unAuth";
	disjob.getPermitInfo = "service/permit/getPermitInfo";
	disjob.savdisjobDetail = "service/job/info/savdisjobDetail";
	disjob.transferFromCron = "service/job/cron/transferFromCron";
	disjob.jobGroupListUrl = "service/job/group/getGroupList";
	disjob.getCanBindSessionList = "service/job/bind/getBindSession";
	disjob.bindJobUrl = "service/job/bind/doBind";
	disjob.reBindJobUrl = "service/job/bind/doReBind";
//	disjob.jobGroupPageUrl = "service/job/group/listPage";
	
	disjob.userActionListUrl = disjob.baseurl + "service/user/userActionList";
	disjob.jobAddNewUrl = disjob.baseurl + "service/job/info/jobDetailInfo?method=addNew";
	disjob.jobGroupPageUrl = disjob.baseurl + "service/job/bind/getJobGroupList";
	
	toastr.options = {/* 定义操作结果弹出框的属性 */
		"closeButton" : true,
		"debug" : false,
		"progressBar" : false,
		"positionClass" : "toast-bottom-full-width",
		"showDuration" : "400",
		"hideDuration" : "1000",
		"timeOut" : "3000",
		"extendedTimeOut" : "1000",
		"showEasing" : "swing",
		"hideEasing" : "linear",
		"showMethod" : "fadeIn",
		"hideMethod" : "fadeOut"
	};
	
	disjob.ajax = function(requrl,data,callback){
		$.ajax({
			url : disjob.baseurl + requrl,
			type : "POST",
			dataType : "json",
			data : data,
			success : function(data) {
				callback(data);
			}
		})
	};
	
	disjob.post = function(requrl,data,callback){
		$.ajax({
			url : disjob.baseurl + requrl,
			type : "POST",
			dataType : "json",
			data : data,
			success : function(data) {
				callback(data);
			}
		})
	};
	
	disjob.selector = function(requrl, data, $ele){
		disjob.ajax(requrl, data ,function(data){
			var selectorHtml = "";
			for (var i = 0, size = data.length; i < size; i++) {
				selectorHtml += '<option value="' + data[i] + '">'
				+ data[i] + '</option>';
			}
			$ele.append(selectorHtml);
		});
	};
	
	disjob.selector = function(requrl, data, $ele, selectedValue){
		disjob.ajax(requrl, data ,function(data){
			var selectorHtml = "";
			if(!selectedValue){
				selectorHtml += '<option value="" >请选择</option>';
			}
			for (var i = 0, size = data.length; i < size; i++) {
				if(data[i] == selectedValue){
					selectorHtml += '<option value="' + data[i] + '" selected="selected" >'
					+ data[i] + '</option>';					
				}else{
					selectorHtml += '<option value="' + data[i] + '">'
					+ data[i] + '</option>';
				}
			}
			$ele.append(selectorHtml);
		});
	};
	
	disjob.multiSelectValue = function($ele, data){
		$.each(data, function(i, v){			
			$('option[value="' + v + '"]', $ele).prop('selected', true);
		});
	};
	
	disjob.getUrlParam = function(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
        var r = window.location.search.substr(1).match(reg);  //匹配目标参数
        if (r != null) return unescape(r[2]); return null; //返回参数值
    };
	
    disjob.toastr = function(result, successCallback, failCallback){
    	if (result.successful) {/* 是否成功 */
        	toastr["info"]("操作成功","操作结果：");/* 显示结果 */
        	successCallback();
        } else {
        	toastr["info"](result.msg,"操作结果：");/* 显示结果 */
        	failCallback();
        }
    }
    
	$.fn.disjobSerialize = function()
	{
	   var o = {};
	   var a = this.serializeArray();
	   $.each(a, function() {
	       if (o[this.name]) {
	           if (!o[this.name].push) {
	               o[this.name] = [o[this.name]];
	           }
	           o[this.name].push(this.value || '');
	       } else {
	           o[this.name] = this.value || '';
	       }
	   });
	   return o;
	};
	
	$.fn.serializeCron = function()
	{
	   var o = {
			   mins : [],
			   seconds : [],
			   hours : [],
			   days : [],
			   months : [],
			   weekdays : []
	   };
	   var a = this.serializeArray();
	   $.each(a, function() {
	       if (o[this.name]) {
	           if (!o[this.name].push) {
	               o[this.name] = [o[this.name]];
	           }
	           o[this.name].push(this.value || '');
	       } else {
	           o[this.name] = this.value || '';
	       }
	   });
	   return o;
	};
	
})(window, jQuery);


/**
 * jQuery JSON plugin v2.6.0
 * https://github.com/Krinkle/jquery-json
 *
 * @author Brantley Harris, 2009-2011
 * @author Timo Tijhof, 2011-2016
 * @source This plugin is heavily influenced by MochiKit's serializeJSON, which is
 *         copyrighted 2005 by Bob Ippolito.
 * @source Brantley Harris wrote this plugin. It is based somewhat on the JSON.org
 *         website's http://www.json.org/json2.js, which proclaims:
 *         "NO WARRANTY EXPRESSED OR IMPLIED. USE AT YOUR OWN RISK.", a sentiment that
 *         I uphold.
 * @license MIT License <https://opensource.org/licenses/MIT>
 */
(function (factory) {
	if (typeof define === 'function' && define.amd) {
		// AMD
		define(['jquery'], factory);
	} else if (typeof exports === 'object') {
		// CommonJS
		factory(require('jquery'));
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function ($) {
	'use strict';

	var escape = /["\\\x00-\x1f\x7f-\x9f]/g,
		meta = {
			'\b': '\\b',
			'\t': '\\t',
			'\n': '\\n',
			'\f': '\\f',
			'\r': '\\r',
			'"': '\\"',
			'\\': '\\\\'
		},
		hasOwn = Object.prototype.hasOwnProperty;

	/**
	 * jQuery.toJSON
	 * Converts the given argument into a JSON representation.
	 *
	 * @param o {Mixed} The json-serializable *thing* to be converted
	 *
	 * If an object has a toJSON prototype, that will be used to get the representation.
	 * Non-integer/string keys are skipped in the object, as are keys that point to a
	 * function.
	 *
	 */
	$.toJSON = typeof JSON === 'object' && JSON.stringify ? JSON.stringify : function (o) {
		if (o === null) {
			return 'null';
		}

		var pairs, k, name, val,
			type = $.type(o);

		if (type === 'undefined') {
			return undefined;
		}

		// Also covers instantiated Number and Boolean objects,
		// which are typeof 'object' but thanks to $.type, we
		// catch them here. I don't know whether it is right
		// or wrong that instantiated primitives are not
		// exported to JSON as an {"object":..}.
		// We choose this path because that's what the browsers did.
		if (type === 'number' || type === 'boolean') {
			return String(o);
		}
		if (type === 'string') {
			return $.quoteString(o);
		}
		if (typeof o.toJSON === 'function') {
			return $.toJSON(o.toJSON());
		}
		if (type === 'date') {
			var month = o.getUTCMonth() + 1,
				day = o.getUTCDate(),
				year = o.getUTCFullYear(),
				hours = o.getUTCHours(),
				minutes = o.getUTCMinutes(),
				seconds = o.getUTCSeconds(),
				milli = o.getUTCMilliseconds();

			if (month < 10) {
				month = '0' + month;
			}
			if (day < 10) {
				day = '0' + day;
			}
			if (hours < 10) {
				hours = '0' + hours;
			}
			if (minutes < 10) {
				minutes = '0' + minutes;
			}
			if (seconds < 10) {
				seconds = '0' + seconds;
			}
			if (milli < 100) {
				milli = '0' + milli;
			}
			if (milli < 10) {
				milli = '0' + milli;
			}
			return '"' + year + '-' + month + '-' + day + 'T' +
				hours + ':' + minutes + ':' + seconds +
				'.' + milli + 'Z"';
		}

		pairs = [];

		if ($.isArray(o)) {
			for (k = 0; k < o.length; k++) {
				pairs.push($.toJSON(o[k]) || 'null');
			}
			return '[' + pairs.join(',') + ']';
		}

		// Any other object (plain object, RegExp, ..)
		// Need to do typeof instead of $.type, because we also
		// want to catch non-plain objects.
		if (typeof o === 'object') {
			for (k in o) {
				// Only include own properties,
				// Filter out inherited prototypes
				if (hasOwn.call(o, k)) {
					// Keys must be numerical or string. Skip others
					type = typeof k;
					if (type === 'number') {
						name = '"' + k + '"';
					} else if (type === 'string') {
						name = $.quoteString(k);
					} else {
						continue;
					}
					type = typeof o[k];

					// Invalid values like these return undefined
					// from toJSON, however those object members
					// shouldn't be included in the JSON string at all.
					if (type !== 'function' && type !== 'undefined') {
						val = $.toJSON(o[k]);
						pairs.push(name + ':' + val);
					}
				}
			}
			return '{' + pairs.join(',') + '}';
		}
	};

	/**
	 * jQuery.evalJSON
	 * Evaluates a given json string.
	 *
	 * @param str {String}
	 */
	$.evalJSON = typeof JSON === 'object' && JSON.parse ? JSON.parse : function (str) {
		/*jshint evil: true */
		return eval('(' + str + ')');
	};

	/**
	 * jQuery.secureEvalJSON
	 * Evals JSON in a way that is *more* secure.
	 *
	 * @param str {String}
	 */
	$.secureEvalJSON = typeof JSON === 'object' && JSON.parse ? JSON.parse : function (str) {
		var filtered =
			str
			.replace(/\\["\\\/bfnrtu]/g, '@')
			.replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']')
			.replace(/(?:^|:|,)(?:\s*\[)+/g, '');

		if (/^[\],:{}\s]*$/.test(filtered)) {
			/*jshint evil: true */
			return eval('(' + str + ')');
		}
		throw new SyntaxError('Error parsing JSON, source is not valid.');
	};

	/**
	 * jQuery.quoteString
	 * Returns a string-repr of a string, escaping quotes intelligently.
	 * Mostly a support function for toJSON.
	 * Examples:
	 * >>> jQuery.quoteString('apple')
	 * "apple"
	 *
	 * >>> jQuery.quoteString('"Where are we going?", she asked.')
	 * "\"Where are we going?\", she asked."
	 */
	$.quoteString = function (str) {
		if (str.match(escape)) {
			return '"' + str.replace(escape, function (a) {
				var c = meta[a];
				if (typeof c === 'string') {
					return c;
				}
				c = a.charCodeAt();
				return '\\u00' + Math.floor(c / 16).toString(16) + (c % 16).toString(16);
			}) + '"';
		}
		return '"' + str + '"';
	};

}));
//------------------------贴过来的$.toJson-----------------------------