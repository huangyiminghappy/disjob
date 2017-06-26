package com.huangyiming.disjob.register.job;

/**
 * 后台分配job状态类
 * @author Disjob
 *
 */
public enum JobOperatorStatus {

	  WARN("可用集群节点少于2台", 1), SUCCESS("分配成功", 0);
      // 成员变量
      private String message;
      private int value;

      // 构造方法
      private JobOperatorStatus(String message, int value) {
          this.message = message;
          this.value = value;
      }

      
 
	public String getMessage() {
		return message;
	}



	public void setMessage(String message) {
		this.message = message;
	}



	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
      
      
}
