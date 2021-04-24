package com.fota.android.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.fota.android.commonlib.utils.SharedPreferencesUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class NetworkUtil {

    public static int NET_CNNT_BAIDU_OK = 1; // NetworkAvailable
    public static int NET_CNNT_BAIDU_TIMEOUT = 2; // no NetworkAvailable
    public static int NET_NOT_PREPARE = 3; // Net no ready
    public static int NET_ERROR = 4; //net error
    private static int TIMEOUT = 3000; // TIMEOUT

    /**
     * check NetworkAvailable
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        //jiang 0829 add null check
        if (context == null) {
            return false;
        }

        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (null == manager)
            return false;
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (null == info || !info.isAvailable())
            return false;
        return true;
    }

    /**
     * 得到ip地址
     *
     * @return
     */
    public static String getLocalIpAddress() {
        String ret = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        ret = inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    /**
     * 返回当前网络状态
     *
     * @param context
     * @return
     */
    public static int getNetState(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo networkinfo = connectivity.getActiveNetworkInfo();
                if (networkinfo != null) {
                    if (networkinfo.isAvailable() && networkinfo.isConnected()) {
                        if (!connectionNetwork())
                            return NET_CNNT_BAIDU_TIMEOUT;
                        else
                            return NET_CNNT_BAIDU_OK;
                    } else {
                        return NET_NOT_PREPARE;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NET_ERROR;
    }

    /**
     * ping "http://www.baidu.com"
     *
     * @return
     */
    static private boolean connectionNetwork() {
        boolean result = false;
        HttpURLConnection httpUrl = null;
        try {
            httpUrl = (HttpURLConnection) new URL("http://www.baidu.com")
                    .openConnection();
            httpUrl.setConnectTimeout(TIMEOUT);
            httpUrl.connect();
            result = true;
        } catch (IOException e) {
        } finally {
            if (null != httpUrl) {
                httpUrl.disconnect();
            }
            httpUrl = null;
        }
        return result;
    }

    /**
     * check is3G
     *
     * @param context
     * @return boolean
     */
    public static boolean is3G(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    /**
     * isWifi
     *
     * @param context
     * @return boolean
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * is2G
     *
     * @param context
     * @return boolean
     */
    public static boolean is2G(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && (activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE
                || activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS || activeNetInfo
                .getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA)) {
            return true;
        }
        return false;
    }

    /**
     * is wifi on
     */
    public static boolean isWifiEnabled(Context context) {
        ConnectivityManager mgrConn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mgrTel = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

    /**
     * 获取当前网络类型
     *
     * @param context
     * @return
     */
//    public static String getNetworkType(Context context) {
//        String strNetworkType = "UnKnown";
//        final NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
//        if (activeNetworkInfo != null && activeNetworkInfo.getType() == 1) {
//            strNetworkType = "WIFI";
//        } else if (activeNetworkInfo != null && activeNetworkInfo.getType() == 0) {
//            String subtypeName = activeNetworkInfo.getSubtypeName();
//            switch (((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDataNetworkType()) {
//                case TelephonyManager.NETWORK_TYPE_GPRS:
//                case TelephonyManager.NETWORK_TYPE_EDGE:
//                case TelephonyManager.NETWORK_TYPE_CDMA:
//                case TelephonyManager.NETWORK_TYPE_1xRTT:
//                case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
//                    strNetworkType = "2G";
//                    break;
//                case TelephonyManager.NETWORK_TYPE_UMTS:
//                case TelephonyManager.NETWORK_TYPE_EVDO_0:
//                case TelephonyManager.NETWORK_TYPE_EVDO_A:
//                case TelephonyManager.NETWORK_TYPE_HSDPA:
//                case TelephonyManager.NETWORK_TYPE_HSUPA:
//                case TelephonyManager.NETWORK_TYPE_HSPA:
//                case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
//                case TelephonyManager.NETWORK_TYPE_EHRPD: //api<11 : replace by 12
//                case TelephonyManager.NETWORK_TYPE_HSPAP: //api<13 : replace by 15
//                    strNetworkType = "3G";
//                    break;
//                case TelephonyManager.NETWORK_TYPE_LTE:
//                    strNetworkType = "4G";
//                    break;
//                default:
//                    if (subtypeName.equalsIgnoreCase("TD-SCDMA") || subtypeName.equalsIgnoreCase("WCDMA") || subtypeName.equalsIgnoreCase("CDMA2000")) {
//                        strNetworkType = "3G";
//                        break;
//                    }
//                    strNetworkType = subtypeName;
//                    break;
//            }
//        }
//        return strNetworkType;
//    }

    //jiang
    public static void getNetIp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String ip = "";
                try {
                    String address = "http://ip.taobao.com/service/getIpInfo2.php?ip=myip";
                    URL url = new URL(address);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setUseCaches(false);
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream in = connection.getInputStream();
                        // 将流转化为字符串  
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        String tmpString = "";
                        StringBuilder retJSON = new StringBuilder();
                        while ((tmpString = reader.readLine()) != null) {
                            retJSON.append(tmpString + "\n");
                        }
                        JSONObject jsonObject = new JSONObject(retJSON.toString());
                        String code = jsonObject.getString("code");
                        if (code.equals("0")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            ip = data.getString("ip");
                        } else {

                        }
                    } else {
                        ip = "";
                    }
                } catch (Exception e) {
                    ip = "";
                }

                SharedPreferencesUtil.getInstance().put(SharedPreferencesUtil.Key.PUBLIC_IP, ip);
            }
        }).start();
    }


    /**
     * 将文件路径数组封装为{@link List < MultipartBody.Part>}
     *
     * @param key       对应请求正文中name的值。目前服务器给出的接口中，所有图片文件使用<br>
     *                  同一个name值，实际情况中有可能需要多个
     * @param filePaths 文件路径数组
     */
    public static List<MultipartBody.Part> files2Parts (String key,
                                                        String[]filePaths){
        List<MultipartBody.Part> parts = new ArrayList<>(filePaths.length);
        for (String filePath : filePaths) {
            File file = new File(filePath);
            // 根据类型及File对象创建RequestBody（okhttp的类）
            RequestBody requestBody = RequestBody.create(MediaType.parse("images/jpeg"), file);
            // 将RequestBody封装成MultipartBody.Part类型（同样是okhttp的）
            MultipartBody.Part part = MultipartBody.Part.
                    createFormData(key, file.getName(), requestBody);
            // 添加进集合
            parts.add(part);
        }
        return parts;
    }

}