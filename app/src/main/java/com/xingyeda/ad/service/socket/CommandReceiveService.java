package com.xingyeda.ad.service.socket;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.altang.app.common.utils.GsonUtil;
import com.altang.app.common.utils.LoggerHelper;
import com.vilyever.socketclient.SocketClient;
import com.vilyever.socketclient.helper.SocketClientAddress;
import com.vilyever.socketclient.helper.SocketClientDelegate;
import com.vilyever.socketclient.helper.SocketClientReceivingDelegate;
import com.vilyever.socketclient.helper.SocketClientSendingDelegate;
import com.vilyever.socketclient.helper.SocketPacket;
import com.vilyever.socketclient.helper.SocketPacketHelper;
import com.vilyever.socketclient.helper.SocketResponsePacket;
import com.vilyever.socketclient.util.CharsetUtil;


import com.xingyeda.ad.BaseApplication;
import com.xingyeda.ad.util.Util;

import org.greenrobot.eventbus.EventBus;

public class CommandReceiveService extends Service {
    public static void startService(Context context){
        Intent intent = new Intent(context, CommandReceiveService.class);
        context.startService(intent);
    }
    private Handler mainHandler;
    private SocketClient socketClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mainHandler = new Handler();
        initSocketClient();
        initReadConfig();
        initSendConfig();
        initCustomListener();
        initSendDataListener();
        initReceiveDataListener();
        autoSetHeartbeat();
        connect();
        LoggerHelper.i("Socket长连接服务创建成功");
    }
    private String heartBeatMessage = "";
    private void refreshHeatBeatMessage(){
        CommandMessageData messageData = new CommandMessageData();
        messageData.setToken(Util.getAndroidId(this) + System.currentTimeMillis());
        messageData.setCommond("M999");
        messageData.setmId(Util.getAndroidId(this));
        messageData.setConverType("ad");
        heartBeatMessage = GsonUtil.gson.toJson(messageData);
    }
    private void initSocketClient(){
        socketClient = new SocketClient(new SocketClientAddress(BaseApplication.HOST,CommandReceiveConfig.SOCKET_PORT,CommandReceiveConfig.CONNECTION_TIMEOUT_MILL));
        /**
         * 设置自动转换String类型到byte[]类型的编码
         * 如未设置（默认为null），将不能使用{@link SocketClient#sendString(String)}发送消息
         * 如设置为非null（如UTF-8），在接受消息时会自动尝试在接收线程（非主线程）将接收的byte[]数据依照编码转换为String，在{@link SocketResponsePacket#getMessage()}读取
         */
        socketClient.setCharsetName(CharsetUtil.UTF_8);
    }

    /**
     * 建立socket连接
     */
    private void connect(){
        socketClient.connect();
    }


    private void initSendConfig(){
        /**
         * 设置包长度转换器
         * 即每次发送数据时，将包头以外的数据长度转换为特定的byte[]发送到远程端用于解析还需要读取多少长度的数据
         *
         * 例：socketClient.sendData(new byte[]{0x01, 0x02})的步骤为
         * 1. socketClient向远程端发送包头（如果设置了包头信息）
         * 2. socketClient要发送的数据为{0x01, 0x02}，长度为2（若设置了包尾，还需加上包尾的字节长度），通过此转换器将int类型的2转换为4字节的byte[]，远程端也照此算法将4字节的byte[]转换为int值
         * 3. socketClient向远程端发送转换后的长度信息byte[]
         * 4. socketClient向远程端发送正文数据{0x01, 0x02}
         * 5. socketClient向远程端发送包尾（如果设置了包尾信息）
         *
         * 此转换器用于第二步
         *
         * 使用{@link com.vilyever.socketclient.helper.SocketPacketHelper.ReadStrategy.AutoReadByLength}必须设置此项
         * 用于分隔多条消息
         */
        socketClient.getSocketPacketHelper().setSendPacketLengthDataConvertor(new SocketPacketHelper.SendPacketLengthDataConvertor() {
            @Override
            public byte[] obtainSendPacketLengthDataForPacketLength(SocketPacketHelper helper, int packetLength) {
                /**
                 * 简单将int转换为byte[]
                 */
                byte[] data = new byte[4];
                data[3] = (byte) (packetLength & 0xFF);
                data[2] = (byte) ((packetLength >> 8) & 0xFF);
                data[1] = (byte) ((packetLength >> 16) & 0xFF);
                data[0] = (byte) ((packetLength >> 24) & 0xFF);
                return data;
            }
        });

        /**
         * 根据连接双方协议设置自动发送的包头数据
         * 每次发送数据包（包括心跳包）都会在发送包内容前自动发送此包头
         *
         * 若无需包头可删除此行
         */
//        socketClient.getSocketPacketHelper().setSendHeaderData(CharsetUtil.stringToData("SocketClient:", CharsetUtil.UTF_8));

        /**
         * 根据连接双方协议设置自动发送的包尾数据
         * 每次发送数据包（包括心跳包）都会在发送包内容后自动发送此包尾
         *
         * 若无需包尾可删除此行
         * 注意：
         * 使用{@link com.vilyever.socketclient.helper.SocketPacketHelper.ReadStrategy.AutoReadByLength}时不依赖包尾读取数据
         */
//        socketClient.getSocketPacketHelper().setSendTrailerData(new byte[]{0x13, 0x10});

        /**
         * 设置分段发送数据长度
         * 即在发送指定长度后通过 {@link SocketClientSendingDelegate#onSendingPacketInProgress(SocketClient, SocketPacket, float, int)}回调当前发送进度
         *
         * 若无需进度回调可删除此二行，删除后仍有【发送开始】【发送结束】的回调
         */
        socketClient.getSocketPacketHelper().setSendSegmentLength(1024); // 设置发送分段长度，单位byte
        socketClient.getSocketPacketHelper().setSendSegmentEnabled(false); // 设置允许使用分段发送，此值默认为false

        /**
         * 设置发送超时时长
         * 在发送每个数据包时，发送每段数据的最长时间，超过后自动断开socket连接
         * 通过设置分段发送{@link SocketPacketHelper#setSendSegmentEnabled(boolean)} 可避免发送大数据包时因超时断开，
         *
         * 若无需限制发送时长可删除此二行
         */
        socketClient.getSocketPacketHelper().setSendTimeout(CommandReceiveConfig.SEND_TIMEOUT_MILL); // 设置发送超时时长，单位毫秒
        socketClient.getSocketPacketHelper().setSendTimeoutEnabled(false); // 设置允许使用发送超时时长，此值默认为false
    }
    private void initReadConfig(){
        /**
         * 设置读取策略为自动读取指定长度
         */
        socketClient.getSocketPacketHelper().setReadStrategy(SocketPacketHelper.ReadStrategy.AutoReadByLength);

        /**
         * 设置包长度转换器
         * 即每次接收数据时，将远程端发送到本地的长度信息byte[]转换为int，然后读取相应长度的值
         *
         * 例：自动接收远程端所发送的socketClient.sendData(new byte[]{0x01, 0x02})【{0x01, 0x02}为将要接收的数据】的步骤为
         * 1. socketClient接收包头（如果设置了包头信息）（接收方式为一直读取到与包头相同的byte[],即可能过滤掉包头前的多余信息）
         * 2. socketClient接收长度为{@link SocketPacketHelper#getReceivePacketLengthDataLength()}（此处设置为4）的byte[]，通过下面设置的转换器，将byte[]转换为int值，此int值暂时称为X
         * 3. socketClient接收长度为X的byte[]
         * 4. socketClient接收包尾（如果设置了包尾信息）（接收方式为一直读取到与包尾相同的byte[],如无意外情况，此处不会读取到多余的信息）
         * 5. socketClient回调数据包
         *
         * 此转换器用于第二步
         *
         * 使用{@link com.vilyever.socketclient.helper.SocketPacketHelper.ReadStrategy.AutoReadByLength}必须设置此项
         * 用于分隔多条消息
         */
        socketClient.getSocketPacketHelper().setReceivePacketLengthDataLength(4);
        socketClient.getSocketPacketHelper().setReceivePacketDataLengthConvertor(new SocketPacketHelper.ReceivePacketDataLengthConvertor() {
            @Override
            public int obtainReceivePacketDataLength(SocketPacketHelper helper, byte[] packetLengthData) {
                /**
                 * 简单将byte[]转换为int
                 */
                int length =  (packetLengthData[3] & 0xFF) + ((packetLengthData[2] & 0xFF) << 8) + ((packetLengthData[1] & 0xFF) << 16) + ((packetLengthData[0] & 0xFF) << 24);
                return length - 4;
            }
        });

        /**
         * 根据连接双方协议设置的包头数据
         * 每次接收数据包（包括心跳包）都会先接收此包头
         *
         * 若无需包头可删除此行
         */
//        socketClient.getSocketPacketHelper().setReceiveHeaderData(CharsetUtil.stringToData("SocketClient:", CharsetUtil.UTF_8));

        /**
         * 根据连接双方协议设置的包尾数据
         *
         * 若无需包尾可删除此行
         * 注意：
         * 使用{@link com.vilyever.socketclient.helper.SocketPacketHelper.ReadStrategy.AutoReadByLength}时不依赖包尾读取数据
         */
//        socketClient.getSocketPacketHelper().setReceiveTrailerData(new byte[]{0x13, 0x10});

        /**
         * 设置接收超时时长
         * 在指定时长内没有数据到达本地自动断开
         *
         * 若无需限制接收时长可删除此二行
         */
        socketClient.getSocketPacketHelper().setReceiveTimeout(CommandReceiveConfig.READ_TIMEOUT_MILL); // 设置接收超时时长，单位毫秒
        socketClient.getSocketPacketHelper().setReceiveTimeoutEnabled(false); // 设置允许使用接收超时时长，此值默认为false
    }
    private int currentConnectionState = -1;
    /**
     * 常用回调配置
     */
    public void initCustomListener(){
        // 对应removeSocketClientDelegate
        socketClient.registerSocketClientDelegate(new SocketClientDelegate() {
            /**
             * 连接上远程端时的回调
             */
            @Override
            public void onConnected(SocketClient client) {
                if(currentConnectionState != 1){
                    currentConnectionState = 1;
                    LoggerHelper.i("Socket连接成功-->" + client.getAddress().getRemoteIP() + ":" + client.getAddress().getRemotePort());
                }
//                SocketPacket packet = socketClient.sendData(new byte[]{0x02}); // 发送消息
//                packet = socketClient.sendString("start"); // 发送消息
//                socketClient.cancelSend(packet); // 取消发送，若在等待发送队列中则从队列中移除，若正在发送则无法取消
                socketClient.sendString(heartBeatMessage);

            }

            /**
             * 与远程端断开连接时的回调
             */
            @Override
            public void onDisconnected(SocketClient client) {
                if(currentConnectionState != 0){
                    currentConnectionState = 0;
                    LoggerHelper.i("Socket连接失败，每隔5秒将尝试重连-->" + client.getAddress().getRemoteIP() + ":" + client.getAddress().getRemotePort());

                }
                mainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 可在此实现自动重连
                        if(socketClient != null){
                            autoSetHeartbeat();
                            socketClient.connect();
                        }
                    }
                },5000);

            }

            /**
             * 接收到数据包时的回调
             */
            @Override
            public void onResponse(final SocketClient client, @NonNull SocketResponsePacket responsePacket) {

                if(!responsePacket.isHeartBeat()){
                    String message = responsePacket.getMessage(); // 获取按默认设置的编码转化的String，可能为null
                    try {
                        CommandMessageData messageData = GsonUtil.gson.fromJson(message,CommandMessageData.class);
                        LoggerHelper.i("Socket接收到命令：" + messageData.getCommond());
                        EventBus.getDefault().post(messageData);
                        messageData.setCommond("M077");
                        socketClient.sendString(GsonUtil.gson.toJson(messageData));
                    }catch (Exception ex){
                        LoggerHelper.i("Socket接收到数据 但是处理异常：" + responsePacket.getMessage() + "\n" + ex.getMessage());
                    }
                }
            }
        });
    }

    /**
     * 发送状态回调配置
     */
    public void initSendDataListener(){
        socketClient.registerSocketClientSendingDelegate(new SocketClientSendingDelegate() {
            /**
             * 数据包开始发送时的回调
             */
            @Override
            public void onSendPacketBegin(SocketClient client, SocketPacket packet) {
            }

            /**
             * 数据包取消发送时的回调
             * 取消发送回调有以下情况：
             * 1. 手动cancel仍在排队，还未发送过的packet
             * 2. 断开连接时，正在发送的packet和所有在排队的packet都会被取消
             */
            @Override
            public void onSendPacketCancel(SocketClient client, SocketPacket packet) {
            }

            /**
             * 数据包发送的进度回调
             * progress值为[0.0f, 1.0f]
             * 通常配合分段发送使用
             * 可用于显示文件等大数据的发送进度
             */
            @Override
            public void onSendingPacketInProgress(SocketClient client, SocketPacket packet, float progress, int sendedLength) {
            }

            /**
             * 数据包完成发送时的回调
             */
            @Override
            public void onSendPacketEnd(SocketClient client, SocketPacket packet) {
            }
        });
    }

    /**
     * 接收状态回调配置
     */
    public void initReceiveDataListener(){
// 对应removeSocketClientReceiveDelegate
        socketClient.registerSocketClientReceiveDelegate(new SocketClientReceivingDelegate() {
            /**
             * 开始接受一个新的数据包时的回调
             */
            @Override
            public void onReceivePacketBegin(SocketClient client, SocketResponsePacket packet) {
//                LoggerHelper.d("socket onReceivePacketBegin");
            }

            /**
             * 完成接受一个新的数据包时的回调
             */
            @Override
            public void onReceivePacketEnd(SocketClient client, SocketResponsePacket packet) {
//                if(packet.isHeartBeat()){
//
//                }else{
//
//                }
//                String message = packet.getMessage();
//                LoggerHelper.d("socket onReceivePacketEnd:" + message);
            }

            /**
             * 取消接受一个新的数据包时的回调
             * 在断开连接时会触发
             */
            @Override
            public void onReceivePacketCancel(SocketClient client, SocketResponsePacket packet) {
            }

            /**
             * 接受一个新的数据包的进度回调
             * progress值为[0.0f, 1.0f]
             * 仅作用于ReadStrategy为AutoReadByLength的自动读取
             * 因AutoReadByLength可以首先接受到剩下的数据包长度
             */
            @Override
            public void onReceivingPacketInProgress(SocketClient client, SocketResponsePacket packet, float progress, int receivedLength) {
            }
        });
    }

    public void disConnect(){
        socketClient.disconnect();
        socketClient = null;
    }


    private void autoSetHeartbeat(){
        refreshHeatBeatMessage();
        /**
         * 设置自动发送的心跳包信息
         */
        socketClient.getHeartBeatHelper().setDefaultSendData(CharsetUtil.stringToData(heartBeatMessage, CharsetUtil.UTF_8));

        /**
         * 设置远程端发送到本地的心跳包信息内容，用于判断接收到的数据包是否是心跳包
         * 通过{@link SocketResponsePacket#isHeartBeat()} 查看数据包是否是心跳包
         */
        socketClient.getHeartBeatHelper().setDefaultReceiveData(CharsetUtil.stringToData(heartBeatMessage, CharsetUtil.UTF_8));

        socketClient.getHeartBeatHelper().setHeartBeatInterval(CommandReceiveConfig.HeartBeatInterval); // 设置自动发送心跳包的间隔时长，单位毫秒
        socketClient.getHeartBeatHelper().setSendHeartBeatEnabled(true); // 设置允许自动发送心跳包，此值默认为false
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disConnect();
        LoggerHelper.i("Socket长连接服务销毁");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
