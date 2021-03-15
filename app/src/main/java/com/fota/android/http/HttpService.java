package com.fota.android.http;


import com.fota.android.common.bean.home.BannerBean;
import com.fota.android.common.bean.home.NoticeBean;
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.BaseHttpResult;
import com.fota.android.core.base.BtbMap;
import com.fota.android.moudles.main.BottomMenuItemInfo;
import com.fota.android.moudles.mine.bean.ContractChengjiaoBean;
import com.fota.android.moudles.mine.bean.ContractLevelBean;
import com.fota.android.moudles.mine.bean.ContractWeituoBean;
import com.fota.android.moudles.mine.bean.GoogleBean;
import com.fota.android.moudles.mine.bean.MineBean;
import com.fota.android.moudles.mine.bean.NoticeCenterBean;
import com.fota.android.moudles.mine.bean.OptionCoinBean;
import com.fota.android.moudles.mine.bean.OptionHisBean;
import com.fota.android.moudles.mine.bean.OptionOffOnBean;
import com.fota.android.moudles.mine.bean.VersionBean;
import com.fota.android.moudles.mine.bean.XianhuoChengjiaoBean;
import com.fota.android.moudles.mine.bean.XianhuoWeituoBean;
import com.fota.android.moudles.mine.login.bean.LoginBean;
import com.fota.android.moudles.mine.resetpassword.bean.PicCheckBean;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by sunchao
 * 网络请求的接口都在这里
 */

public interface HttpService {
//    //登录接口
//    @POST("account/user/login")
//    @Headers("Content-Type: application/json")
//    Observable<BaseHttpResult<LoginBean>> login(@Field("type") int type, @Field
//            ("phone") String phone, @Field("email") String email, @Field
//                                                        ("pwd") String pwd);

    //登录接口
    @POST("account/user/login")
    @Headers("Content-Type: application/json")
    Observable<BaseHttpResult<LoginBean>> login(@Body JsonObject body);

    //忘记密码
    //获取图片验证码
    @GET("home/appTabbar")
    Observable<BaseHttpResult<BottomMenuItemInfo>> appTabbar(@QueryMap BtbMap map);


    @GET("account/captcha/picture")
    Observable<BaseHttpResult<String>> getPicCode();


    //    //校验图片验证码  data ""
//    @FormUrlEncoded
//    @POST("account/captcha/verify")
//    Observable<BaseHttpResult<PicCheckBean>> getCheckPicCode(@Field("captchaCode") String captchaCode, @Field
//            ("userSymbol") String userSymbol);
    //校验图片验证码
    @Headers("Content-Type: application/json")
    @POST("account/captcha/verify")
    Observable<BaseHttpResult<PicCheckBean>> getCheckPicCode(@Body JsonObject body);

    //忘记密码
    @Headers("Content-Type: application/json")
    @PUT("account/user/resetpwd")
    Observable<BaseHttpResult<BaseHttpEntity>> resetPsw(@Body JsonObject jsonObject);

    //重置密码
    @Headers("Content-Type: application/json")
    @PUT("account/user/updatepwd")
    Observable<BaseHttpResult<String>> changePsw(@Body JsonObject body);


    //注册接口
    @Headers("Content-Type: application/json")
    @POST("account/user/register")
    Observable<BaseHttpResult<Object>> regist(@Body JsonObject body);

    //获取验证码
    @Headers("Content-Type: application/json")
    @POST("account/verification/code")
    Observable<BaseHttpResult<BaseHttpEntity>> getVcode(@Body JsonObject body);

    @GET("account/verification/sendPhoneCode")
    Observable<BaseHttpResult<BaseHttpEntity>> getSmsCode();

    //    首页
    //轮播图
    @GET("home/banner")
    Observable<BaseHttpResult<List<BannerBean>>> banner();

    //公告列表
    @GET("home/notice")
    Observable<BaseHttpResult<List<NoticeBean>>> getNotic();

    //    我的
    //我的首页接口
    @GET("account/user/asset")
    Observable<BaseHttpResult<MineBean>> getMineData();

    //登出
    @POST("account/user/logout")
    Observable<BaseHttpResult<String>> logOut();

    //绑定手机号
    @POST("account/security/phone")
    Observable<BaseHttpResult<String>> bindPhone(@Body JsonObject body);

