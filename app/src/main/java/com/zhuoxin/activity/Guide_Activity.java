package com.zhuoxin.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuoxin.R;
import com.zhuoxin.adapter.PagerGuideAdapter;
import com.zhuoxin.base.BaseActivity;
import com.zhuoxin.db.DBManager;
import com.zhuoxin.service.MusicService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Guide_Activity extends BaseActivity {
    ViewPager vp_guide;
    ImageView iv_circle_red;
    float pixelWidth;
    TextView tv_ship;
    List<Integer> idList;
    boolean isFromSettings = false;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_);
        bundle = getIntent().getBundleExtra("bundle");
        if (bundle != null) {
            isFromSettings = bundle.getBoolean("isFromSettings", false);
        }
        boolean isFirstRun = getSharedPreferences("config", Context.MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFromSettings || isFirstRun) {
            initData();//设置数据
            initView();//设置监听事件，点击事件
        } else {
            startActivity(HomeActivity.class);
            finish();
        }
    }

    private void initView() {
        vp_guide.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //滑动时的状态
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                iv_circle_red.setPadding((int) (position * pixelWidth + pixelWidth * positionOffset), 0, 0, 0);
            }

            //滑动完成，页面选择后的状态
            @Override
            public void onPageSelected(int position) {
                if (position == idList.size() - 1) {
                    tv_ship.setVisibility(View.VISIBLE);
                } else {
                    tv_ship.setVisibility(View.INVISIBLE);
                }
            }

            //滑动状态改变
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //ship点击事件
        tv_ship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //保存是否是第一次运行程序
                getSharedPreferences("config", Context.MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();
                copyFile();
                if (isFromSettings) {
                    finish();
                } else {
                    startActivity(HomeActivity.class);
                    finish();
                }

            }
        });
        startService(MusicService.class);
    }

    private void initData() {
        pixelWidth = getDensity() * 40;
        vp_guide = (ViewPager) findViewById(R.id.vp_guide);
        tv_ship = (TextView) findViewById(R.id.tv_ship);
        idList = new ArrayList<Integer>();
        idList.add(R.drawable.pager_guide1);
        idList.add(R.drawable.pager_guide2);
        idList.add(R.drawable.pager_guide3);
        PagerGuideAdapter adapter = new PagerGuideAdapter(this, idList);//创建适配器
        vp_guide.setAdapter(adapter);//设置适配器
        iv_circle_red = (ImageView) findViewById(R.id.iv_circle_red);
    }

    private void copyFile() {
        File file = this.getFilesDir();//直接获取手机中的file文件夹目录
        File targetFile = new File(file, "commonnum.db");
        if (!DBManager.isExistsDB(targetFile)) {
            DBManager.copyAssetsFileToFile(this, "commonnum.db", targetFile);
        }

    }

    @Override
    public void finish() {
        stopService(MusicService.class);
        super.finish();
    }

    private float getDensity() {
        //新建了一个尺寸信息（显示度量）
        DisplayMetrics metrics = new DisplayMetrics();
        //获取当前设备的信息（获取设备管理.默认显示.度量（传到位置））
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //返回密度信息（密度1.5x）
        return metrics.density;
    }
}
