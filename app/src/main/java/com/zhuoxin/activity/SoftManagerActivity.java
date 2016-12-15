package com.zhuoxin.activity;

import android.Manifest;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhuoxin.R;
import com.zhuoxin.base.ActionBarActivity;
import com.zhuoxin.view.PiechartView;

import java.io.File;

public class SoftManagerActivity extends ActionBarActivity implements View.OnClickListener {
    PiechartView pv_softmgr;
    TextView tv_softmgr;
    ProgressBar pb_softmgr;
    RelativeLayout rl_allSoftware;
    RelativeLayout rl_systemSoftware;
    RelativeLayout rl_usedSoftware;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soft_manager);
        initView();
        requestPermissionAndShowInfo();
    }

    //初始化数据
    private void initView() {
        pv_softmgr = (PiechartView) findViewById(R.id.pv_softmgr);
        tv_softmgr = (TextView) findViewById(R.id.tv_softmgr);
        pb_softmgr = (ProgressBar) findViewById(R.id.pb_softmgr);
        rl_allSoftware = (RelativeLayout) findViewById(R.id.rl_allSoftware);
        rl_systemSoftware = (RelativeLayout) findViewById(R.id.rl_systemSoftware);
        rl_usedSoftware = (RelativeLayout) findViewById(R.id.rl_usedSoftware);
        initActionBar(true, "软件管理", false, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rl_usedSoftware.setOnClickListener(this);
        rl_systemSoftware.setOnClickListener(this);
        rl_allSoftware.setOnClickListener(this);
    }

    //申请权限并显示
    private void requestPermissionAndShowInfo() {
        //动态的申请权限
        int permissionState = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionState == PackageManager.PERMISSION_GRANTED) {
            //显示信息
            showMemory();
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showMemory();
        } else {
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle("权限获取").setMessage("请从系统获取读取权限").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                }
            }).setNegativeButton("Cancle", null).create();
            dialog.show();
        }
    }

    //显示手机存储
    private void showMemory() {
        //获取手机SD卡中的信息
        File file = Environment.getExternalStorageDirectory();
        //获取总大小和已用空间
        long total = file.getTotalSpace();//获取总空间大小
        long used = total - file.getFreeSpace();//获取已用空间大小
        //把数据展示出来
        int angle = (int) (360.0 * used / total);//已用空间角度
        pv_softmgr.showPiechart(angle);
        pb_softmgr.setProgress((int) (100.0 * used / total));
        String totalStr = Formatter.formatFileSize(this, total);
        String freeStr = Formatter.formatFileSize(this, file.getFreeSpace());
        tv_softmgr.setText("可用空间：" + freeStr + "/" + totalStr);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Bundle bundle = new Bundle();
        switch (id) {
            case R.id.rl_allSoftware:
                //保存string类型的bundle,设置的变量名为appType,存入的字符串为all
                bundle.putString("appType", "all");
                bundle.putString("softwareType","所有软件");
                startActivity(SoftwareActivity.class, bundle);
                break;
            case R.id.rl_systemSoftware:
                //保存string类型的bundle,设置的变量名为appType,存入的字符串为system
                bundle.putString("appType", "system");
                bundle.putString("softwareType","系统软件");
                startActivity(SoftwareActivity.class, bundle);
                break;
            case R.id.rl_usedSoftware:
                //保存string类型的bundle,设置的变量名为appType,存入的字符串为used
                bundle.putString("appType", "used");
                bundle.putString("softwareType","用户软件");
                startActivity(SoftwareActivity.class, bundle);
                break;
        }
    }
}
