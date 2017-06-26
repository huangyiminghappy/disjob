package com.huangyiming.disjob.java.core.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteOrder;

import com.huangyiming.disjob.java.utils.SerializeUtils;
import com.huangyiming.disjob.java.utils.TimeUtils;
import com.huangyiming.disjob.quence.Log;

/**
 * 
 * @author Disjob
 *
 */
public class RpcDecoder extends LengthFieldBasedFrameDecoder {

	public RpcDecoder(int maxFrameLength, int lengthFieldOffset,
			int lengthFieldLength) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
	}

	public RpcDecoder(ByteOrder byteOrder, int maxFrameLength,
			int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment,
			int initialBytesToStrip, boolean failFast) {
		super(byteOrder, maxFrameLength, lengthFieldOffset, lengthFieldLength,
				lengthAdjustment, initialBytesToStrip, failFast);

	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in)
			throws Exception {
		in = (ByteBuf) super.decode(ctx, in);
		if (in == null) {
			return null;
		}

		if (in.readableBytes() < 9) {
			Log.error("when server response, readableBytes less than header length!");
			return null;
		}

		byte type = in.readByte();
		int version = in.readInt();
		int length = in.readInt();
		if (in.readableBytes() < length) {
			Log.error("when server response, the length of header is "+ length + " but the readableBytes is less!");
			return null;
		}
		RpcRequest request = new RpcRequest();
		Header header = new Header(type, version, length);
		request.setHeader(header);
		if(type!=0){//不是心跳包，则有消息内容体，解析消息内容
			ByteBuf buf = in.readBytes(length);
			byte[] req = new byte[buf.readableBytes()];
			buf.readBytes(req);
			RpcRequestData requestData = (RpcRequestData) SerializeUtils.deserialize(req, RpcRequestData.class);
			request.setData(requestData);
			Log.debug("[ DECODER ] request id:"+request.getData().getRequestId()+"; receive time:"+TimeUtils.getFormatNow());
		}
		return request;
	}

}
