package com.huangyiming.disjob.java.dynamic;

import com.huangyiming.disjob.java.ProviderClassName;
import com.huangyiming.disjob.quence.Action;
import com.huangyiming.disjob.quence.ActionQueue;
import com.huangyiming.disjob.quence.TaskExecuteException;

public abstract class FileHandler extends Action implements ProviderClassName{

	protected String fileName ;
	protected String dir ;
	public FileHandler(String dir ,String filePath){
		this.dir = dir;
		this.fileName = filePath;
	}
	
	public FileHandler(ActionQueue queue) {
		super(queue);
	}

	@Override
	public void execute() throws TaskExecuteException {
		execute(dir,fileName);
	}
	
	public String getClassName() {
		
		return this.getClass().getName();
	}
	
	
	public abstract void execute(String dir,String fileName) throws TaskExecuteException ;
}
