package com.zhuoxin.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhuoxin.R;
import com.zhuoxin.base.ActionBarActivity;
import com.zhuoxin.biz.FileManager;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;

public class FileManagerActivity extends ActionBarActivity {
    @InjectView(R.id.tv_file_manager)
    TextView tv_file_manager;
    @InjectViews({R.id.pb_anyFile, R.id.pb_txtFile, R.id.pb_videoFile, R.id.pb_audioFile, R.id.pb_imageFile, R.id.pb_zipFile, R.id.pb_apkFile})
    List<ProgressBar> pbList;
    @InjectViews({R.id.iv_anyFile, R.id.iv_txtFile, R.id.iv_videoFile, R.id.iv_audioFile, R.id.iv_imageFile, R.id.iv_zipFile, R.id.iv_apkFile})
    List<ImageView> ivList;
    @InjectViews({R.id.rl_anyFile, R.id.rl_txtFile, R.id.rl_videoFile, R.id.rl_audioFile, R.id.rl_imageFile, R.id.rl_zipFile, R.id.rl_apkFile})
    List<RelativeLayout> rlList;
    String fileType[] = {"所有文件", "文档文件", "视频文件", "音频文件", "图像文件", "压缩文件", "apk文件"};
    //实例化一个文件查询(单例)
    FileManager fileManager;
    //实例化一个线程
    Thread fileManagerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);
        ButterKnife.inject(this);
        initActionBar(true, "文件管理", false, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        checkSelfPermison();

    }

    private void initView() {
        //获取filemanager(单例模式,直接获取对象),并设置监听
        fileManager = FileManager.getFileManager();
        fileManager.setSearchListener(new FileManager.SearchListener() {
            @Override
            public void searching(final long size) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_file_manager.setText("已找到:" + Formatter.formatFileSize(FileManagerActivity.this, size));
                    }
                });
            }

            @Override
            public void end(final boolean endFlag) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //如果查询完毕
                        if (endFlag) {
                            for (int i = 0; i < pbList.size(); i++) {
                                final int temp = i;
                                //将每个进度条设置隐藏
                                pbList.get(i).setVisibility(View.GONE);
                                //将每个小图标设置显示
                                ivList.get(i).setVisibility(View.VISIBLE);
                                //用for循环设置点击事件
                                rlList.get(i).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Bundle bundle = new Bundle();
                                        //将文件类型通过bundle传给下一布局(每个点击事件传的类型都不同)
                                        bundle.putString("fileType", fileType[temp]);
                                        startActivity(FileActivity.class, bundle);
                                    }
                                });

                            }
                            //查询完后,来句吐司提示
                            Toast.makeText(FileManagerActivity.this, "查找完毕", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        //异步操作,也就是开条线程
        asyncSearchSDCardFiles();
    }

    private void checkSelfPermison() {
        if (Build.VERSION.SDK_INT >= 23) {
            int hasGot = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            //判断权限是否允许
            if (hasGot == PackageManager.PERMISSION_GRANTED) {
                initView();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        } else {
            initView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initView();
        } else {
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle("权限获取").setMessage("请从系统获取读取权限").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                }
            }).setNegativeButton("Cancle", null).create();
            dialog.show();
            Toast.makeText(FileManagerActivity.this, "请获取权限后重新进入", Toast.LENGTH_SHORT).show();

        }

    }

    //为了使文件删除后,返回总文件大小显示界面显示正确的大小数据,需要在onResume生命周期时,重新刷新数据
    @Override
    protected void onResume() {
        super.onResume();
        tv_file_manager.setText("已找到:" + Formatter.formatFileSize(FileManagerActivity.this, FileManager.getFileManager().getAnyFileSize()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        int hasGot = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasGot == PackageManager.PERMISSION_GRANTED) {
            //打断线程(之后不运行)
            fileManagerThread.interrupt();
            //将线程设置为空,与之前线程脱离关系
            fileManagerThread = null;
            //将查找状态重置
            fileManager.isSearching = false;
        }
    }

    public void asyncSearchSDCardFiles() {
        //开启一条子线程
        fileManagerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //查询SD卡中的文件(耗时操作,需放在子线程中)
                fileManager.searchSDCardFile();
            }
        });
        //开启线程
        fileManagerThread.start();
    }

}
