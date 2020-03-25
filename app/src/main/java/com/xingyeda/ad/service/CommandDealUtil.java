package com.xingyeda.ad.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.xingyeda.ad.broadcast.BroadCasetKeys;
import com.xingyeda.ad.config.SettingConfigManager;
import com.xingyeda.ad.module.addata.ADListManager;
import com.xingyeda.ad.module.versionmanager.VersionManager;
import com.xingyeda.ad.service.socket.CommandMessageData;
import com.xingyeda.ad.util.DeviceUtil;
import com.xingyeda.ad.util.MyLog;
import com.zz9158.app.common.utils.ToolUtils;

import java.io.File;

public class CommandDealUtil {
    public static void dealCommand(Context context, CommandMessageData commandMessageData){
        String command = commandMessageData.getCommond();
        MyLog.i("Command received :" + command);
        if (ServiceCommond.UPDATE_DEVICE.equals(command))//更新设备
        {
            sendBroadcast(context,BroadCasetKeys.UPDATE_DEVICE);
        }  else if (ServiceCommond.RELOADIMG.equals(command))//刷新广告等
        {
            ADListManager.getInstance(context).setNeedUpdateList();
        } else if (ServiceCommond.PC_RESTART.equals(command))//重启
        {
            DeviceUtil.reboot(context);
        }else if(ServiceCommond.UPLOAD_LOGFILES.equals(command)){
            String content = commandMessageData.getContent();
            if(!ToolUtils.string().isEmpty(content)){
                LogFileManager.uploadFiles(context, content.split("\\|"));
            }

        }else if(ServiceCommond.DELETE_ALL_LOGFILES.equals(command)){
            if (MyLog.getLogRootPath() != null) {
                File[] files = MyLog.getLogRootPath().listFiles();
                if (files.length > 0) {
                    for (File file : files) {
                        if (file.getName().endsWith(".txt")) {
                            ToolUtils.file().deleteFile(file);
                        }
                    }
                }
            }
        }else if(ServiceCommond.REMOTE_CONFIG.equals(command)){
            SettingConfigManager.getInstance().updateSettingForNet(context);
        }
    }
    private static void sendBroadcast(Context context, String action) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(action));
    }
}
