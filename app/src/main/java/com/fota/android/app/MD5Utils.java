package com.fota.android.app;

import com.fota.android.commonlib.utils.Base64;

import java.security.MessageDigest;


/**
 * Created by Administrator on 2018/3/28.
 */

public class MD5Utils {

    public static String md5(String string) {
//        if (TextUtils.isEmpty(string)) {
//            return "";
//        }
//        return string;


        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md5.digest(string.getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result.append(temp);
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 登录密码md5
     * @param string
     * @return
     */
    public static String sha256Psw(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Constants.getSaltPsw().getBytes());
            byte[] digest = md.digest(string.getBytes());

            return Base64.encode(digest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 资金密码md5
     * @param string
     * @return
     */
    public static String sha256Capital(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Constants.getSaltCapital().getBytes());
            byte[] digest = md.digest(string.getBytes());

            return Base64.encode(digest);
//            salt = XXX
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取MD5字符串
     */
    public static String getMD5(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(content.getBytes());
            return getHashString(digest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取加盐的MD5字符串
     */
    public static String getMD5WithSalt(String content) {
//        return getMD5(getMD5(content) + SALT);
        return getMD5(content + Constants.SALT);
    }

    private static String getHashString(MessageDigest digest) {
        StringBuilder builder = new StringBuilder();
        for (byte b : digest.digest()) {
            builder.append(Integer.toHexString((b >> 4) & 0xf));
            builder.append(Integer.toHexString(b & 0xf));
        }
        return builder.toString();
    }
}
