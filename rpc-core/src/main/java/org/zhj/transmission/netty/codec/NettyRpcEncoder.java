package org.zhj.transmission.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.zhj.compress.impl.GzipCompress;
import org.zhj.constant.RpcConstant;
import org.zhj.dto.RpcMsg;
import org.zhj.factory.SingletonFactory;
import org.zhj.serialize.impl.KryoSerializer;

import java.util.concurrent.atomic.AtomicInteger;

public class NettyRpcEncoder extends MessageToByteEncoder<RpcMsg> {

    private static final AtomicInteger ID_GEN = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcMsg rpcMsg, ByteBuf byteBuf) throws Exception {
        byteBuf.writeBytes(RpcConstant.RPC_MAGIC_CODE);
        byteBuf.writeByte(rpcMsg.getVersionType().getCode());
        // 往右挪4位
        int lengIndex = byteBuf.writerIndex();
        byteBuf.writerIndex(byteBuf.writerIndex() + 4);
        byteBuf.writeByte(rpcMsg.getMsgType().getCode());
        byteBuf.writeByte(rpcMsg.getSerializeType().getCode());
        byteBuf.writeByte(rpcMsg.getCompressType().getCode());
        byteBuf.writeInt(ID_GEN.getAndIncrement());

        int msgLength = RpcConstant.REQ_HEAD_LEN;
        if (!rpcMsg.getMsgType().isHeartBeat() && rpcMsg.getData() != null) {
            byte[] data = data2Bytes(rpcMsg);
            byteBuf.writeBytes(data);
            msgLength += data.length;
        }
        int curIndex = byteBuf.writerIndex();
        byteBuf.writerIndex(lengIndex);
        byteBuf.writeInt(msgLength);
        byteBuf.writerIndex(curIndex);
    }

    private byte[] data2Bytes(RpcMsg msg) {
        // TODO获取序列化和数据压缩类型
        // msg.getCompressType();
        // msg.getSerializeType();
        KryoSerializer kryoSerializer = SingletonFactory.getInstance(KryoSerializer.class);
        byte[] dataBytes = kryoSerializer.serialize(msg.getData());
        GzipCompress gzipCompress = SingletonFactory.getInstance(GzipCompress.class);
        return gzipCompress.compress(dataBytes);
    }
}
