package com.zhuoxin.base;

import android.os.Bundle;
import android.view.View;

import com.zhuoxin.R;
import com.zhuoxin.view.ActionBarView;

public class ActionBarActivity extends BaseActivity {
    ActionBarView actionBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void initActionBar(boolean hasBack, String title, boolean hasMenu, View.OnClickListener listener) {
        //这里要求那些继承自ActionBarActivity的子类必须有R.id.actionbar
        actionBarView = (ActionBarView) findViewById(R.id.actionbar);
        actionBarView.initActionBar(hasBack, title, hasMenu, listener);
    }
}
