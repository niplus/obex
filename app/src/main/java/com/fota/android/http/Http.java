package com.fota.android.http;


import android.text.TextUtils;

import com.fota.android.app.Constants;
import com.fota.android.app.FotaApplication;
import com.fota.android.app.MD5Utils;
import com.fota.android.commonlib.http.HttpsUtils;
import com.fota.android.utils.DeviceUtils;
import com.fota.android.utils.StringFormatUtils;
import com.fota.android.utils.UserLoginUtil;
import com.tencent.mmkv.MMKV;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.fota.android.app.Constants.SALT;
import static com.fota.android.commonlib.utils.CommonUtils.getSystemTimeWithDiff;

/**
 * Created by sunchao
 * 默认获取的Retrofit是不传api版本号，不支持断网缓存的，Retrofit可以复用，在需要传api版本和添加缓存时，需要使用getRetrofit(String apiVersion, boolean haveCache)
 * 的构造方法，Retrofit是不支持复用的，获取对应的service也不支持复用，参考httpServiceConfigurable，需要跟可以复用的Service分开
 */

public class Http {

    private static OkHttpClient client;
    private static OkHttpClient clientConfigurable;
    private static HttpService httpService;
    private static HttpService httpServiceConfigurable;
    private static volatile Retrofit retrofit;
    private static volatile Retrofit retrofitConfigurable;//可配置api版本和患侧的retrofit
    private static ExchargeService exchangeService;
    private static WalletService walletService;
    private static MarketService marketService;

    private static final Charset UTF8 = Charset.forName("UTF-8");

    /**
     * @return retrofit的底层利用反射的方式, 获取所有的api接口的类
     */
    public static HttpService getHttpService() {
        if (httpService == null) {
            httpService = getRetrofit().create(HttpService.class);
        }
        return httpService;
    }

    /**
     * @return retrofit的底层利用反射的方式, 获取所有的api接口的类
     */
    public static HttpService getHttpService(String apiVersion, boolean havaCache) {
        httpServiceConfigurable = getRetrofit(apiVersion, havaCache).create(HttpService.class);
        return httpServiceConfigurable;
    }

    /**
     * @return retrofit的底层利用反射的方式, 获取所有的api接口的类
     */
    public static ExchargeService getExchangeService() {
        if (exchangeService == null) {
            exchangeService = getRetrofit().create(ExchargeService.class);
        }
        return exchangeService;
    }

    public static WalletService getWalletService() {
        if (walletService == null) {
            walletService = getRetrofit().create(WalletService.class);
        }
        return walletService;
    }

    public static MarketService getMarketService() {
        if (marketService == null) {
            marketService = getRetrofit().create(MarketService.class);
        }
        return marketService;
    }

