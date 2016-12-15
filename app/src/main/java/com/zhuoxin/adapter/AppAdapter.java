package com.zhuoxin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuoxin.R;
import com.zhuoxin.base.MyBaseAdapter;
import com.zhuoxin.entity.AppInfo;

import java.util.List;

public class AppAdapter extends MyBaseAdapter<AppInfo> {
    public AppAdapter(List dateList, Context context) {
        super(dateList, context);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_software, null);
            holder = new ViewHolder();
            holder.appversion = (TextView) view.findViewById(R.id.appversion);
            holder.packagename = (TextView) view.findViewById(R.id.packagename);
            holder.appicon = (ImageView) view.findViewById(R.id.iv_appicon);
            holder.appname = (TextView) view.findViewById(R.id.appname);
            holder.cb_delete = (CheckBox) view.findViewById(R.id.cb_delete);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.cb_delete.setTag(i);
        AppInfo appInfo = getItem(i);
        holder.appname.setText(appInfo.appname);
        holder.packagename.setText(appInfo.packagename);
        holder.appversion.setText(appInfo.appversion);
        //设置图标
        holder.appicon.setImageDrawable(appInfo.appicon);
        //如果是系统的app就禁用checkbox
        if (getItem(i).isSystem) {
            holder.cb_delete.setClickable(false);
        }else{
            holder.cb_delete.setClickable(true);
        }
        holder.cb_delete.setChecked(appInfo.isDelete);
        holder.cb_delete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //ListView 图片错位,CheckBox错位
                int index = (int) holder.cb_delete.getTag();
                getItem(index).isDelete = b;
            }
        });
        return view;
    }

    private static class ViewHolder {
        TextView appname;//注意这里不要定义成String
        ImageView appicon;
        TextView packagename;
        TextView appversion;
        CheckBox cb_delete;
    }
}
