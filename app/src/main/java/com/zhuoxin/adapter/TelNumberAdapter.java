package com.zhuoxin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhuoxin.R;
import com.zhuoxin.base.MyBaseAdapter;
import com.zhuoxin.entity.TelNumberInfo;

import java.util.List;

public class TelNumberAdapter extends MyBaseAdapter<TelNumberInfo> {
    public TelNumberAdapter(List<TelNumberInfo> dateList, Context context) {
        super(dateList, context);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view==null){
            view = inflater.inflate(R.layout.item_telnumberlist,null);
            holder = new ViewHolder();
            holder.tv_telname = (TextView) view.findViewById(R.id.tv_telname);
            holder.tv_telnumber = (TextView) view.findViewById(R.id.tv_telnumber);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        TelNumberInfo info = getItem(i);
        holder.tv_telnumber.setText(info.number);
        holder.tv_telname.setText(info.name);
        return view;
    }
 private class ViewHolder{
    TextView tv_telnumber;
     TextView tv_telname;
 }
}
