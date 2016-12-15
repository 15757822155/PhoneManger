package com.zhuoxin.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.BatteryManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhuoxin.R;
import com.zhuoxin.base.ActionBarActivity;
import com.zhuoxin.biz.MemoryManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PhoneStateActivity extends ActionBarActivity {
    @InjectView(R.id.pb_battery)
    ProgressBar pb_battery;
    @InjectView(R.id.iv_battery)
    ImageView iv_battery;
    @InjectView(R.id.tv_battery)
    TextView tv_battery;
    @InjectView(R.id.tv_state_name)
    TextView tv_state_name;
    @InjectView(R.id.tv_state_version)
    TextView tv_state_version;
    @InjectView(R.id.tv_state_cpu)
    TextView tv_state_cpu;
    @InjectView(R.id.tv_state_cpunumber)
    TextView tv_state_cpunumber;
    @InjectView(R.id.tv_state_totalram)
    TextView tv_state_totalram;
    @InjectView(R.id.tv_state_availram)
    TextView tv_state_availram;
    @InjectView(R.id.tv_state_screen)
    TextView tv_state_screen;
    @InjectView(R.id.tv_state_camear)
    TextView tv_state_camear;
    @InjectView(R.id.tv_state_bsversion)
    TextView tv_state_bsversion;
    @InjectView(R.id.tv_state_root)
    TextView tv_state_root;
    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_state);
        ButterKnife.inject(this);
        initActionBar(true, "手机状态", false, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initView() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //当前电量
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                //当前总电量
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
                //当前电量百分比
                int parent = (int) (100.0 * level / scale);
                if (parent == 100) {
                    iv_battery.setBackgroundColor(getResources().getColor(R.color.piechartColor, null));
                } else {
                    iv_battery.setBackgroundColor(getResources().getColor(R.color.piechartBackgroudColor, null));
                }
                pb_battery.setProgress(parent);
                tv_battery.setText(parent + "%");

            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, filter);
        //获取品牌版本
        tv_state_name.setText("设备名称:" + Build.BRAND);
        tv_state_version.setText("系统版本:Android" + Build.VERSION.RELEASE);
        //获取cpu
        tv_state_cpu.setText("CPU型号:" + getCPUName());
        tv_state_cpunumber.setText("核心数:" + getCPUCore());
        //获取运存
        tv_state_totalram.setText("手机总运存"+ MemoryManager.totalRAMString(this));
        tv_state_availram.setText("手机当前运存"+ MemoryManager.availableRAMString(this));
        //获取屏幕分辨率
        tv_state_screen.setText("屏幕分辨率:" + getScreen());
        tv_state_camear.setText("相机分辨率" + getCamera());
        //基带版本
        tv_state_bsversion.setText("基带版本:" + Build.VERSION.INCREMENTAL);
        tv_state_root.setText("是否root:" + isRoot());
    }

    private String getCPUName() {
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader("/proc/cpuinfo");
            br = new BufferedReader(fr);
            String str = "";
            while ((str = br.readLine()) != null) {
                if ((str.contains("model name"))) {
                    return str.split(":")[1];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getCPUCore() {
        FileReader fr = null;
        BufferedReader br = null;
        int core = 0;
        try {
            fr = new FileReader("/proc/cpuinfo");
            br = new BufferedReader(fr);
            String str = "";
            while ((str = br.readLine()) != null) {
                if ((str.contains("processor"))) {
                    core++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return core;
    }

    private String getScreen() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point.y + "*" + point.x;
    }

    private String getCamera() {
        String s = null;
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            android.hardware.Camera camera = Camera.open();
            Camera.Parameters parameters = camera.getParameters();
            List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
            s = sizes.get(0).height + "*" + sizes.get(0).width;
            //释放相机
            camera.release();
            return s;
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 0);
        }
        return s;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            android.hardware.Camera camera = Camera.open();
            Camera.Parameters parameters = camera.getParameters();
            List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
            String s = sizes.get(0).height + "*" + sizes.get(0).width;
            //释放相机
            camera.release();
            tv_state_camear.setText("相机分辨率:" + s);
        } else {
            Toast.makeText(this, "请设置权限", Toast.LENGTH_SHORT);
        }
    }

    private boolean isRoot() {
        if (new File("/system/bin/su").exists() || new File("/system/xbin/su").exists()) {
            return true;
        } else {
            return false;
        }
    }
}
