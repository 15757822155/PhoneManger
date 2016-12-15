package com.zhuoxin.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.zhuoxin.R;
import com.zhuoxin.adapter.AppAdapter;
import com.zhuoxin.base.ActionBarActivity;
import com.zhuoxin.entity.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class SoftwareActivity extends ActionBarActivity {
    //所有安装包列表
    List<AppInfo> appInfoList = new ArrayList<AppInfo>();
    //找到进度条
    ProgressBar pb_softmgr_loading;
    //自定义适配器
    AppAdapter adapter;
    //当前的默认布局(未改变)
    ListView ll_software;
    //根据app类型,选择显示的软件(所有,系统,用户)
    String appType;
    //删除所有复选框
    CheckBox cb_deleteall;
    //删除按钮
    Button btn_delete;
    //广播接收者
    BroadcastReceiver receiver;
    //标题类型
    String softwareType;
    //Handler处理
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            //处理逻辑
            int what = message.what;
            switch (what) {
                //如果从handler获取的message的标记为1,做一下操作
                case 1:
                    //设置进度条和listview的显示和隐藏
                    ll_software.setVisibility(View.VISIBLE);
                    pb_softmgr_loading.setVisibility(View.INVISIBLE);
                    //通知adapter数据发生改变
                    adapter.notifyDataSetChanged();
                    break;
            }
            return false;//如果不想让其他handler处理,返回一个true
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_software);
        //数据初始化等操作
        initView();
        //动态创建receiver,注册后必须反注册
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //重新获取数据
                saveAppInfo();
                //通知adapter列表数据发生改变
                adapter.notifyDataSetChanged();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        //注册广播接收者
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        //反注册广播接收者
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    //初始化数据
    private void initView() {
        //获取从上一布局保存的appType(从bundle中获取)
        appType = getIntent().getBundleExtra("bundle").getString("appType", "all");
        //获取从上一布局保存的softwareType(从bundle中获取)
        softwareType = getIntent().getBundleExtra("bundle").getString("softwareType", "所有软件");
        initActionBar(true, softwareType, false, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ll_software = (ListView) findViewById(R.id.ll_software);
        pb_softmgr_loading = (ProgressBar) findViewById(R.id.pb_softmgr_loading);
        cb_deleteall = (CheckBox) findViewById(R.id.cb_deleteall);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        adapter = new AppAdapter(appInfoList, this);
        ll_software.setAdapter(adapter);
        saveAppInfo();//调用保存软件信息的方法
        //设置删除所有软件复选框的监听事件
        cb_deleteall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //通过for循环把数据的状态改变
                for (int i = 0; i < appInfoList.size(); i++) {
                    if (appType.equals("all")) {
                        if (!appInfoList.get(i).isSystem) {
                            appInfoList.get(i).isDelete = b;
                        }
                    } else if (appType.equals("system")) {
                        appInfoList.get(i).isDelete = false;
                    } else {
                        appInfoList.get(i).isDelete = b;
                    }
                }
                //把最新的数据给了adapter,并刷新页面
                adapter.notifyDataSetChanged();
            }
        });
        //对删除按钮设置单击监听事件
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //循环取出列表中的app,如果是isDelete,就调用删除程序
                for (AppInfo info : appInfoList) {
                    if (info.isDelete) {
                        //如果包名不是手机管家的包名就做以下内容
                        if (!info.packagename.equals(getPackageName())) {
                            //跳转到系统的卸载程序
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_DELETE);
                            intent.setData(Uri.parse("package:" + info.packagename));
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    }

    //保存手机应用信息
    private void saveAppInfo() {
        //设置进度条和listview的显示隐藏(这个必须写在子线程外面)
        pb_softmgr_loading.setVisibility(View.VISIBLE);
        ll_software.setVisibility(View.INVISIBLE);
        //因为访问数据(文件,网络)是耗时操作,所以要开启子线程操作,避免出现ANR(application not responding)现象
        new Thread(new Runnable() {
            @Override
            public void run() {
                //每次获取数据前先清空数据
                appInfoList.clear();
                //获取所有安装包的信息（获得包管理、获得已安装包（包管理的可卸载包和活动包））
                List<PackageInfo> packageInfoList = getPackageManager().getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES | PackageManager.GET_ACTIVITIES);
                //循环获取所有软件信息
                for (PackageInfo info : packageInfoList) {
                    //循环获得手机中软件信息
                    ApplicationInfo applicationInfo = info.applicationInfo;
                    //获得应用是否是系统应用的boolean值；
                    boolean isSystem;
                    //判断是否为系统应用（通过位运算完成）
                    if ((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0 || (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        isSystem = true;
                    } else {
                        isSystem = false;
                    }
                    //获取应用名称
                    String appname = (String) getPackageManager().getApplicationLabel(applicationInfo);
                    //获取应用图标
                    Drawable appicon = getPackageManager().getApplicationIcon(applicationInfo);
                    //获取应用版本号
                    String appversion = info.versionName;
                    //获取应用包名
                    String packagename = info.packageName;
                    //根据从上一布局传过来的appType来选择显示的内容
                    if (appType.equals("all")) {//假如类型为all,则显示所有应用内容
                        AppInfo appInfo = new AppInfo(appicon, appname, packagename, appversion, isSystem, false);
                        appInfoList.add(appInfo);
                    } else if (appType.equals("system")) {//假如类型为system,并且isSystem为true,则显示系统应用
                        if (isSystem) {
                            AppInfo appInfo = new AppInfo(appicon, appname, packagename, appversion, isSystem, false);
                            appInfoList.add(appInfo);
                        }
                    } else {
                        if (!isSystem) {//假如类型为used,并且isSystem为false,则显示第三方应用
                            AppInfo appInfo = new AppInfo(appicon, appname, packagename, appversion, isSystem, false);
                            appInfoList.add(appInfo);
                        }
                    }
                }
                //runOnUiThread子线程通知主线程的改变数据 View.post同理
                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });*/
                //Handler
                //先是创建了一个Message,直接让hangdler获取消息
                Message message = handler.obtainMessage();
                //对这个message设计标记为1
                message.what = 1;
                //将这个消息传递给handler
                handler.sendMessage(message);
            }
        }).start();//开启线程

    }
}
