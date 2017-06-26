package com.huangyiming.disjob.rpc.utils;

/**
 * php任务类型
 * @author Disjob
 *
 */
public enum PhpTaskCmd {

	   KILLTASK("kill任务", 15), RESTART("重启进程", 14); 
	    // 成员变量  
	    private String name;  
	    private int type;  
	    // 构造方法  
	    private PhpTaskCmd(String name, int type) {  
	        this.name = name;  
	        this.type = type;  
	    }  
	    // 普通方法  
	    public static String getName(int type) {  
	        for (PhpTaskCmd c : PhpTaskCmd.values()) {  
	            if (c.getType() == type) {  
	                return c.name;  
	            }  
	        }  
	        return null;  
	    }  
	    // get set 方法  
	    public String getName() {  
	        return name;  
	    }  
	    public void setName(String name) {  
	        this.name = name;  
	    }
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}  
	    
	
	
}
