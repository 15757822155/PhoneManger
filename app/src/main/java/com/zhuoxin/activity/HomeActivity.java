package com.zhuoxin.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuoxin.R;
import com.zhuoxin.base.ActionBarActivity;
import com.zhuoxin.biz.MemoryManager;
import com.zhuoxin.db.DBManager;
import com.zhuoxin.view.CleanCircleView;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zhuoxin.view.CleanCircleView.isRunning;

public class HomeActivity extends ActionBarActivity implements View.OnClickListener {
    TextView tv_telmgr;
    TextView tv_softmgr;
    TextView tv_rocket;
    CleanCircleView ccv_home;
    ImageView iv_clean_home;
    TextView tv_clean_home;
    TextView tv_phonemgr;
    TextView tv_filemgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.inject(this);
        tv_telmgr = (TextView) findViewById(R.id.tv_telmgr);
        tv_softmgr = (TextView) findViewById(R.id.tv_softmgr);
        tv_rocket = (TextView) findViewById(R.id.tv_rocket);
        ccv_home = (CleanCircleView) findViewById(R.id.ccv_home);
        iv_clean_home = (ImageView) findViewById(R.id.iv_clean_home);
        tv_clean_home = (TextView) findViewById(R.id.tv_clean_home);
        tv_phonemgr = (TextView) findViewById(R.id.tv_phonemgr);
        tv_filemgr = (TextView) findViewById(R.id.tv_filemgr);
        tv_filemgr.setOnClickListener(this);
        tv_phonemgr.setOnClickListener(this);
        iv_clean_home.setOnClickListener(this);
        tv_rocket.setOnClickListener(this);
        tv_softmgr.setOnClickListener(this);
        tv_telmgr.setOnClickListener(this);
        initActionBar(false, "手机管家", true, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        tv_clean_home.setText(MemoryManager.usedPercent(this) + "%");
        int targetAngle = (int) (3.6 * MemoryManager.usedPercent(this));
        ccv_home.setTargetAngle(targetAngle);
    }

    @OnClick(R.id.tv_sdclean)
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.iv_menu:
                startActivity(SettingsActivity.class);
                break;
            case R.id.tv_telmgr:
                startActivity(PhoneActivity.class);
                break;
            case R.id.tv_softmgr:
                startActivity(SoftManagerActivity.class);
                break;
            case R.id.tv_rocket:
                startActivity(RocketActivity.class);
                break;
            case R.id.tv_phonemgr:
                startActivity(PhoneStateActivity.class);
                break;
            case R.id.tv_filemgr:
                startActivity(FileManagerActivity.class);
                break;
            case R.id.tv_sdclean:
                File targetFile = new File(this.getFilesDir(), "clearpath.db");
                if (!DBManager.isExistsDB(targetFile)) {
                    DBManager.copyAssetsFileToFile(this, "clearpath.db", targetFile);
                }
                startActivity(CleanActivity.class);
            case R.id.iv_clean_home:
                if (!isRunning) {
                    MemoryManager.killBackgroundProcessesRAM(this);
                    int targetAngle = (int) (3.6 * MemoryManager.usedPercent(this));
                    tv_clean_home.setText(MemoryManager.usedPercent(this) + "%");
                    isRunning = true;
                    ccv_home.setTargetAngle(targetAngle);
                }
                break;
        }
    }
}
