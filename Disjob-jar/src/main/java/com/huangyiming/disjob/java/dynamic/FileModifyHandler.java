package com.huangyiming.disjob.java.dynamic;

import com.huangyiming.disjob.java.core.dispatcher.EventObjectDispatcher;
import com.huangyiming.disjob.java.utils.FileUtils;
import com.huangyiming.disjob.java.utils.Log;
import com.huangyiming.disjob.quence.TaskExecuteException;

public class FileModifyHandler extends FileHandler{
	public FileModifyHandler(String dir,String filePath){
		super(dir,filePath);
	}
	
	@Override
	public void execute(String dir, String fileName) throws TaskExecuteException {
		if(!FileUtils.isJarFile(fileName)){
			return ;
		}
		
		String absPath = "";
		if(dir.endsWith("/")){
			absPath = dir + fileName;
		}else{
			absPath = dir + "/" + fileName ;
		}
		EventObjectDispatcher.dispatcherDynamicJob(absPath);
		Log.debug(getClassName()+"; dir:"+dir+"; file name:"+absPath);
	}

}
