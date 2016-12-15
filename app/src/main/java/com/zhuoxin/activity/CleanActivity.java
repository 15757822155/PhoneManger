package com.zhuoxin.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.zhuoxin.R;
import com.zhuoxin.adapter.FileAdapter;
import com.zhuoxin.base.ActionBarActivity;
import com.zhuoxin.biz.MemoryManager;
import com.zhuoxin.db.DBManager;
import com.zhuoxin.entity.FileInfo;
import com.zhuoxin.utils.FileTypeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class CleanActivity extends ActionBarActivity {
    @InjectView(R.id.lv_clean)
    ListView lv_clean;
    List<String> filePathList = new ArrayList<String>();
    FileAdapter adapter;
    List<FileInfo> fileInfoList = new ArrayList<FileInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean);
        ButterKnife.inject(this);
        initActionBar(true, "手机清理", false, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initData();
    }

    private void initData() {
        File targetFile = new File(getFilesDir(), "clearpath.db");
        filePathList.addAll(DBManager.getFilePath(targetFile));
        adapter = new FileAdapter(fileInfoList, this);
        for (String path : filePathList) {
            File appFile = new File(MemoryManager.getPhoneInSDCardPath() + path);
            if (appFile.exists()) {
                FileInfo fileInfo = new FileInfo(appFile, "icon_file", FileTypeUtil.TYPE_ANY);
                adapter.getData().add(fileInfo);
            }
        }
        lv_clean.setAdapter(adapter);
    }

    @OnClick(R.id.btn_clean)
    public void onClick(View view) {
        List<FileInfo> tempList = new ArrayList<FileInfo>();
        tempList.addAll(adapter.getData());
        for (int i = 0; i < tempList.size(); i++) {
            if (tempList.get(i).isSelect()) {
                adapter.getData().remove(i);
                deleteFile(tempList.get(i).getFile());
                Toast.makeText(this,"删除成功",Toast.LENGTH_SHORT).show();
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void deleteFile(File file) {
        if (file.isDirectory()) {
            File files[] = file.listFiles();
            if (files != null) {
                if (files.length <= 0) {
                    file.delete();
                    return;
                } else {
                    for (File f : files) {
                        deleteFile(f);
                    }
                }
            }
            file.delete();
        } else {
            file.delete();
            return;
        }
    }
}
