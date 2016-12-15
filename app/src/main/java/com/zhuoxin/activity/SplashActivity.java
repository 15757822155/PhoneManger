package com.zhuoxin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.zhuoxin.R;

import java.io.File;

import com.zhuoxin.base.BaseActivity;
import com.zhuoxin.db.DBManager;

public class SplashActivity extends BaseActivity {
    ImageView tv_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startAnimation();
        copyFile();
    }
    private void startAnimation(){
        tv_message = (ImageView) findViewById(R.id.iv_active);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_set);
        //新建监听事件
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            //动画结束时
            @Override
            public void onAnimationEnd(Animation animation) {
                Toast.makeText(SplashActivity.this, "跳转到主页面", Toast.LENGTH_SHORT).show();
                startActivity(PhoneActivity.class);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        animation.setAnimationListener(animationListener);
        tv_message.startAnimation(animation);
    }
    private void copyFile() {
        File file = this.getFilesDir();//直接获取手机中的file文件夹目录
        File targetFile = new File(file, "commonnum.db");
        if (!DBManager.isExistsDB(targetFile)) {
            DBManager.copyAssetsFileToFile(this, "commonnum.db", targetFile);
        }

    }
}
