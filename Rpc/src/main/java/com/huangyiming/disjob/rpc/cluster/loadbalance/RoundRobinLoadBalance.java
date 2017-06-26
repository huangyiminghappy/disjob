package com.huangyiming.disjob.rpc.cluster.loadbalance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.huangyiming.disjob.rpc.client.HURL;
import com.huangyiming.disjob.rpc.cluster.LoadBalance;


/**
 * 
 * @author Disjob
 *
 */
public class RoundRobinLoadBalance implements LoadBalance{
	
	private List<HURL> urls;

    private static AtomicInteger idx = new AtomicInteger(0);
    
    public RoundRobinLoadBalance( List<HURL> urls){
    	this.urls = urls;
    }

	@Override
	public void onRefresh(List<HURL> rpcLst) {
		
	}

	@Override
	public HURL select() {
		List<HURL> urlLst = this.urls;

		HURL url = null;
        if (urlLst.size() > 1) {
        	url = doSelect();
        } else if (urlLst.size() == 1) {
        	url = urlLst.get(0);
        }

         if (url != null) {
            return url;
        }
        //throw new RpcServiceException("No available referers for call request:jobName");
        return null;
	}
	
	protected HURL doSelect() {
        int index = idx.incrementAndGet();
        for (int i = 0; i < urls.size(); i++) {
            HURL url = urls.get((i + index) % urls.size());
            if(url.getHost() != null && url.getPhpFilePath() != null){
            	return url;
            }
        }
        return null;
    }

	@Override
	public void setWeightString(String weightString) {
		
	}
}
