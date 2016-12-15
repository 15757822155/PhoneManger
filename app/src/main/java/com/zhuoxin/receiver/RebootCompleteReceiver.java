package com.zhuoxin.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.zhuoxin.service.MusicService;

public class RebootCompleteReceiver extends BroadcastReceiver {
    public RebootCompleteReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //找到系统的配置文件
        boolean start = context.getSharedPreferences("config", Context.MODE_PRIVATE).getBoolean("startWhenBootComplete", false);
        //如果系统配置文件中开机启动的设置是true,就执行音乐服务
        if (start) {
            Toast.makeText(context, "重启完成", Toast.LENGTH_SHORT).show();
            Intent musicIntent = new Intent();
            musicIntent.setClass(context, MusicService.class);
            context.startService(musicIntent);
        }
    }
}
