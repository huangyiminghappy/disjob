package com.huangyiming.disjob.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteOrder;

import com.huangyiming.disjob.common.util.DateUtil;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.common.util.SerializeUtil;

/**
 * 
 * @author Disjob
 *
 */
public class RpcDecoder extends LengthFieldBasedFrameDecoder{
	
	public RpcDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
	}

	
	public RpcDecoder(ByteOrder byteOrder, int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
			int lengthAdjustment, int initialBytesToStrip, boolean failFast) {
		super(byteOrder, maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast);
		
	}


	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		try {
			in = (ByteBuf)super.decode(ctx, in);
			if (in == null) {
			    return null;
			}
			
			if (in.readableBytes() < 9) {
				System.err.println("when server response, readableBytes less than header length!");
			    LoggerUtil.warn("when server response, readableBytes less than header length!");
			    return null ;
			}
			byte type = in.readByte();
			if(type == 0){
				LoggerUtil.info("heratbeat response from RPC server!");
				return null;
			}
			int version = in.readInt();
			int length = in.readInt();
			if (in.readableBytes() < length) {
			    LoggerUtil.warn("when server response, the length of header is " + length + " but the readableBytes is less!");
			}
			ByteBuf buf = in.readBytes(length);
			byte[] req = new byte[buf.readableBytes()];
			buf.readBytes(req);
			RpcResponse response = (RpcResponse) SerializeUtil.deserialize(req, RpcResponse.class);
			response.setVersion(version);
			response.setLength(length);
	        LoggerUtil.debug("[ DECODER ] request id:"+response.getRequestId()+"; receive rpc at time:"+DateUtil.getFormatNow());
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.error(e.getMessage());
		}
	    return null; 
	}

	
}
