package com.huangyiming.disjob.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.IOException;
import java.nio.ByteOrder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.common.util.SerializeUtil;
import com.huangyiming.disjob.rpc.utils.PhpTaskCmd;

 /**
  * 处理disJob的rpc解码类
  * @author Disjob
  *
  */
public class DisJobTaskDecoder extends LengthFieldBasedFrameDecoder{
	public   PhpTaskCmd cmd;
	public DisJobTaskDecoder(PhpTaskCmd cmd,int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
		this.cmd = cmd;
	}

	
	public DisJobTaskDecoder(ByteOrder byteOrder, int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
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
        DisJobResponse response = bytes2Response(req);
        return response;
	        
	}


	private DisJobResponse bytes2Response(byte[] req) throws JsonParseException,
			JsonMappingException, IOException {
		DisJobResponse response = null;
        //kill task  后面第二版返回值要改成15
        switch (cmd.getType()) {
		case 15:
			response = (DisJobKillTaskResponse) SerializeUtil.deserialize(req, DisJobKillTaskResponse.class);
			break;
		case 14:
			response = (DisJobRestartTaskResponse) SerializeUtil.deserialize(req, DisJobRestartTaskResponse.class);
		default:
			break;
		}
		return response;
	}

	
}
