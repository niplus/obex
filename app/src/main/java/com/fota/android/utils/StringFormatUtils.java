package com.fota.android.utils;

import android.text.TextUtils;
import android.util.Log;

import com.fota.android.commonlib.utils.RegexUtils;
import com.google.gson.Gson;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/6/1.
 */

public class StringFormatUtils {
    private static ArrayList<String> accountList;

    private static Zxcvbn zxcvbn = null;

    public static String formatBank(String str, int befor, int end) {
        try {
            Log.d("formatBank", " 原始: " + str);

            if (str == null || befor < 0 || end < 0)
                return str;

            String temp = str.trim();

            if (temp.length() <= (befor + end))
                return temp;

            String startStr = temp.substring(0, befor);
            String middleStr = temp.substring(befor, str.length() - end);
            String endStr = temp.substring(str.length() - end, str.length());

            String middleConvert = "";
            for (int i = 0; i < middleStr.length(); i++) {
                middleConvert += "*";
            }

            String result = startStr + middleConvert + endStr;

            Log.d("formatBank", "startStr: " + startStr);
            Log.d("formatBank", "middleConvert: " + middleConvert);
            Log.d("formatBank", "endStr: " + endStr);
            Log.d("formatBank", "result: " + result);


            return result;
        } catch (Exception e) {
            return str;
        }
    }


    public static String formatAlipay(String str) {
        try {

            if (str == null || str.trim().length() == 0)
                return "";


            if (isNumeric(str)) {
                return formatBank(str, 3, 2);
            } else {
                if (str.length() <= 3) {
                    return str;
                }
                if (!str.contains("@")) {
                    return formatBank(str, 3, 0);
                }

                //int index = str.indexOf("@");

                return str;

            }
        } catch (Exception e) {
            return str;
        }
    }

    //Java中判断字符串是否全为数字的方
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 对字符加星号处理：除前面几位和后面几位外，其他的字符以星号代替
     *
     * @param content  传入的字符串
     * @param frontNum 保留前面字符的位数
     * @param endNum   保留后面字符的位数
     * @return 带星号的字符串
     */

    public static String getHideString(String content, int frontNum, int endNum) {

        if (frontNum >= content.length() || frontNum < 0) {
            return content;
        }
        if (endNum >= content.length() || endNum < 0) {
            return content;
        }
        if (frontNum + endNum >= content.length()) {
            return content;
        }
        String starStr = "";
        for (int i = 0; i < (content.length() - frontNum - endNum); i++) {
            starStr = starStr + "*";
        }
        return content.substring(0, frontNum) + starStr
                + content.substring(content.length() - endNum, content.length());

    }

    /**
     * 对字符加星号处理：除前面几位和后面几位外，其他的字符以星号代替，星号最多4个
     *
     * @param content 传入的字符串
     * @return 带星号的字符串
     */

    public static String getHidePhone(String content) {

        if (TextUtils.isEmpty(content)) {
            return "";
        }
        if (content.length() > 10) {
            return content.substring(0, 3) + "****" + content.substring(content.length() - 3, content.length());
        } else {
            return getHideString(content, 3, 3);
        }

    }

    /**
     * 返回隐藏的邮箱，最多4个*
     *
     * @param email
     * @return
     */
    public static String getHideEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return "";
        }
        return email.replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1****$3$4");

    }

    public static boolean checkEmail(String email) {// 验证邮箱的正则表达式 前端先不判断
//        return RegexUtils.checkEmail(email);
        return true;

    }

    /**
     * 验证密码强度
     * # 0 Weak        （guesses < ^ 3 10）
     * # 1 Fair        （guesses <^ 6 10）
     * # 2 Good        （guesses <^ 8 10）
     * # 3 Strong      （guesses < 10 ^ 10）
     * # 4 Very strong （guesses >= 10 ^ 10）
     *
     * @param str
     * @return
     */
    public static int checkStrengthISHeigh(String str, String account) {
        if (TextUtils.isEmpty(str))
            return 0;
        if (zxcvbn == null)
            zxcvbn = new Zxcvbn();
        if (accountList == null)
            accountList = new ArrayList<>();
        accountList.clear();
        if (!TextUtils.isEmpty(account))
            accountList.add(account);
        Strength strength = zxcvbn.measure(str, accountList);
        return strength.getScore();
    }

    public static int getStengthParam(String str, String account) {
        int score = checkStrengthISHeigh(str, account);
        if (score < 3) {
            return score;
        } else {
            return 3;
        }
    }

    /**
     * 密码长度判断
     */
    public static boolean checkPswLength(String str) {
        if (TextUtils.isEmpty(str))
            return false;

        return str.length() >= 6 && str.length() <= 32;
    }

    /**
     * 去除字符串中的年月日
     *
     * @param str
     * @return
     */
    public static String delYDM(String str) {
        if (TextUtils.isEmpty(str))
            return "";
        return str;
//        String str2 = str.replace("年", "").replace("月", "").replace("日", "").
//                replace("Y", "").replace("M", "").replace("D", "");
//        return str.replaceAll("[^0-9]", "");
    }

    /**
     * 去除字符串中的年月日
     *
     * @param str
     * @return
     */
    public static int getIntId(String str) {
        int id = 0;
        if (TextUtils.isEmpty(str))
            return 0;
        if (str.length() > 11) {
            str = str.substring(str.length() - 11, str.length());
        }
        try {
            id = Integer.parseInt(str);
        } catch (Exception e) {

        }
        return id;
    }

    /**
     * 校验身份证
     *
     * @param idCard
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isIDCard(String idCard) {
        return RegexUtils.checkIdCard(idCard);
    }

    /**
     * 获取get请求参数
     *
     * @param url
     */
    public static String getParamsStr(String url) {
        if (TextUtils.isEmpty(url))
            return "";
        int index = url.lastIndexOf("?");
        if (index == -1 || url.endsWith("?"))
            return "";
        return url.substring(index + 1, url.length());
    }

    //  Map数据类型转为JSON字符串
    public static String mapToJson(Map mapObj) {
        Gson gson = new Gson();
        String json_string = null;
        try {
            json_string = gson.toJson(mapObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json_string;
    }


}