    //绑定邮箱
    @POST("account/security/email")
    Observable<BaseHttpResult<String>> bindEmail(@Body JsonObject body);

    //设置资金密码
    @POST("account/security/fundPwd")
    Observable<BaseHttpResult<String>> setCapital(@Body JsonObject body);

    //重置资金密码
    @Headers("Content-Type: application/json")
    @PUT("account/security/fundPwd")
    Observable<BaseHttpResult<String>> resetCapital(@Body JsonObject body);

    //忘记资金密码
    @Headers("Content-Type: application/json")
    @PUT("account/security/fundPwd/forget")
    Observable<BaseHttpResult<String>> forgetCapital(@Body JsonObject body);

    //获取谷歌验证信息
    @GET("account/verification/google/secretkey")
    Observable<BaseHttpResult<GoogleBean>> getGoogle();

    //绑定谷歌
    @POST("account/security/google")
    Observable<BaseHttpResult<String>> bindGoogle(@Body JsonObject body);

    //解绑谷歌
    @HTTP(method = "DELETE", path = "account/security/google")
    Observable<BaseHttpResult<String>> unbindGoogle(@QueryMap BtbMap map);

    //交易设置列表
    @GET("account/user/contract/lever")
    Observable<BaseHttpResult<List<ContractLevelBean>>> getContractLevels();

    //设置交易杠杆
    @POST("account/user/contract/lever")
    Observable<BaseHttpResult<String>> setContractLever(@Body JsonObject body);

    //上传 图片
    @Multipart
//    @Headers("Content-Type: multipart/form-data")
    @POST("account/security/idcard/picture")
    Observable<BaseHttpResult<String>> uploadIdPic(@Part List<MultipartBody.Part> parts);

    //身份认证提交审核
    @POST("account/security/idcard")
    Observable<BaseHttpResult<String>> identityCheck(@Body JsonObject body);

    //合约委托列表
    @GET("account/user/contract/list")
    Observable<BaseHttpResult<ContractWeituoBean>> getHYTradeList(@QueryMap BtbMap map);

    //合约成交列表
    @GET("account/user/contract/matched")
    Observable<BaseHttpResult<ContractChengjiaoBean>> getHYChengjiaoList(@QueryMap BtbMap map);

    //USDT委托列表
    @GET("account/user/usdt/list")
    Observable<BaseHttpResult<XianhuoWeituoBean>> getUSDTTradeList(@QueryMap BtbMap map);

    //USDT成交列表
    @GET("account/user/usdt/matched")
    Observable<BaseHttpResult<XianhuoChengjiaoBean>> getUSDTChengjiaoList(@QueryMap BtbMap map);

    //期权成交历史
    @GET("option/order/page")
    Observable<BaseHttpResult<OptionHisBean>> getOptionHisList(@QueryMap BtbMap map);

    //期权可下注币种的获取
    @GET("option/asset")
    Observable<BaseHttpResult<List<OptionCoinBean>>> getOptionAccountCoin();

    //获取是否可以点击进入OptionFragment
    @GET("base/config/option")
    Observable<BaseHttpResult<OptionOffOnBean>> enterOptionCheck();

    //版本信息
    @GET("home/appInfo")
    Observable<BaseHttpResult<VersionBean>> getVersionUpdate(@QueryMap BtbMap map);

    //登录token验证
    @GET("account/user/checkToken")
    Observable<BaseHttpResult<Boolean>> loginTokenCheck(@QueryMap BtbMap map);

    //获取系统时间
    @GET("home/time")
    Observable<BaseHttpResult<String>> getServiceTIme();

    //消息中心
    @GET("app/msgCenter")
    Observable<BaseHttpResult<NoticeCenterBean>> getNoticesList(@QueryMap BtbMap map);

    //消息中心
    @Headers("Content-Type: application/json")
    @PUT("account/user/username")
    Observable<BaseHttpResult<NoticeCenterBean>> setNickName(@Body BtbMap map);

    //切换语言接口，暂时不用了
    @Headers("Content-Type: application/json")
    @PUT("account/user/changeLanguage")
    Observable<BaseHttpResult<BaseHttpEntity>> changeLanguage(@Body JsonObject jsonObject);
}
