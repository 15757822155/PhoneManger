package com.zhuoxin.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zhuoxin.entity.TelClassInfo;
import com.zhuoxin.entity.TelNumberInfo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    public static void copyAssetsFileToFile(Context context, String assetsPath, File targetFile) {
        InputStream is = null;
        BufferedInputStream bis = null;
        OutputStream os = null;
        BufferedOutputStream bos = null;
        try {
            is = context.getAssets().open(assetsPath);//打开资源文件夹，并打开我们想要的数据库文件
            bis = new BufferedInputStream(is);
            os = new FileOutputStream(targetFile);
            bos = new BufferedOutputStream(os);
            byte b[] = new byte[1024];
            int count = 0;
            while ((count = bis.read(b)) != -1) {
                bos.write(b, 0, count);
            }
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
                os.close();
                bis.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static boolean isExistsDB(File targetFile) {
        if (targetFile.exists() && targetFile.length() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public static List<TelClassInfo> readTelclassinfo(File targetFile) {
        List<TelClassInfo> telclassinfos = new ArrayList<TelClassInfo>();//用于保存检索到的数据
        //创建数据库
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(targetFile, null);
        Cursor cursor = sqLiteDatabase.rawQuery("select * from classlist", null);//输入检索语句
        while (cursor.moveToNext()) {//假如有下一行，移动到下一行
            String name = cursor.getString(cursor.getColumnIndex("name"));//获取列的索引
            int idx = cursor.getInt(cursor.getColumnIndex("idx"));
            TelClassInfo telclassinfo = new TelClassInfo(name, idx);//实例化TelClassInfo保存，获取到的数据
            telclassinfos.add(telclassinfo);//将对象添加到列表中
        }
        return telclassinfos;
    }

    public static List<TelNumberInfo> readTelNumberInfo(File targetFile, int idx) {
        List<TelNumberInfo> telNumberInfoList = new ArrayList<TelNumberInfo>();
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(targetFile, null);
        Cursor cursor = sqLiteDatabase.rawQuery("select * from table" + idx, null);//输入检索语句
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String number = cursor.getString(cursor.getColumnIndex("number"));
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            TelNumberInfo info = new TelNumberInfo(_id, name, number);
            telNumberInfoList.add(info);
        }
        return telNumberInfoList;
    }

    //取出数据库中app路径
    public static List<String> getFilePath(File targetFile) {
        List<String> filePathList = new ArrayList<String>();
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(targetFile, null);
        Cursor cursor = sqLiteDatabase.rawQuery("select * from softdetail", null);
        while (cursor.moveToNext()) {
            String filePath = cursor.getString(cursor.getColumnIndex("filepath"));
            filePathList.add(filePath);
        }
        return filePathList;
    }

}
