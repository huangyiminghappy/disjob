package com.huangyiming.disjob.register.auth.service;

import java.util.List;

public interface AuthService {

	void assign(String username, String group, String type);

	void unAssign(String username, String group, String type);
	
	List<String> getAuthAvailableJobGroup();

	boolean[] getAuthByUsernameAndJobgroup(String username, String jobgroup);
}