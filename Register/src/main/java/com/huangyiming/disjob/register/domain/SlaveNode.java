package com.huangyiming.disjob.register.domain;

  public  class SlaveNode{  
    String name="";  
    String ip;  
    public SlaveNode(String name,String ip) {  
        this.name = name;  
        this.ip = ip;  
    }  
    public SlaveNode(String ip) {  
         this.ip = ip;  
    } 
    @Override  
    public String toString() {  
        return this.name+"-"+this.ip;  
    }
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}  
    
    
}
