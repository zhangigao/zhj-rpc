package org.zhj.transmission.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.zhj.compress.impl.GzipCompress;
import org.zhj.constant.RpcConstant;
import org.zhj.dto.RpcMsg;
import org.zhj.dto.RpcReq;
import org.zhj.dto.RpcResp;
import org.zhj.enums.CompressType;
import org.zhj.enums.MsgType;
import org.zhj.enums.SerializeType;
import org.zhj.enums.VersionType;
import org.zhj.exception.RpcException;
import org.zhj.factory.SingletonFactory;
import org.zhj.serialize.impl.KryoSerializer;

import java.util.Arrays;

public class NettyRpcDecoder extends LengthFieldBasedFrameDecoder {

    public NettyRpcDecoder() {
        super(RpcConstant.REQ_MAX_LEN, 5, 4, -9, 0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf byteBuf = (ByteBuf) super.decode(ctx, in);
        if (byteBuf == null) {
            return null;
        }
        return decode(byteBuf);
    }

    private Object decode(ByteBuf byteBuf) throws Exception {
        readAndCheckMagic(byteBuf);
        byte versionCode = byteBuf.readByte();
        int msgLength = byteBuf.readInt();
        byte mesCode = byteBuf.readByte();
        byte serializerCode = byteBuf.readByte();
        byte compressCode = byteBuf.readByte();
        int reqId = byteBuf.readInt();

        VersionType versionType = VersionType.from(versionCode);
        SerializeType serializeType = SerializeType.from(serializerCode);
        CompressType compressType = CompressType.from(compressCode);
        MsgType msgType = MsgType.from(mesCode);
        Object data = readData(byteBuf, msgLength - RpcConstant.REQ_HEAD_LEN, msgType);

        return RpcMsg.builder()
                .reqId(reqId)
                .versionType(versionType)
                .serializeType(serializeType)
                .compressType(compressType)
                .msgType(msgType)
                .data(data)
                .build();
    }

    private void readAndCheckMagic(ByteBuf byteBuf) {
        byte[] magicCode = new byte[RpcConstant.RPC_MAGIC_CODE.length];
        byteBuf.readBytes(magicCode);
        if (!Arrays.equals(RpcConstant.RPC_MAGIC_CODE, magicCode)) {
            throw new RpcException("魔术值异常:" + new String(magicCode));
        }
    }

    private Object readData(ByteBuf byteBuf, int dataLen, MsgType msgType) throws Exception {
        if (msgType.isReq()) {
            return readData(byteBuf, dataLen, RpcReq.class);
        } else {
            return readData(byteBuf, dataLen, RpcResp.class);
        }
    }

    private <T> T readData(ByteBuf byteBuf, int dataLen, Class<T> clazz) throws Exception {
        if (dataLen <= 0) {
            return null;
        }
        byte[] data = new byte[dataLen];
        byteBuf.readBytes(data);
        GzipCompress gzipCompress = SingletonFactory.getInstance(GzipCompress.class);
        byte[] decompress = gzipCompress.decompress(data);
        KryoSerializer kryoSerializer = SingletonFactory.getInstance(KryoSerializer.class);
        return kryoSerializer.deserialize(decompress, clazz);
    }
}
