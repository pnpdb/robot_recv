package com.suspnp.robot.handler;

import com.suspnp.robot.protocol.ImageTransferProtocol;
import com.suspnp.robot.utils.ConstantValue;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.log4j.Logger;

/**
 * Created by lianhai on 2017-09-11.
 */
public abstract class HeartbeatHandler extends SimpleChannelInboundHandler {

    private static Logger logger = Logger.getLogger(HeartbeatHandler.class);

    protected String name;
    //心跳次数
    private int heartbeatCount = 0;

    protected HeartbeatHandler(String name) {
        this.name = name;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, Object obj) throws Exception {
        ImageTransferProtocol body = (ImageTransferProtocol) obj;
        if (body.getPackType() == ConstantValue.PING_MSG) {
            sendPongMsg(context);
        } else if (body.getPackType() == ConstantValue.PONG_MSG) {
            logger.info(name + " get pong msg from " + context.channel().remoteAddress());
        } else {
            handleData(context, body);
        }
    }

    protected void sendPingMsg(ChannelHandlerContext context) {
        ImageTransferProtocol body = new ImageTransferProtocol(ConstantValue.PING_MSG);
        context.writeAndFlush(body);
        heartbeatCount++;
        logger.info(name + " sent ping msg to " + context.channel().remoteAddress() + ", count: " +
                heartbeatCount);
    }

    private void sendPongMsg(ChannelHandlerContext context) {
        ImageTransferProtocol body = new ImageTransferProtocol(ConstantValue.PONG_MSG);
        context.channel().writeAndFlush(body);
        heartbeatCount++;
        logger.info(name + " sent pong msg to " + context.channel().remoteAddress() + ", count: " +
                heartbeatCount);
    }

    protected abstract void handleData(ChannelHandlerContext channelHandlerContext, ImageTransferProtocol body);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // IdleStateHandler 所产生的 IdleStateEvent 的处理逻辑.
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE:
                    handleWriterIdle(ctx);
                    break;
                case ALL_IDLE:
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        }
    }

    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        logger.info("---READER_IDLE---");
    }

    private void handleWriterIdle(ChannelHandlerContext ctx) {
        logger.info("---WRITER_IDLE---");
        sendPingMsg(ctx);
    }

    protected void handleAllIdle(ChannelHandlerContext ctx) {
        logger.info("---ALL_IDLE---");
    }

}
