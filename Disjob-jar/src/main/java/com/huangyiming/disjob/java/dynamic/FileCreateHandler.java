package com.huangyiming.disjob.java.dynamic;

import com.huangyiming.disjob.java.utils.Log;
import com.huangyiming.disjob.quence.TaskExecuteException;

/**
 * 
 * @author Disjob
 * 当创建一个新的文件，这文件里面没有数据的时候，只会触发这个 Handler,如果是在监听的目录里面复制一个文件。这个时候即会触发：ceater handler 也会触发 modify handler
 */
public class FileCreateHandler extends FileHandler{

	public FileCreateHandler(String dir,String filePath){
		super(dir,filePath);
	}

	@Override
	public void execute(String dir, String fileName) throws TaskExecuteException {
		
		Log.error(getClassName()+"; dir:"+dir+"; file name:"+fileName);
	}
}
