package com.zhuoxin.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.zhuoxin.R;
import com.zhuoxin.adapter.TelNumberAdapter;
import com.zhuoxin.base.ActionBarActivity;

import com.zhuoxin.db.DBManager;
import com.zhuoxin.entity.TelNumberInfo;

import java.io.File;
import java.util.List;

public class PhoneNumActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    ListView ll_phone_num;
    List<TelNumberInfo> dataList;
    String number;
    int idx;
    final int PERMISSION_REQUEST_CODE = 0;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_num);
        Bundle bundle = getIntent().getBundleExtra("bundle");//将上个页面中传过来的数据传给bundle
        title = bundle.getString("title");//直接用bundle调用数据
        idx = bundle.getInt("idx", 1);
        initView();//获取电话，并拨打电话，设置权限
    }

    private void initView() {
        //获取号码文件
        File targetFile = new File(getFilesDir(), "commonnum.db");
        dataList = DBManager.readTelNumberInfo(targetFile, idx);
        TelNumberAdapter adapter = new TelNumberAdapter(dataList, this);
        ll_phone_num = (ListView) findViewById(R.id.ll_phone_num);
        ll_phone_num.setAdapter(adapter);
        ll_phone_num.setOnItemClickListener(this);
        initActionBar(true, title, false, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        number = dataList.get(i).number;
        //检查版本
        if (Build.VERSION.SDK_INT >= 23) {
            int hasGot = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
            //判断权限是否允许
            if (hasGot == PackageManager.PERMISSION_GRANTED) {
                call(number);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CODE);
            }
        } else {
            call(number);
        }
    }

    //编写权限回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    call(number);
                } else {
                    AlertDialog dialog = new AlertDialog.Builder(this).setTitle("权限提醒").setMessage("如果想获取权限，请点击确定").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).create();
                    dialog.show();
                }
                break;
        }
    }

    public void call(final String number) {
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("拨出电话").setMessage("是否拨打该电话" + number).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(Intent.ACTION_CALL, Uri.parse("tel:" + number));

            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create();
        dialog.show();

    }
}
