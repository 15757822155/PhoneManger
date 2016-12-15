package com.zhuoxin.base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
public class BaseActivity extends AppCompatActivity {
    public void startActivity(Class targetClass){
        Intent intent = new Intent(this,targetClass);
        startActivity(intent);
    }
    public void startActivity(Class targetClass, Bundle bundle){
        Intent intent = new Intent(this,targetClass);
        intent.putExtra("bundle",bundle);
        startActivity(intent);
    }
    public void startActivity(String action, Uri data){
        Intent intent = new Intent();
        intent.setAction(action);
        intent.setData(data);
        startActivity(intent);
    }
    public void startService(Class targetClass){
        Intent intent = new Intent(this,targetClass);
        startService(intent);
    }
    public void stopService(Class targetClass){
        Intent intent = new Intent(this,targetClass);
        stopService(intent);
    }
}