    /**
     * 设置公共参数
     */
    private static Interceptor addQueryParameterInterceptor() {
        Interceptor addQueryParameterInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request request;
                HttpUrl modifiedUrl = originalRequest.url().newBuilder()
                        // Provide your custom parameter here
//                        .addQueryParameter("phoneSystem", "")
//                        .addQueryParameter("phoneModel", "")
                        .build();
                request = originalRequest.newBuilder().url(modifiedUrl).build();
                return chain.proceed(request);
            }
        };
        return addQueryParameterInterceptor;
    }

    /**
     * 设置头
     */
    private static Interceptor addHeaderInterceptor(final String apiVersion) {
        Interceptor headerInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                String netStatus = NetworkUtil.getNetworkType(FotaApplication.getInstance());
//                String language = AppConfigs.getLanguege().getLanguage();
                String language = MMKV.defaultMMKV().decodeString("language", "zh");
                Request originalRequest = chain.request();
                Long timstamp = getSystemTimeWithDiff();
                Request.Builder requestBuilder = originalRequest.newBuilder()
                        // Provide your custom header here
                        .header("Token", UserLoginUtil.getToken())
                        .header("Content-Type", "application/json")
                        .header("Timestamp", timstamp + "")
                        .header("Platform", "2")
                        .header("Accept-Language", language)
                        .header("Broker-Id", Constants.BROKER_ID)
//                        .header("ip", SharedPreferencesUtil.getInstance().get(PUBLIC_IP, ""))
                        .header("Version", DeviceUtils.getVersonName(FotaApplication.getInstance()))
                        .header("User-Agent", DeviceUtils.getAgent())
                        .header("Signature", getSignMd5(originalRequest, timstamp))
                        .header("UDID", DeviceUtils.getUniqueId(FotaApplication.getInstance()))
                        .header("Network-State", netStatus)
                        .header("Channel-Id", "Unknow")
                        .method(originalRequest.method(), originalRequest.body());
                if (!TextUtils.isEmpty(apiVersion)) {
                    requestBuilder.header("API-Version", apiVersion);
                }
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };
        return headerInterceptor;
    }

    /**
     * @param originalRequest
     * @return 签名
     */
    private static String getSignMd5(Request originalRequest, long timestamp) {
        if (originalRequest == null) {
            return "";
        }
        String sign = "";
        StringBuilder secret = new StringBuilder();
        HttpUrl url = originalRequest.url();
        String urlStr = url.toString();
        String body = "";
        String urlParams = "";

        if (originalRequest.body() != null) {
            RequestBody requestBody = originalRequest.body();
            Buffer buffer = new Buffer();
            try {
                requestBody.writeTo(buffer);
                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }

                if (isPlaintext(buffer)) {
                    body = buffer.readString(charset);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!TextUtils.isEmpty(body))
                body = MD5Utils.getMD5(body);
        }

        //只传？后面的部分
        if ("GET".equals(originalRequest.method()) || "DELETE".equals(originalRequest.method())) {
            urlParams = StringFormatUtils.getParamsStr(urlStr);
        } else {
            urlParams = "";
        }

        secret.append(urlParams).append(body).append(timestamp).append(DeviceUtils.getUniqueId(FotaApplication.getInstance())).append(SALT);
        sign = MD5Utils.getMD5(secret.toString());

        return sign;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private static boolean isPlaintext(Buffer buffer) throws EOFException {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    /**
     * 设置缓存
     */
    private static Interceptor addCacheInterceptor() {
        Interceptor cacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!NetworkUtil.isNetworkAvailable(FotaApplication.getInstance())) {
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                }
                Response response = chain.proceed(request);
                if (NetworkUtil.isNetworkAvailable(FotaApplication.getInstance())) {
                    int maxAge = 0;
                    // 有网络时 设置缓存超时时间0个小时 ,意思就是不读取缓存数据,只对get有用,post没有缓冲
                    response.newBuilder()
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            .removeHeader("Retrofit")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                            .build();
                } else {
                    // 无网络时，设置超时为4周  只对get有用,post没有缓冲
                    int maxStale = 60 * 60 * 24 * 28;
                    response.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" +
                                    maxStale)
                            .removeHeader("nyn")
                            .build();
                }
                return response;
            }
        };
        return cacheInterceptor;
    }

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            synchronized (Http.class) {
                //添加一个log拦截器,打印所有的log
                HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
                //可以设置请求过滤的水平,body,basic,headers
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                //设置 请求的缓存的大小跟位置
                File cacheFile = new File(FotaApplication.getInstance().getCacheDir(), "cache");
                Cache cache = new Cache(cacheFile, 1024 * 1024 * 50); //50Mb 缓存的大小
                HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);

                //初始化Cookie管理器
                CookieJar cookieJar = new CookieJar() {

                    //Cookie缓存区
                    private final Map<String, List<Cookie>> cookiesMap = new HashMap<String, List<Cookie>>();

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        // TODO Auto-generated method stub
                        //移除相同的url的Cookie
//                    String host = arg0.host();
//                    List<Cookie> cookiesList = cookiesMap.get(host);
//                    if (cookiesList != null) {
//                        cookiesMap.remove(host);
//                    }
                        cookiesMap.put(url.host(), cookies);
                        //再重新天添加
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl arg0) {
                        // TODO Auto-generated method stub
                        List<Cookie> cookiesList = cookiesMap.get(arg0.host());
                        //注：这里不能返回null，否则会报NULLException的错误。
                        //原因：当Request 连接到网络的时候，OkHttp会调用loadForRequest()
                        return cookiesList != null ? cookiesList : new ArrayList<Cookie>();
                    }
                };

                OkHttpClient.Builder builder = new OkHttpClient
                        .Builder()
                        .hostnameVerifier(sslParams.hostnameVerifier)
                        .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                        .addInterceptor(addQueryParameterInterceptor())  //参数添加
                        .addInterceptor(addHeaderInterceptor(null)) // token过滤
                        .cache(cache)  //添加缓存
                        .cookieJar(cookieJar)
                        .connectTimeout(5L, TimeUnit.SECONDS)
                        .readTimeout(10L, TimeUnit.SECONDS)
                        .writeTimeout(20L, TimeUnit.SECONDS);
                if (Constants.DEBUG) {
                    builder.addInterceptor(httpLoggingInterceptor); //日志,debug下可看到
                }
                client = builder.build();

                // 获取retrofit的实例
                retrofit = new Retrofit
                        .Builder()
                        .baseUrl(getIpAddress())  //自己配置
                        .client(client)
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create()) //这里是用的fastjson的
                        .build();
            }
        }
        return retrofit;
    }

    /**
     * @param apiVersion api版本号，header中设置
     * @param haveCache  是否需要缓存
     * @return
     */
    public static Retrofit getRetrofit(String apiVersion, boolean haveCache) {
        synchronized (Http.class) {
            //添加一个log拦截器,打印所有的log
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            //可以设置请求过滤的水平,body,basic,headers
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            //设置 请求的缓存的大小跟位置
            File cacheFile = new File(FotaApplication.getInstance().getCacheDir(), "cache");
            Cache cache = new Cache(cacheFile, 1024 * 1024 * 50); //50Mb 缓存的大小
            HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);

            //初始化Cookie管理器
            CookieJar cookieJar = new CookieJar() {

                //Cookie缓存区
                private final Map<String, List<Cookie>> cookiesMap = new HashMap<String, List<Cookie>>();

                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    // TODO Auto-generated method stub
                    //移除相同的url的Cookie
//                    String host = arg0.host();
//                    List<Cookie> cookiesList = cookiesMap.get(host);
//                    if (cookiesList != null) {
//                        cookiesMap.remove(host);
//                    }
                    cookiesMap.put(url.host(), cookies);
                    //再重新天添加
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl arg0) {
                    // TODO Auto-generated method stub
                    List<Cookie> cookiesList = cookiesMap.get(arg0.host());
                    //注：这里不能返回null，否则会报NULLException的错误。
                    //原因：当Request 连接到网络的时候，OkHttp会调用loadForRequest()
                    return cookiesList != null ? cookiesList : new ArrayList<Cookie>();
                }
            };

            OkHttpClient.Builder builder = new OkHttpClient
                    .Builder()
                    .hostnameVerifier(sslParams.hostnameVerifier)
                    .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                    .addInterceptor(addQueryParameterInterceptor())  //参数添加
                    .addInterceptor(addHeaderInterceptor(apiVersion)) // token过滤
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .cache(cache)  //添加缓存
                    .cookieJar(cookieJar)
                    .connectTimeout(5L, TimeUnit.SECONDS)
                    .readTimeout(10L, TimeUnit.SECONDS)
                    .writeTimeout(20L, TimeUnit.SECONDS);
            if (haveCache) {
                builder.addInterceptor(haveCache ? addCacheInterceptor() : null);
            }
//            if (Constants.DEBUG) {
//                builder.addInterceptor(httpLoggingInterceptor); //日志,debug下可看到
//            }
            clientConfigurable = builder.build();

            // 获取retrofit的实例
            retrofitConfigurable = new Retrofit
                    .Builder()
                    .baseUrl(getIpAddress())  //自己配置
                    .client(clientConfigurable)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create()) //这里是用的fastjson的
                    .build();
        }
        return retrofitConfigurable;
    }

    public static void clearService() {
        retrofit = null;
        retrofitConfigurable = null;
        httpService = null;
        httpServiceConfigurable = null;
        exchangeService = null;
        walletService = null;
        marketService = null;
    }


    public static String getIpAddress() {
        return "http://bg-dev.yuchains.com/mapi/";
//        return Constants.getHttpUrl();
        //return Pub.isStringEmpty(AppConfigs.getIpAddress()) ? getDefaultAddress() : AppConfigs.getIpAddress();
    }


}
