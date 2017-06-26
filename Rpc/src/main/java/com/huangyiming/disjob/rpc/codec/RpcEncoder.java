package com.huangyiming.disjob.rpc.codec;

import com.huangyiming.disjob.common.util.DateUtil;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.common.util.SerializeUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoder extends MessageToByteEncoder<RpcRequest> {

	private Class<RpcRequest> genericClass;

    public RpcEncoder(Class<RpcRequest> genericClass) {
        this.genericClass = genericClass;
    }
    
	@Override
	protected void encode(ChannelHandlerContext ctx, RpcRequest request, ByteBuf out) throws Exception {
		 if (genericClass.isInstance(request)) {
			 Header header =  request.getHeader();
			 RpcRequestData data = request.getData();
			 //消息类型
	         out.writeByte(header.getType());
	         //协议版本
	         out.writeInt(header.getVersion());
	         //封装数据
	         if(data != null){
	        	 LoggerUtil.debug("encode data:"+data.getRequestId()+","+request.toString());
	        	 byte[] dataByte = SerializeUtil.serialize(data);
	        	 out.writeInt(dataByte.length);
		         out.writeBytes(dataByte);
		         LoggerUtil.debug("[ ENCODER ] request id:"+request.getData().getRequestId()+"; send rpc at time:"+DateUtil.getFormatNow());
	         }else{
	        	 out.writeInt(0);
	         }
	     }
	}

}
