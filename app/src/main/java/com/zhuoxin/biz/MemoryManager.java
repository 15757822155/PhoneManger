package com.zhuoxin.biz;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.text.format.Formatter;

import com.zhuoxin.activity.RocketActivity;
import com.zhuoxin.adapter.RocketAdapter;
import com.zhuoxin.process.ProcessManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/21.
 * 这个类主要用于获取手机RAM的工具包
 *
 * @author 杨再光
 * @version 1.0
 * @since 2016年11月21日10:23:45
 */

public class MemoryManager {
    /**
     * 这个方法主要用于获取手机中内存信息(MenoryInfo)
     *
     * @param context 上下文环境
     * @return 返回的是运存信息
     */
    private static ActivityManager.MemoryInfo getMemoryInfo(Context context) {
        //获取手机运行内存
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    /**
     * 这个方法用于获取手机总运存保存的是bit类型(long)
     *
     * @param context 上下文环境
     * @return long类型手机总运存
     */
    public static long totalRAMLong(Context context) {
        return getMemoryInfo(context).totalMem;
    }

    /**
     * 这个方法用于获取格式化后的手机总运存(String)
     *
     * @param context
     * @return String类型手机总运存
     */
    public static String totalRAMString(Context context) {
        return Formatter.formatFileSize(context, totalRAMLong(context));
    }

    /**
     * 这个方法主要用于获取当前手机可用运存(long)
     *
     * @param context
     * @return long类型的可用运存
     */
    public static long availableRAMLong(Context context) {
        return getMemoryInfo(context).availMem;
    }

    /**
     * 这个方法用于获取格式化后的手机当前可用运存(String)
     *
     * @param context
     * @return String类型手机当前可用运存
     */
    public static String availableRAMString(Context context) {
        return Formatter.formatFileSize(context, availableRAMLong(context));
    }

    /**
     * 这个方法用于获取手机当前已用运存(long)
     *
     * @param context
     * @return long类型手机当前已用运存
     */
    public static long usedRAMLong(Context context) {
        return totalRAMLong(context) - availableRAMLong(context);
    }

    /**
     * 这个方法用于获取手机当前已用运存百分数(int)
     *
     * @param context
     * @return int类型手机当前已用运存百分数
     */
    public static int usedPercent(Context context) {
        return (int) (100.0 * usedRAMLong(context) / totalRAMLong(context));
    }

    /**
     * 这个方法用于获取手机当前进程(List)
     *
     * @param context
     * @return List<RunningAppProcessesInfo>类型手机当前非系统进程
     */
    public static List<ActivityManager.RunningAppProcessInfo> getRunningAppProcessesInfo(Context context) {
        List<ActivityManager.RunningAppProcessInfo> rapi = ProcessManager.getRunningAppProcessInfo(context);
        List<ActivityManager.RunningAppProcessInfo> tempInfo = new ArrayList<ActivityManager.RunningAppProcessInfo>();
        for (int i = 0; i < rapi.size(); i++) {
            if (!rapi.get(i).processName.contains("android")) {
                tempInfo.add(rapi.get(i));
            }
        }
        return tempInfo;
    }

    public static void killBackgroundProcessesRAM(Context context) {
        //杀死进程,需要借助ActivityManager
        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //通过调用adapter的getData获取数据方法(获得是一个List<T>格式的数据)
        List<ActivityManager.RunningAppProcessInfo> list = getRunningAppProcessesInfo(context);
        for (int i = 0; i < list.size(); i++) {
            //判断当前获得的程序是不是自己的程序
            if (!list.get(i).processName.equals(context.getPackageName())) {
                //依次杀死所有后台进程
                am.killBackgroundProcesses(list.get(i).processName);
            }
        }
    }

    //获取内置SD卡路径
    public static String getPhoneInSDCardPath() {
        //获取SD状态,判断是否有内置SD卡
        String sdcardPath = Environment.getExternalStorageState();
        //如果有,取出路径
        if (sdcardPath.equals(Environment.MEDIA_MOUNTED)) {//加载状态
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    //获取外置SD卡路径
    public static String getPhoneOutSDCardPath() {
        //获取外置SD状态,判断是否有外置SD卡,"SECONDARY_STORAGE"
        Map<String, String> sdMap = System.getenv();
        if (sdMap.containsKey("SECONDARY_STORAGE")) {
            //取值,包括SD卡信息,以:分割不同信息
            String paths = sdMap.get("SECONDARY_STORAGE");
            String path = paths.split(":")[0];
            if (path == null) {
                return null;
            } else
                return path;
        } else {
            return null;
        }
    }
}
