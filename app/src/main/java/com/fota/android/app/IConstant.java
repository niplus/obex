package com.fota.android.app;

/**
 * 网络相关的常量都放在Constants中
 * IntentExtra包含了Intent常量
 * 其他的可以放这里，可以直接用
 */
public interface IConstant {
    long SECOND = 1000;
    long MINUTE = 60 * SECOND;
    long HOUR = 60 * MINUTE;
    long DAY = 24 * HOUR;
    long MONTH = 30 * DAY;
    long YEAR = 365 * DAY;
}