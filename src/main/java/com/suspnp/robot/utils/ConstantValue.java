package com.suspnp.robot.utils;

/**
 * Created by lianhai on 2017-09-11.
 */
public class ConstantValue {

    public static final int PACK_HEAD = 0X76;
    public static final String SERVER_HOST = "127.0.0.1";
    public static final int SERVER_PORT = 9321;

    // 包类型：心跳、图片
    public static final int PING_MSG = 1;
    public static final int PONG_MSG = 2;
    public static final int CUSTOM_MSG = 3;

    // 图片服务器路径
    public static final String STATIC_SERVER_PATH = "/Users/leonard/Desktop/";
    // 测试图片路径
    public static final String TEST_IMAGE_PATH = "/Users/leonard/Desktop/netty_image.jpeg";
}
