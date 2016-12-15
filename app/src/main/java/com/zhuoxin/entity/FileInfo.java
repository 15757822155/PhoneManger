package com.zhuoxin.entity;

import java.io.File;


public class FileInfo {
    private File file;//原始文件
    private String iconName;//文件的图标资源
    private String fileType;//文件的类型
    private boolean isSelect;//添加标记是否选择,删除

    /**
     * 构造函数
     *
     * @param file
     * @param fileType
     * @param iconName
     */
    public FileInfo(File file,  String iconName,String fileType) {
        this.file = file;
        this.fileType = fileType;
        this.iconName = iconName;
        this.isSelect=false;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
