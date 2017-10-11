package com.suspnp.robot;

import com.suspnp.robot.coder.ImagePackDecoder;
import com.suspnp.robot.coder.ImagePackEncoder;
import com.suspnp.robot.handler.ClientHandler;
import com.suspnp.robot.protocol.ImageTransferProtocol;
import com.suspnp.robot.utils.ConstantValue;
import com.suspnp.robot.utils.ImageUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created by lianhai on 2017-09-11.
 */
public class ImageClient {

    private static Logger logger = Logger.getLogger(ImageClient.class);

    private NioEventLoopGroup workGroup = new NioEventLoopGroup(1);
    private Channel channel;
    private Bootstrap bootstrap;

    public static void main(String[] args) {
        ImageClient client = new ImageClient();
        client.start();
//        client.sendData();
    }

    /**
     * 模拟客户端发送一张图片
     */
    private void sendData() {
        //文件名:车号+日期+故障类型+故障编号
        String fileName = "060056_2017.09.011_01_05.jpg";
        byte[] fileNameBytes = fileName.getBytes();
        byte[] contentBytes = ImageUtils.image2Bytes(ConstantValue.TEST_IMAGE_PATH);
        if (contentBytes != null) {
            ImageTransferProtocol pack = new ImageTransferProtocol(1 + 1 + fileNameBytes.length + contentBytes
                    .length, fileNameBytes, contentBytes);
            channel.writeAndFlush(pack);
            logger.info("Client write a image to server");
        }
    }

    /**
     * 根据图片路径发送图片
     *
     * @param imagePath
     */
    public void sendData(String imagePath) {
        File file = new File(imagePath);
        String fileName = file.getName();
        byte[] fileNameBytes = fileName.getBytes();
        byte[] contentBytes = ImageUtils.image2Bytes(imagePath);
        if (contentBytes != null) {
            ImageTransferProtocol pack = new ImageTransferProtocol(1 + 1 + fileNameBytes.length + contentBytes
                    .length, fileNameBytes, contentBytes);
            channel.writeAndFlush(pack);
            logger.info("Client write a image to server");
        }
    }

    private void start() {
        try {
            bootstrap = new Bootstrap();
            bootstrap
                    .group(workGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new MyChannelHandler());
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.option(ChannelOption.SO_TIMEOUT, 3000);
            doConnect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void doConnect() throws Exception {
        if (channel != null && channel.isActive()) {
            return;
        }
        ChannelFuture future = bootstrap.connect(ConstantValue.SERVER_HOST, ConstantValue.SERVER_PORT);//.sync()
        future.addListener(new MyChannelFutureListener());
    }

    private class MyChannelFutureListener implements ChannelFutureListener {

        public void operationComplete(ChannelFuture futureListener) throws Exception {
            if (futureListener.isSuccess()) {
                channel = futureListener.channel();
                logger.info("Connect to server successfully");
            } else {
                logger.info("Failed to connect to server, try connect after 10s");
                futureListener.channel().eventLoop().schedule(connectTask, 3, TimeUnit.SECONDS);
            }
        }
    }

    /**
     * 失败重连任务
     */
    private Runnable connectTask = new Runnable() {
        public void run() {
            try {
                doConnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 网络事件处理器
     */
    private class MyChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline p = ch.pipeline();
            //10s没有写事件则触发UserEventTriggered
            p.addLast(new IdleStateHandler(0, 10, 0));
            // 添加自定义协议的编码解码器
            p.addLast(new ImagePackEncoder());
            p.addLast(new ImagePackDecoder());
            // 处理网络IO
            p.addLast(new ClientHandler(ImageClient.this));
        }
    }

}
