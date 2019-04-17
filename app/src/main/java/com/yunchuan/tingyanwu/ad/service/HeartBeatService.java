package com.yunchuan.tingyanwu.ad.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.yunchuan.tingyanwu.ad.util.CrashApplication;
import com.yunchuan.tingyanwu.ad.util.CustomProtocalCodecFactory;
import com.yunchuan.tingyanwu.ad.util.ProtocalPack;
import com.yunchuan.tingyanwu.ad.util.SharedPreUtil;
import com.yunchuan.tingyanwu.ad.vo.Message;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.IoServiceListener;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.greenrobot.eventbus.EventBus;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HeartBeatService extends Service {

    private NioSocketConnector connector = null;
    private IoSession session = null;
    private SoundPool mSoundPool;
    private CopyOnWriteArrayList<Message> listMessage = new CopyOnWriteArrayList<>();

    private boolean openDoor = false;
    private boolean isNet = false;

    @Override
    public void onCreate() {
        super.onCreate();

        registerReceiver(netReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        new Thread() {
            @Override
            public void run() {
                initClientMina();
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (listMessage != null && !listMessage.isEmpty()) {
                        for (Message message : listMessage) {
                            if (message != null) {
                                String str = message.getCommond().split(",")[0];
                                Log.d("heartBeat", str);
                                if (str.equals("reloadAd")) {//开门

                                }
                                iteratorRemove(listMessage, message);
                            }
                        }
                    }
                }
            }
        }.start();

    }

    private BroadcastReceiver netReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {//网络监视器
            ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cwjManager.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
            } else {
                if (session != null) {
                    session.close(true);
                }
                if (connector != null) {
                    connector.dispose();
                }
            }
        }
    };

    public void iteratorRemove(List<Message> list, Message target) {

        for (int i = 0; i < list.size(); i++) {
            Message value = list.get(i);
            if (value.getCommond().equals(target.getCommond())) {
                list.remove(value);
                i--;
            }
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(netReceiver);
        Intent intent = new Intent();
        intent.setAction("HeartBeatService.onDestroy");
        HeartBeatService.this.sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /*
    获取消息
     */

    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    /*
    发送消息
     */
    private void sendMessage() throws InterruptedException {
        while (isNet == false) {
            Intent intent = new Intent();
            intent.setAction("HeartBeatService.SocketConnected");
            HeartBeatService.this.sendBroadcast(intent);

            Message msg = new Message();
            msg.setConverType("Object");
            msg.setContent("KeepLive");
            msg.setCommond("Commond");
            msg.setmId(SharedPreUtil.getString(HeartBeatService.this, "Mac", ""));

            String jsonObject = new Gson().toJson(msg);

            session.write(new ProtocalPack(jsonObject));

            Thread.sleep(1000 * 10);
        }
    }

    /*
    int转byte[]
     */
    private byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

    /*
    合并byte[]数组
     */
    private byte[] addBytes(byte[] data1, byte[] data2) {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;
    }



    private void initClientMina() {
        while (true) {
            connector = new NioSocketConnector();
            //设置链接超时时间
            connector.setConnectTimeoutMillis(1000 * 30);
            //添加过滤器
            connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CustomProtocalCodecFactory(Charset.forName("UTF-8"))));
            //添加消息处理
            connector.setHandler(new ClientHandler());
            //添加状态监听
            connector.addListener(new IoListener());
            try {
                //创建连接
                ConnectFuture future = connector.connect(new InetSocketAddress(CrashApplication.HOST,CrashApplication.SOCKET_PORT));
                //等待连接创建完成
                future.awaitUninterruptibly();
                //获得session
                session = future.getSession();
                if (session.isConnected()) {
                    Log.e("heartBeat","socket connect!");
                    isNet = false;
                    sendMessage();
                    break;
                }
            } catch (Exception e) {
                Log.e("heartBeat",e.toString());
                if (session != null) {
                    session.close(true);
                }
                if (connector != null) {
                    connector.dispose();
                }

            }


            try {
                Thread.sleep(1000*30);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    public class ClientHandler extends IoHandlerAdapter {

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {

            ProtocalPack pk = (ProtocalPack) message;
            Message msg = new Gson().fromJson(pk.getContent(), Message.class);
            EventBus.getDefault().post(msg.getCommond());

            Log.e("heartBeat",msg.getCommond());


            listMessage.add(msg);
        }
    }

    /*
    MINA状态监听
     */
    public class IoListener implements IoServiceListener {

        @Override
        public void serviceActivated(IoService ioService) throws Exception {

        }

        @Override
        public void serviceIdle(IoService ioService, IdleStatus idleStatus) throws Exception {

        }

        @Override
        public void serviceDeactivated(IoService ioService) throws Exception {

        }

        @Override
        public void sessionCreated(IoSession ioSession) throws Exception {

        }

        @Override
        public void sessionClosed(IoSession ioSession) throws Exception {

        }

        @Override
        public void sessionDestroyed(IoSession ioSession) throws Exception {
            isNet = true;

            Intent intent = new Intent();
            intent.setAction("HeartBeatService.SocketIsNotConnected");
            HeartBeatService.this.sendBroadcast(intent);

            if (session != null) {
                session.close(true);
            }
            if (connector != null) {
                connector.dispose();
            }

            initClientMina();
        }
    }
}
