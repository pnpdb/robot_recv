package com.suspnp.robot.handler;

import com.suspnp.robot.ImageClient;
import com.suspnp.robot.protocol.ImageTransferProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Created by lianhai on 2017-09-11.
 */
public class ClientHandler extends HeartbeatHandler {

    private static Logger logger = Logger.getLogger(ClientHandler.class);

    private ImageClient client;

    public ClientHandler(ImageClient client) {
        super("Client");
        this.client = client;
    }


    @Override
    protected void handleData(ChannelHandlerContext channelHandlerContext, ImageTransferProtocol body) {
        logger.info(name + " get package: " + body.toString());
    }

    @Override
    protected void handleAllIdle(ChannelHandlerContext ctx) {
        super.handleAllIdle(ctx);
        sendPingMsg(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        if (!channel.isActive())
            logger.error(name + "exception, close the channel");
        cause.printStackTrace();
        ctx.close();
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logger.info("Connect inactive");
        final EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(connectTask, 10, TimeUnit.SECONDS);
    }

    private Runnable connectTask = new Runnable() {
        public void run() {
            try {
                client.doConnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
