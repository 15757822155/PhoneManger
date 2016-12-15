package com.zhuoxin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.zhuoxin.R;
import com.zhuoxin.adapter.TelclassAdapter;
import com.zhuoxin.base.ActionBarActivity;
import com.zhuoxin.base.BaseActivity;
import com.zhuoxin.db.DBManager;
import com.zhuoxin.entity.TelClassInfo;
import java.io.File;
import java.util.List;

public class PhoneActivity extends ActionBarActivity {
    ListView lv_item;
    TelclassAdapter adapter;
    List<TelClassInfo> telclassinfoList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        initData();//设置数据
        initView();//单击事件
        initActionBar(true, "手机通讯录", false, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void initData(){
        lv_item = (ListView) findViewById(R.id.lv_item);
        File file = this.getFilesDir();
        File targetFile = new File(file, "commonnum.db");
        telclassinfoList = DBManager.readTelclassinfo(targetFile);
        adapter = new TelclassAdapter(telclassinfoList, this);
        lv_item.setAdapter(adapter);
    }
    private void initView(){
        //设置单击事件
        lv_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /*
                Intent intent = new Intent(PhoneActivity.this, PhoneNumActivity.class);
                String title = telclassinfoList.get(i).name;
                int idx = telclassinfoList.get(i).idx;
                intent.putExtra("idx",idx);
                intent.putExtra("title",title);
                startActivity(intent);*/
                Bundle bundle =new Bundle();
                bundle.putInt("idx",telclassinfoList.get(i).idx);
                bundle.putString("title",telclassinfoList.get(i).name);
                startActivity(PhoneNumActivity.class,bundle);
            }
        });
    }
}
