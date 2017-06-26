package com.huangyiming.disjob.rpc.utils;

import com.huangyiming.disjob.common.util.SpringWorkFactory;
import com.huangyiming.disjob.rpc.support.StoreRepThreadPoolService;

public class RpcSpringWorkFactory extends SpringWorkFactory{

	public static StoreRepThreadPoolService getStoreRepThreadPoolService(){
		
		return (StoreRepThreadPoolService) getWorkObject("storeRepThreadPoolService");
	}
	
}
