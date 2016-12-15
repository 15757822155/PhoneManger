package com.zhuoxin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhuoxin.R;

public class ActionBarView extends LinearLayout {
    ImageView iv_back;
    ImageView iv_menu;
    TextView tv_title;

    public ActionBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //需要先把ActionBar的布局引入过来
        inflate(context, R.layout.layout_actionbar, this);
        this.setBackgroundResource(R.drawable.shape_blue);
        this.setElevation(10);
        //找到对应的View
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_menu = (ImageView) findViewById(R.id.iv_menu);
        tv_title = (TextView) findViewById(R.id.tv_title);
    }

    //添加一个初始化ActionBar的方法
    public void initActionBar(boolean hasBack, String title, boolean hasMenu, OnClickListener listener) {
        if (hasBack) { //显示是否有返回键
            iv_back.setOnClickListener(listener);
        } else {
            iv_back.setVisibility(View.INVISIBLE);
        }
        tv_title.setText(title);
        if (hasMenu) {
            iv_menu.setOnClickListener(listener);
        } else {
            iv_menu.setVisibility(View.INVISIBLE);
        }
    }
}
