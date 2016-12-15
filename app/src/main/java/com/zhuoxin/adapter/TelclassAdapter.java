package com.zhuoxin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhuoxin.R;
import com.zhuoxin.base.MyBaseAdapter;
import com.zhuoxin.entity.TelClassInfo;

import java.util.List;

public class TelclassAdapter extends MyBaseAdapter<TelClassInfo> {

    public TelclassAdapter(List<TelClassInfo> dateList, Context context) {
        super(dateList, context);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            view = inflater.inflate(R.layout.item_telclasslist, null);//填充布局
            viewHolder = new ViewHolder();
            viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        TelClassInfo telclassinfo = (TelClassInfo) getItem(i);
        viewHolder.tv_name.setText(telclassinfo.name);
        return view;
    }

    public class ViewHolder {
        TextView tv_name;
    }
}
