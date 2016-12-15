package com.zhuoxin.entity;

import android.graphics.drawable.Drawable;

public class AppInfo {
    public String appname;
    //是否是系统
    public boolean isSystem;
    public Drawable appicon;
    public String packagename;
    public String appversion;
    //是否删除
    public boolean isDelete;

    //构造函数里,将一些用到的参数全部传进去
    public AppInfo(Drawable appicon, String appname, String packagename, String appversion, boolean isSystem, boolean isDelete) {
        this.appicon = appicon;
        this.appname = appname;
        this.isSystem = isSystem;
        this.packagename = packagename;
        this.appversion = appversion;
        //如果是系统软件就不能被删除
        if (isSystem) {
            this.isDelete = false;
        } else {
            this.isDelete = isDelete;
        }
    }
}
