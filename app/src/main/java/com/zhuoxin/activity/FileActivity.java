package com.zhuoxin.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.zhuoxin.R;
import com.zhuoxin.adapter.FileAdapter;
import com.zhuoxin.base.ActionBarActivity;
import com.zhuoxin.biz.FileManager;
import com.zhuoxin.entity.FileInfo;
import com.zhuoxin.utils.FileTypeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FileActivity extends ActionBarActivity {
    List<FileInfo> fileInfoList;
    String fileType;
    @InjectView(R.id.lv_file)
    ListView lv_file;
    FileAdapter adapter;
    @InjectView(R.id.btn_file_delete)
    Button btn_file_delete;
    FileManager fm = FileManager.getFileManager();

    //实例化一个线程
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        ButterKnife.inject(this);
        //获取从上一布局传入的文件类型
        Bundle bundle = getIntent().getBundleExtra("bundle");
        fileType = bundle.getString("fileType");
        initActionBar(true, fileType, false, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getFileList();
        adapter = new FileAdapter(fileInfoList, this);
        lv_file.setAdapter(adapter);
        //对查找到的每一个文件设置点击事件
        lv_file.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //通过文件类型工具类,获得每个文件的类型
                String mime = FileTypeUtil.getMIMEType(fileInfoList.get(i).getFile());
                Intent intent = new Intent();
                //弹出一个页面
                intent.setAction(Intent.ACTION_VIEW);
                //根据文件类型,跳转到各类型对应的程序
                intent.setDataAndType(Uri.fromFile(fileInfoList.get(i).getFile()), mime);
                startActivity(intent);
            }
        });
        lv_file.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                switch (i) {
                    case SCROLL_STATE_IDLE:
                        adapter.isScroll = false;
                        adapter.notifyDataSetChanged();
                        break;
                    case SCROLL_STATE_FLING:
                    case SCROLL_STATE_TOUCH_SCROLL:
                        adapter.isScroll = true;
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
        //删除操作
        btn_file_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //建立一个List,复制手机中的List(原因是防止空指针)
                List<FileInfo> tempList = new ArrayList<FileInfo>();
                //添加数据(getData是adapter我们自己写的获取列表的方法)
                tempList.addAll(adapter.getData());
                //使用for循环做删除操作
                for (FileInfo f : tempList) {
                    //如果该文件是选中状态
                    if (f.isSelect()) {
                        //移除总文件列表中该条数据
                        fm.getAnyFileList().remove(f);
                        //根据不同文件类型,移除对应各类型列表数据
                        switch (f.getFileType()) {
                            case FileTypeUtil.TYPE_VIDEO:
                                fm.getVideoFileList().remove(f);
                                break;
                            case FileTypeUtil.TYPE_AUDIO:
                                fm.getAudioFileList().remove(f);
                                break;
                            case FileTypeUtil.TYPE_TXT:
                                fm.getTxtFileList().remove(f);
                                break;
                            case FileTypeUtil.TYPE_IMAGE:
                                fm.getImageFileList().remove(f);
                                break;
                            case FileTypeUtil.TYPE_ZIP:
                                fm.getZipFileList().remove(f);
                                break;
                            case FileTypeUtil.TYPE_APK:
                                fm.getApkFileList().remove(f);
                                break;
                        }
                        //获取总文件的大小
                        long totalSize = fm.getAnyFileSize();
                        //设置文件删除后总文件大小
                        fm.setAnyFileSize(totalSize - f.getFile().length());
                        //在删除列表数据后,还要删除该条数据,否则,在重新查找后,会发现该文件还在
                        f.getFile().delete();
                    }

                }
                //通知数据改变
                adapter.notifyDataSetChanged();
                //吐司删除成功
                Toast.makeText(FileActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void getFileList() {
        //根据文件类型,获得类型的具体文件
        switch (fileType) {
            case "文档文件":
                fileInfoList = FileManager.getFileManager().getTxtFileList();
                break;
            case "视频文件":
                fileInfoList = FileManager.getFileManager().getVideoFileList();
                break;
            case "音频文件":
                fileInfoList = FileManager.getFileManager().getAudioFileList();
                break;
            case "图像文件":
                fileInfoList = FileManager.getFileManager().getImageFileList();
                break;
            case "压缩文件":
                fileInfoList = FileManager.getFileManager().getZipFileList();
                break;
            case "apk文件":
                fileInfoList = FileManager.getFileManager().getApkFileList();
                break;
            default:
                fileInfoList = FileManager.getFileManager().getAnyFileList();
                break;
        }
    }
}
