package com.zhuoxin.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zhuoxin.R;
import com.zhuoxin.adapter.RocketAdapter;
import com.zhuoxin.base.ActionBarActivity;
import com.zhuoxin.biz.MemoryManager;

public class RocketActivity extends ActionBarActivity {
    TextView tv_brand;
    TextView tv_version;
    TextView tv_rocket_space;
    ListView lv_rocket;
    Button btn_rocket;
    ProgressBar pb_rocket;
    ProgressBar pb_rocket_loading;
    RocketAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rocket);
        initActionBar(true, "手机加速", false, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initView();
        initData();
        initListView();
    }


    private void initView() {
        tv_brand = (TextView) findViewById(R.id.tv_brand);
        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_rocket_space = (TextView) findViewById(R.id.tv_rocket_space);
        lv_rocket = (ListView) findViewById(R.id.lv_rocket);
        btn_rocket = (Button) findViewById(R.id.btn_rocket);
        pb_rocket_loading = (ProgressBar) findViewById(R.id.pb_rocket_loading);
        btn_rocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MemoryManager.killBackgroundProcessesRAM(RocketActivity.this);
                initListView();
                getRunTimeMemory();
            }
        });
        pb_rocket = (ProgressBar) findViewById(R.id.pb_rocket);
    }

    private void initData() {
        //获取手机品牌
        String brand = Build.BRAND;
        tv_brand.setText(brand);
        //获取手机版本号
        String version = Build.VERSION.RELEASE;//构建版本发布
        tv_version.setText(version);
        getRunTimeMemory();

    }

    private void getRunTimeMemory() {
        //进度条内存设置
        pb_rocket.setProgress(MemoryManager.usedPercent(this));
        //剩余内存文本设置
        tv_rocket_space.setText("剩余内存" + MemoryManager.availableRAMString(this) + "/" + MemoryManager.totalRAMString(this));
    }

    private void initListView() {
        //4.0之前,通过ActivityManager获取运行中的进程
       /* ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> rapi = am.getRunningAppProcesses();
        for (int i = 0; i < rapi.size(); i++) {
            Log.e("运行进程", getPackageName());
        }*/
        //4.0之后,获取不到运行中的程序,而且在5.0是谷歌也删除了这个接口
        adapter = new RocketAdapter(MemoryManager.getRunningAppProcessesInfo(this), this);
        lv_rocket.setAdapter(adapter);
        pb_rocket_loading.setVisibility(View.INVISIBLE);

    }

}
