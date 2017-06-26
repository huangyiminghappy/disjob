package com.huangyiming.disjob.register.center;

import org.springframework.stereotype.Service;

@Service
public class JobZKRegistry extends AbstractZKRegistryCenter{
	
	public JobZKRegistry() {
	}

	@Override
	protected boolean initRootNode() {
		return false;
	}
}
