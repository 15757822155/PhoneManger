package com.zhuoxin.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zhuoxin.R;

import java.util.ArrayList;
import java.util.List;

public class PagerGuideAdapter extends PagerAdapter {
    List<Integer> idList = new ArrayList<Integer>();
    LayoutInflater inflater;

    public PagerGuideAdapter(Context context, List<Integer> idList) {
        this.idList = idList;
        inflater = LayoutInflater.from(context);
    }

    //返回view的总数
    @Override
    public int getCount() {
        return idList.size();
    }

    //创建一个View，并对它的key进行设置返回
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //创建了一个View
        View view = inflater.inflate(R.layout.item_pager_guild, null);
        ImageView iv_guide = (ImageView) view.findViewById(R.id.iv_guide);
        iv_guide.setImageResource(idList.get(position));
        //添加到容器中，负责对view的判断与显示
        container.addView(view);
        //把当前的view当成key返回
        return view;
    }

    //判断当前页的键值是否相同
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
