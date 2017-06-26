package com.huangyiming.disjob.rpc.cluster;

import java.util.List;

import com.huangyiming.disjob.rpc.client.HURL;


/**
 * 
 * @author Disjob
 *
 * @param
 */
public interface LoadBalance{

    void onRefresh(List<HURL> rpcLst);

    HURL select();

    void setWeightString(String weightString);

}
