package com.xingyeda.ad.service.socket;

public class CommandReceiveConfig {
    /**
     * 端口
     */
    public static final int SOCKET_PORT = 5888;
    /**
     * 连接超时时间
     */
    public static final int CONNECTION_TIMEOUT_MILL = 5000;

    /**
     * 数据读取超时时间
     */
    public static final int READ_TIMEOUT_MILL = 10000;

    /**
     * 数据发送超时时间
     */
    public static final int SEND_TIMEOUT_MILL = 10000;

    public static final int HeartBeatInterval = 10000;
}
