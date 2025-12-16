package org.zhj.transmission.netty.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class NettyRpcDecoder extends LengthFieldBasedFrameDecoder {

    public NettyRpcDecoder() {
        this(1024, 0, 4);
    }

    public NettyRpcDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }
}
