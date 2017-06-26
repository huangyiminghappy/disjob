package com.huangyiming.disjob.rpc.client.proxy;

import java.net.InetSocketAddress;

import com.huangyiming.disjob.common.exception.TransportException;
import com.huangyiming.disjob.rpc.client.HURL;
import com.huangyiming.disjob.rpc.codec.Response;
import com.huangyiming.disjob.rpc.codec.RpcRequest;

/**
 * 
 * @author Disjob
 *
 */
public interface Channel {

    /**
     * get local socket address.
     * 
     * @return local address.
     */
    InetSocketAddress getLocalAddress();

    /**
     * get remote socket address
     * 
     * @return
     */
    InetSocketAddress getRemoteAddress();

    /**
     * send request.
     *
     * @param request
     * @return response future
     * @throws TransportException
     */
    Response request(RpcRequest request) throws TransportException;

    /**
     * open the channel
     * 
     * @return
     */
    io.netty.channel.Channel connect(int reConCount) throws InterruptedException;

    /**
     * close the channel.
     */
    void close();

    /**
     * close the channel gracefully.
     */
    void close(int timeout);

    /**
     * is closed.
     * 
     * @return closed
     */
    boolean isClosed();

    /**
     * the node available status
     * 
     * @return
     */
    boolean isAvailable();

    /**
     * 
     * @return
     */
    public HURL getHurl();
    
    public io.netty.channel.Channel getChannel();

}
