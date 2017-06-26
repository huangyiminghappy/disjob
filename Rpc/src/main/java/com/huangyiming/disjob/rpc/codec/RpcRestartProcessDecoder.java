package com.huangyiming.disjob.rpc.codec;

import java.nio.ByteOrder;
import java.util.HashMap;

import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.common.util.SerializeUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 重启子进程返回值解码
 * @author Disjob
 *
 */
public class RpcRestartProcessDecoder extends LengthFieldBasedFrameDecoder{
	
	public RpcRestartProcessDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
	}

	
	public RpcRestartProcessDecoder(ByteOrder byteOrder, int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
			int lengthAdjustment, int initialBytesToStrip, boolean failFast) {
		super(byteOrder, maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast);
		
	}


	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		in = (ByteBuf)super.decode(ctx, in);
		if (in == null) {
	        return null;
	    }
		
		if (in.readableBytes() < 8) {
            LoggerUtil.warn("when server response, readableBytes less than header length!");
        }
        
		byte type = in.readByte();
		if(type == 0){
			return null;
		}
        int length = in.readInt();
        if (in.readableBytes() < length) {
            LoggerUtil.warn("when server response, the length of header is " + length + " but the readableBytes is less!");
        }
        ByteBuf buf = in.readBytes(length);
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        HashMap response = (HashMap) SerializeUtil.deserialize(req, HashMap.class);
        return response;
	        
	}

	
}
