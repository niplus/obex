package com.fota.android.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.fota.android.app.FotaApplication;
import com.fota.android.commonlib.utils.Pub;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by sunchao
 */

public class DeviceUtils {

    private static String versonName;

    /**
     * 获取设备唯一标识
     * AndroidId 和 Serial Number 的通用性都较好，并且不受权限限制
     *
     * @param context
     * @return
     */
    public static String getUniqueId(Context context) {
        String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String id = androidID + Build.SERIAL;
        try {
            return toMD5(id);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return id;
        }
    }


    private static String toMD5(String text) throws NoSuchAlgorithmException {
        //获取摘要器 MessageDigest
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        //通过摘要器对字符串的二进制字节数组进行hash计算
        byte[] digest = messageDigest.digest(text.getBytes());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            //循环每个字符 将计算结果转化为正整数;
            int digestInt = digest[i] & 0xff;
            //将10进制转化为较短的16进制
            String hexString = Integer.toHexString(digestInt);
            //转化结果如果是个位数会省略0,因此判断并补0
            if (hexString.length() < 2) {
                sb.append(0);
            }
            //将循环结果添加到缓冲区
            sb.append(hexString);
        }
        //返回整个结果
        return sb.toString();
    }

    /**
     * 获取当前app版本号
     */
    public static String getVersonName(Context context) {
        if (null == context) {
            return "";
        }
        if (!Pub.isStringEmpty(versonName)) {
            return versonName;
        }
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versonName = info.versionName;
        } catch (Exception e) {

        }
        return versonName;

    }


    /**
     * 获取本应用的VersionCode
     */
    public static int getVersionCode() {
        PackageInfo info = getPackageInfo(null);
        if (info != null) {
            return info.versionCode;
        } else {
            return -1;
        }
    }

    /**
     * 根据packageName获取packageInfo
     */
    public static PackageInfo getPackageInfo(String packageName) {
        Context context = FotaApplication.getInstance();
        if (null == context) {
            return null;
        }
        if (TextUtils.isEmpty(packageName)) {
            packageName = context.getPackageName();
        }
        PackageInfo info = null;
        PackageManager manager = context.getPackageManager();
        // 根据packageName获取packageInfo
        try {
            info = manager.getPackageInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
        } catch (PackageManager.NameNotFoundException e) {
        }
        return info;
    }

    /**
     * 获取设备信息
     *
     * @return
     */
    public static String getAgent() {
        String name = com.blankj.utilcode.util.DeviceUtils.getManufacturer();//获取厂商
        String model = com.blankj.utilcode.util.DeviceUtils.getModel();//获取型号
        return name + "_" + model;
    }

}
