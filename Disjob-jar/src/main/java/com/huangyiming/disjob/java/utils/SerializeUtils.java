package com.huangyiming.disjob.java.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SerializeUtils<T> {
	public static final ObjectMapper mapper = new ObjectMapper();
		
	public static byte [] serialize(Object obj) throws JsonProcessingException{
		return mapper.writeValueAsBytes(obj);
	}
	
	public static <T> Object deserialize(byte[] byteData, Class<T> cls) throws JsonParseException, JsonMappingException, IOException{
		return mapper.readValue(byteData, cls);
	}
}
