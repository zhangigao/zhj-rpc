package org.zhj.transmission.netty.server;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.zhj.dto.RpcMsg;
import org.zhj.dto.RpcReq;
import org.zhj.dto.RpcResp;
import org.zhj.enums.CompressType;
import org.zhj.enums.MsgType;
import org.zhj.enums.SerializeType;
import org.zhj.enums.VersionType;
import org.zhj.handler.RpcReqHandler;
import org.zhj.provider.ServiceProvider;

@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcMsg> {

    private final RpcReqHandler rpcReqHandler;


    public NettyServerHandler(ServiceProvider serviceProvider) {
        this.rpcReqHandler = new RpcReqHandler(serviceProvider);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcMsg rpcMsg) throws Exception {
        MsgType msgType;
        Object data;
        if (rpcMsg.getMsgType().isHeartBeat()) {
            msgType = MsgType.HEARTBEAT_RESP;
            data = null;
        } else {
            msgType = MsgType.RPC_RESP;
            RpcReq rpcReq = ((RpcReq) rpcMsg.getData());
            log.debug("接收到客户端请求：{}", rpcReq);
            data = handleRpcReq(rpcReq);
        }


        RpcMsg msg = RpcMsg.builder().reqId(rpcMsg.getReqId()).versionType(VersionType.VERSION1).msgType(msgType).serializeType(SerializeType.KRYO).compressType(CompressType.GZIP).data(data).build();
        channelHandlerContext.writeAndFlush(msg).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务端发生异常", cause);
        ctx.close();
    }

    private RpcResp<?> handleRpcReq(RpcReq rpcReq) {
        try {
            Object data = rpcReqHandler.invoke(rpcReq);
            return RpcResp.success(rpcReq.getReqId(), data);
        } catch (Exception e) {
            log.error("调用失败: {}", rpcReq.getInterfaceName() + ":" + rpcReq.getMethodName(), e);
            return RpcResp.fail(rpcReq.getReqId(), e.getMessage());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
            log.debug("长时间为收到客户端心跳，关闭channel, addr：{}", ctx.channel().remoteAddress());
            ctx.channel().close();
        }
        super.userEventTriggered(ctx, evt);
    }
}
