///*
//package com.fota.android.utils;
//
//import android.annotation.TargetApi;
//import android.content.Context;
//import android.content.res.Configuration;
//import android.content.res.Resources;
//import android.os.Build;
//import android.os.LocaleList;
//import android.text.TextUtils;
//import android.util.DisplayMetrics;
//
//import com.fota.android.app.Constants;
//
//import java.util.HashMap;
//import java.util.Locale;
//public class AppLanguageUtils {
//
//    public static Context attachBaseContext(Context context, String language) {
//      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//         return updateResources(context, language);
//                   } else {
//                         return context;
//                     }
//             }
//
//    @TargetApi(Build.VERSION_CODES.N)
//    private static Context updateResources(Context context, String language) {
//        Resources resources = context.getResources();
//        Locale locale = AppLanguageUtils.getLocaleByLanguage(language);
//
//        Configuration configuration = resources.getConfiguration();
//        configuration.setLocale(locale);
//        configuration.setLocales(new LocaleList(locale));
//        return context.createConfigurationContext(configuration);
//    }
//
//
//    */
///**
//     *
//     * @param language language
//     * @return
//     *//*
//
//    public static Locale getLocaleByLanguage(String language) {
//        if (isSupportLanguage(language)) {
//            return mAllLanguages.get(language);
//        } else {
//            Locale locale = Locale.getDefault();
//            for (String key : mAllLanguages.keySet()) {
//                if (TextUtils.equals(mAllLanguages.get(key).getLanguage(), locale.getLanguage())) {
//                    return locale;
//                }
//            }
//        }
//        return Locale.ENGLISH;
//    }
//
//    private static boolean isSupportLanguage(String language) {
//        return mAllLanguages.containsKey(language);
//    }
//
//    public static String getSupportLanguage(String language) {
//        if (isSupportLanguage(language)) {
//            return language;
//        }
//
//        if (null == language) {//为空则表示首次安装或未选择过语言，获取系统默认语言
//            Locale locale = Locale.getDefault();
//            for (String key : mAllLanguages.keySet()) {
//                if (TextUtils.equals(mAllLanguages.get(key).getLanguage(), locale.getLanguage())) {
//                    return locale.getLanguage();
//                }
//            }
//        }
//        return Constants.ENGLISH;
//    }
//
//    public static HashMap<String, Locale> mAllLanguages = new HashMap<String, Locale>(8) {{
//        put(Constants.ENGLISH, Locale.ENGLISH);
//        put(Constants.CHINESE, Locale.SIMPLIFIED_CHINESE);
//        put(Constants.TRADITIONAL_CHINESE, Locale.TRADITIONAL_CHINESE);
//    }};
//
//    @SuppressWarnings("deprecation")
//    public static void changeAppLanguage(Context context, String newLanguage) {
//        Resources resources = context.getResources();
//        Configuration configuration = resources.getConfiguration();
//
//        // app locale
//        Locale locale = getLocaleByLanguage(newLanguage);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            configuration.setLocale(locale);
//        } else {
//            configuration.locale = locale;
//        }
//
//        // updateConfiguration
//        DisplayMetrics dm = resources.getDisplayMetrics();
//        resources.updateConfiguration(configuration, dm);
//    }
//
//
//
//
//
//
//
//}
//*/
