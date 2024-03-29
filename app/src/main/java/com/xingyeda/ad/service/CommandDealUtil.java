package com.xingyeda.ad.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.xingyeda.ad.broadcast.BroadCasetKeys;
import com.xingyeda.ad.config.DeviceUUIDManager;
import com.xingyeda.ad.config.SettingConfigManager;
import com.xingyeda.ad.logdebug.LogDebugUtil;
import com.xingyeda.ad.module.ad.data.ADListManager;
import com.xingyeda.ad.module.logger.LogFileManager;
import com.xingyeda.ad.service.socket.CommandMessageData;
import com.xingyeda.ad.util.CustomMainBoardUtil;
import com.xingyeda.ad.util.MyLog;
import com.zz9158.app.common.utils.ToolUtils;

import java.io.File;

public class CommandDealUtil {
    public static void dealCommand(Context context, CommandMessageData commandMessageData){
        String command = commandMessageData.getCommond();
        LogDebugUtil.appendLog("Command received :" + command + " content:" + commandMessageData.getContent());
        if (ServiceCommond.UPDATE_DEVICE.equals(command))//更新设备
        {
            sendBroadcast(context,BroadCasetKeys.UPDATE_DEVICE);
        }  else if (ServiceCommond.RELOADIMG.equals(command))//刷新广告等
        {
            ADListManager.getInstance(context).setNeedUpdateList();
        } else if (ServiceCommond.PC_RESTART.equals(command))//重启
        {
            CustomMainBoardUtil.reboot(context,"接收到服务器重启命令");
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
        }else if(ServiceCommond.CHANG_DEVICE_UUID.equals(command)){
            DeviceUUIDManager.saveNewUUIDToFile(commandMessageData.getContent());
            CustomMainBoardUtil.reboot(context,"远程修改设备UUID");
        }
    }
    private static void sendBroadcast(Context context, String action) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(action));
    }
}
