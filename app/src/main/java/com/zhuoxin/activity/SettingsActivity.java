package com.zhuoxin.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.zhuoxin.R;
import com.zhuoxin.base.ActionBarActivity;

public class SettingsActivity extends ActionBarActivity implements View.OnClickListener {
    RelativeLayout rl_start;
    RelativeLayout rl_notification;
    RelativeLayout rl_push;
    RelativeLayout rl_help;
    RelativeLayout rl_aboutus;
    ToggleButton tb_start;
    ToggleButton tb_notification;
    ToggleButton tb_push;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initActionBar(true, "系统设置", false, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initView();
        initData();
        mContext = this;
    }


    private void initView() {

        rl_aboutus = (RelativeLayout) findViewById(R.id.rl_aboutus);
        rl_help = (RelativeLayout) findViewById(R.id.rl_help);
        rl_notification = (RelativeLayout) findViewById(R.id.rl_notification);
        rl_push = (RelativeLayout) findViewById(R.id.rl_push);
        rl_start = (RelativeLayout) findViewById(R.id.rl_start);
        tb_notification = (ToggleButton) findViewById(R.id.tb_notification);
        tb_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //状态选中时,在通知栏显示信息,状态取消时,清空通知栏信息
                if (b) {
                    Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Notification notification = new Notification.Builder(SettingsActivity.this)
                            .setContentTitle("手机管家")
                            .setContentText("手机垃圾过多,请清理")
                            .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_phonemgr))
                            .setSmallIcon(R.drawable.icon_phonemgr)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .build();
                    NotificationManager manager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
                    manager.notify(0, notification);
                } else {
                    NotificationManager manager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
                    manager.cancel(0);
                }
            }
        });
        tb_push = (ToggleButton) findViewById(R.id.tb_push);
        tb_push.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(SettingsActivity.this, "功能暂未推出", Toast.LENGTH_SHORT).show();
            }
        });
        tb_start = (ToggleButton) findViewById(R.id.tb_start);
        tb_start.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //点击按钮将这个设置传递到系统配置文件中
                getSharedPreferences("config", MODE_PRIVATE).edit().putBoolean("startWhenBootComplete", tb_start.isChecked()).commit();
            }
        });
    }

    private void initData() {
        boolean startWhenBootComplete = getSharedPreferences("config", MODE_PRIVATE).getBoolean("startWhenBootComplete", false);
        tb_start.setChecked(startWhenBootComplete);
        rl_start.setOnClickListener(this);
        rl_notification.setOnClickListener(this);
        rl_push.setOnClickListener(this);
        rl_help.setOnClickListener(this);
        rl_aboutus.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.rl_start:
                tb_start.setChecked(!tb_start.isChecked());
                break;
            case R.id.rl_notification:
                tb_notification.setChecked(!tb_notification.isChecked());
                break;
            case R.id.rl_push:
                tb_push.setChecked(!tb_push.isChecked());
                Toast.makeText(this, "功能暂未推出", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_help:
                Bundle bundle = new Bundle();
                bundle.putBoolean("isFromSettings", true);
                startActivity(Guide_Activity.class, bundle);
                break;
            case R.id.rl_aboutus:
                startActivity(AboutUsActivity.class);
                break;
        }
    }
}
