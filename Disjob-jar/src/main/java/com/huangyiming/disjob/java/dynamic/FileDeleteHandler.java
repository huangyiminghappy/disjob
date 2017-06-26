package com.huangyiming.disjob.java.dynamic;

import com.huangyiming.disjob.java.utils.Log;
import com.huangyiming.disjob.quence.TaskExecuteException;

public class FileDeleteHandler extends FileHandler{

	public FileDeleteHandler(String dir,String filePath){
		super(dir,filePath);
	}
	
	@Override
	public void execute(String dir, String fileName) throws TaskExecuteException {
		
		Log.error(getClassName()+"; dir:"+dir+"; file name:"+fileName);
	}
}
