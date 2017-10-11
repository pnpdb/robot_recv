package com.suspnp.robot;

import com.suspnp.robot.coder.ImagePackDecoder;
import com.suspnp.robot.coder.ImagePackEncoder;
import com.suspnp.robot.handler.ServerHandler;
import com.suspnp.robot.utils.ConstantValue;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created by lianhai on 2017-09-11.
 */
public class ImageServer {

    public static void main(String[] args) throws Exception {
        //logger.info(Charset.defaultCharset());
        new ImageServer().bind(ConstantValue.SERVER_PORT);
    }

    private void bind(int port) throws Exception {
        // 配置NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 服务器辅助启动类配置
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChildChannelHandler())
                    .option(ChannelOption.SO_BACKLOG, 1024)  // 设置tcp缓冲区大小
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            // 绑定端口,同步等待绑定成功
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            // 等到服务端监听端口关闭
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 释放线程资源
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline p = ch.pipeline();
            // 启用客户端服务器双向读写心跳
//            p.addLast(new IdleStateHandler(10, 0, 0));
            // 添加自定义协议的编码解码器
            p.addLast(new ImagePackEncoder());
            p.addLast(new ImagePackDecoder());
            // 处理网络IO
            p.addLast(new ServerHandler());
        }
    }

}