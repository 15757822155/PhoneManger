package com.zhuoxin.entity;

/**
 * Created by Administrator on 2016/11/7.
 */

public class TelNumberInfo {
    public String name;
    public String number;
    public int idx;

    public TelNumberInfo(int idx, String name, String number) {
        this.idx = idx;
        this.name = name;
        this.number = number;
    }
}
