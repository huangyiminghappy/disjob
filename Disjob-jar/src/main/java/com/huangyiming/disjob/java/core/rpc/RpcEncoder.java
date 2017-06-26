package com.huangyiming.disjob.java.core.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.huangyiming.disjob.java.utils.SerializeUtils;
import com.huangyiming.disjob.java.utils.TimeUtils;
import com.huangyiming.disjob.quence.Log;

public class RpcEncoder extends MessageToByteEncoder<RpcResponse> {

	private Class<RpcResponse> genericClass;

    public RpcEncoder(Class<RpcResponse> genericClass) {
        this.genericClass = genericClass;
    }
    
	@Override
	protected void encode(ChannelHandlerContext ctx, RpcResponse response, ByteBuf out) throws Exception {
		 if (genericClass.isInstance(response)) {
			out.writeByte(1);
			out.writeInt(1);
			if(response !=null){
				byte[] bytes = SerializeUtils.serialize(response);
				out.writeInt(bytes.length);
				out.writeBytes(bytes);
			}else{
				out.writeInt(0);
			}
			Log.debug("[ ENCODER ] request id:"+response.getRequestId()+"; send at time:"+TimeUtils.getFormatNow());
	     }else{
	    	 Log.error("消息类型 不匹配.");
	     }
	}

}
