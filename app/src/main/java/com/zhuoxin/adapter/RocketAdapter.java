package com.zhuoxin.adapter;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuoxin.R;
import com.zhuoxin.base.MyBaseAdapter;

import java.util.List;

public class RocketAdapter extends MyBaseAdapter<ActivityManager.RunningAppProcessInfo> {
    public RocketAdapter(List<ActivityManager.RunningAppProcessInfo> dateList, Context context) {
        super(dateList, context);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_software, null);
            holder = new ViewHolder();
            holder.iv_icon = (ImageView) view.findViewById(R.id.iv_appicon);
            holder.tv_appname = (TextView) view.findViewById(R.id.appname);
            holder.tv_packagename = (TextView) view.findViewById(R.id.packagename);
            holder.tv_version = (TextView) view.findViewById(R.id.appversion);
            holder.cb_delete = (CheckBox) view.findViewById(R.id.cb_delete);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //将每个程序前的复选框和版本号隐藏
        holder.tv_version.setVisibility(View.GONE);
        holder.cb_delete.setVisibility(View.GONE);
        try {
            //对每个程序的图标,名字,包名赋值
            holder.iv_icon.setImageDrawable(context.getPackageManager().getApplicationIcon(getItem(i).processName));
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(getItem(i).processName, PackageManager.MATCH_UNINSTALLED_PACKAGES);
            holder.tv_appname.setText(context.getPackageManager().getApplicationLabel(applicationInfo));
            holder.tv_packagename.setText(getItem(i).processName);
        } catch (PackageManager.NameNotFoundException e) {
            //若找不到,则赋默认值
            holder.iv_icon.setImageResource(R.drawable.item_arrow_right);
            holder.tv_packagename.setText(getItem(i).processName);
            holder.tv_appname.setText("未知程序");
        }
        return view;
    }

    static class ViewHolder {
        TextView tv_appname;
        TextView tv_packagename;
        ImageView iv_icon;
        TextView tv_version;
        CheckBox cb_delete;
    }
}
