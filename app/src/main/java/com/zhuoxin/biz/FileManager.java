package com.zhuoxin.biz;

import com.zhuoxin.entity.FileInfo;
import com.zhuoxin.utils.FileTypeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于查找文件,用单例模式
 * Created by Administrator on 2016/11/22.
 */

public class FileManager {
    //单例模式,饿汉式
    private static FileManager fileManager = new FileManager();

    private FileManager() {
    }

    public static FileManager getFileManager() {
        return fileManager;
    }

    //内置,外置路径
    public static File inSDCardDir = null;
    public static File outSDCardDir = null;

    //静态代码块,只运行一次,只会赋值一次
    static {
        if (MemoryManager.getPhoneInSDCardPath() != null) {
            inSDCardDir = new File(MemoryManager.getPhoneInSDCardPath());
        }
        if (MemoryManager.getPhoneOutSDCardPath() != null) {
            outSDCardDir = new File(MemoryManager.getPhoneOutSDCardPath());
        }

    }

    //设置查找状态,判断是否停止查询
    public boolean isSearching = false;
    //建立所有文件的List,存储所有查到文件,文件的总大小
    List<FileInfo> anyFileList = new ArrayList<FileInfo>();
    long anyFileSize = 0;
    List<FileInfo> txtFileList = new ArrayList<FileInfo>();
    long txtFileSize = 0;
    List<FileInfo> videoFileList = new ArrayList<FileInfo>();
    long videoFileSize = 0;
    List<FileInfo> audioFileList = new ArrayList<FileInfo>();
    long audioFileSize = 0;
    List<FileInfo> imageFileList = new ArrayList<FileInfo>();
    long imageFileSize = 0;
    List<FileInfo> zipFileList = new ArrayList<FileInfo>();
    long zipFileSize = 0;
    List<FileInfo> apkFileList = new ArrayList<FileInfo>();
    long apkFileSize = 0;

    public List<FileInfo> getAnyFileList() {
        return anyFileList;
    }

    public void setAnyFileList(List<FileInfo> anyFileList) {
        this.anyFileList = anyFileList;
    }

    public long getAnyFileSize() {
        return anyFileSize;
    }

    public void setAnyFileSize(long anyFileSize) {
        this.anyFileSize = anyFileSize;
    }

    public List<FileInfo> getApkFileList() {
        return apkFileList;
    }

    public List<FileInfo> getAudioFileList() {
        return audioFileList;
    }

    public List<FileInfo> getImageFileList() {
        return imageFileList;
    }

    public List<FileInfo> getTxtFileList() {
        return txtFileList;
    }

    public List<FileInfo> getVideoFileList() {
        return videoFileList;
    }

    public List<FileInfo> getZipFileList() {
        return zipFileList;
    }

    public void setApkFileList(List<FileInfo> apkFileList) {
        this.apkFileList = apkFileList;
    }

    public void setApkFileSize(long apkFileSize) {
        this.apkFileSize = apkFileSize;
    }

    public void setAudioFileList(List<FileInfo> audioFileList) {
        this.audioFileList = audioFileList;
    }

    public void setAudioFileSize(long audioFileSize) {
        this.audioFileSize = audioFileSize;
    }

    public void setImageFileList(List<FileInfo> imageFileList) {
        this.imageFileList = imageFileList;
    }

    public void setImageFileSize(long imageFileSize) {
        this.imageFileSize = imageFileSize;
    }

    public void setTxtFileList(List<FileInfo> txtFileList) {
        this.txtFileList = txtFileList;
    }

    public void setTxtFileSize(long txtFileSize) {
        this.txtFileSize = txtFileSize;
    }

    public void setVideoFileList(List<FileInfo> videoFileList) {
        this.videoFileList = videoFileList;
    }

    public void setVideoFileSize(long videoFileSize) {
        this.videoFileSize = videoFileSize;
    }

    public void setZipFileList(List<FileInfo> zipFileList) {
        this.zipFileList = zipFileList;
    }

    public void setZipFileSize(long zipFileSize) {
        this.zipFileSize = zipFileSize;
    }

    public void initData() {
        //清空所有文件列表的数据
        if (anyFileList.size() > 0) {
            anyFileList.clear();
            anyFileSize = 0;
            txtFileList.clear();
            txtFileSize = 0;
            videoFileList.clear();
            videoFileSize = 0;
            audioFileList.clear();
            audioFileSize = 0;
            imageFileList.clear();
            imageFileSize = 0;
            zipFileList.clear();
            zipFileSize = 0;
            apkFileList.clear();
            apkFileSize = 0;
        }
    }

    //查找文件
    public void searchSDCardFile() {
        if (isSearching) {
            //如果正在查找什么都不做
            return;
        } else {
            //修改查找状态
            isSearching = true;
            //判断anyFileList是否有内容,有就清空
            initData();
        }
        //查找内置外置SD卡(boolean表示是否查询完毕)
        searchFile(inSDCardDir, true);
        searchFile(outSDCardDir, false);

    }

    //根据指定文件夹查找内容
    public void searchFile(File file, boolean endFlag) {
        //判断文件是否合法
        if (file == null || !file.canRead() || !file.exists()) {
            return;
        }
        //递归查找文件,文件夹>>递归,文件--执行操作
        if (file.isDirectory()) {
            File files[] = file.listFiles();
            if (files != null || files.length > 0) {
                for (File f : files) {
                    searchFile(f, false);
                }
            } else {
                return;
            }
            //结束时调用
            if (endFlag) {
                //结束时修改寻找状态
                isSearching = false;
                if (listener != null) {
                    listener.end(true);
                }
            }
        } else {
            //调用文件类型工具包,存储图标名字,和文件类型
            String iconAndType[] = FileTypeUtil.getFileIconAndTypeName(file);
            //new FileInfo,存储文件大小
            FileInfo fileInfo = new FileInfo(file, iconAndType[0], iconAndType[1]);
            anyFileList.add(fileInfo);
            anyFileSize += file.length();
            //判断文件类型并给不同类型的列表赋值
            if (fileInfo.getFileType().equals(FileTypeUtil.TYPE_TXT)) {
                txtFileList.add(fileInfo);
                txtFileSize += file.length();
            } else if (fileInfo.getFileType().equals(FileTypeUtil.TYPE_VIDEO)) {
                videoFileList.add(fileInfo);
                videoFileSize += file.length();
            } else if (fileInfo.getFileType().equals(FileTypeUtil.TYPE_AUDIO)) {
                audioFileList.add(fileInfo);
                audioFileSize += file.length();
            } else if (fileInfo.getFileType().equals(FileTypeUtil.TYPE_IMAGE)) {
                imageFileList.add(fileInfo);
                imageFileSize += file.length();
            } else if (fileInfo.getFileType().equals(FileTypeUtil.TYPE_ZIP)) {
                zipFileList.add(fileInfo);
                zipFileSize += file.length();
            } else if (fileInfo.getFileType().equals(FileTypeUtil.TYPE_APK)) {
                apkFileList.add(fileInfo);
                apkFileSize += file.length();
            }
            //调用查找的接口方法(将查到的文件总大小传入)
            if (listener != null) {
                listener.searching(anyFileSize);
            }

        }
    }

    //回调接口
    public interface SearchListener {
        void searching(long size);

        void end(boolean endFlag);
    }

    //定义接口对象
    public SearchListener listener = null;

    //定义设置接口的方法
    public void setSearchListener(SearchListener listener) {
        this.listener = listener;
    }
}
