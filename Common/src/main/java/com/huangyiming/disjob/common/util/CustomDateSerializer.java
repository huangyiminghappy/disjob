package com.huangyiming.disjob.common.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * <pre>
 * 
 *  File: CustomDateSerializer.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  自定义日期格式化工具类
 * 
 *  Revision History
 *
 *  Date：		2016年6月10日
 *  Author：		Disjob
 *
 * </pre>
 */
public class CustomDateSerializer extends JsonSerializer<Date> {
	@Override
	public void serialize(Date value, JsonGenerator jgen,SerializerProvider provider)
			throws IOException,JsonProcessingException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattedDate = formatter.format(value);
		jgen.writeString(formattedDate);
	}
}