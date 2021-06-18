package com.fota.android.http;

import com.fota.android.common.bean.wallet.AddressEntity;
import com.fota.android.common.bean.wallet.ContractAccountBean;
import com.fota.android.common.bean.wallet.RateBean;
import com.fota.android.common.bean.wallet.TransferBean;
import com.fota.android.common.bean.wallet.TransferListItemBean;
import com.fota.android.common.bean.wallet.WalletBean;
import com.fota.android.common.bean.wallet.WalletHistoryBean;
import com.fota.android.common.bean.wallet.WithDrawEntity;
import com.fota.android.commonlib.http.BaseHttpEntity;
import com.fota.android.commonlib.http.BaseHttpPage;
import com.fota.android.commonlib.http.BaseHttpResult;
import com.fota.android.core.base.BtbMap;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface WalletService {

    @GET("trade/usdk/asset/info")
    Observable<BaseHttpResult<WalletBean>> getWallet();

    @GET("asset/withdraw/asset/fee")
    Observable<BaseHttpResult<RateBean>> getRate(@Query("assetName")String assetName, @Query("network")String network);

    @GET("asset/transfer/record/coin")
    Observable<BaseHttpResult<BaseHttpPage<WalletHistoryBean>>> getWitdrawtHistory(@QueryMap BtbMap map);

    //注册接口
    @GET("asset/withdraw/check")
    Observable<BaseHttpEntity> withDrawCheck();

    //注册接口
    @POST("asset/withdraw/new")
    Observable<BaseHttpResult<BtbMap>> withDraw(@Body WithDrawEntity body);

    //注册接口
    @GET("asset/deposit/address/new")
    Observable<BaseHttpResult<String>> deposite(@QueryMap BtbMap map);

    //注册接口
    @GET("activity/invite/code")
    Observable<BaseHttpResult<String>> invite(@QueryMap BtbMap map);


    //地址列表
    @GET("asset/withdraw/list")
    Observable<BaseHttpResult<List<AddressEntity>>> getAddressList(@QueryMap BtbMap map);

    //删除用户的提币地址
    @DELETE("asset/withdraw/del")
    Observable<BaseHttpEntity> deleteAddress(@QueryMap BtbMap map);

    //撤销用户的提币
    @DELETE("asset/withdraw/cancel")
    Observable<BaseHttpEntity> deleteWithdraw(@QueryMap BtbMap map);

    //注册接口
    @POST("asset/withdraw/add")
    Observable<BaseHttpEntity> withDraw(@Body AddressEntity body);

    //划转之前判断是否限制接口
    @GET("asset/transfer/check")
    Observable<BaseHttpEntity> transferCheck();

    //资金划转接口
    @POST("asset/transfer")
    Observable<BaseHttpEntity> transfer(@Body TransferBean body);

    //获取资金划转记录列表
    @GET("asset/transfer/record/asset")
    Observable<BaseHttpResult<BaseHttpPage<TransferListItemBean>>> getTransferList(@QueryMap BtbMap map);

    //获取可用usdt
    @GET("asset/usdt/capital")
    Observable<BaseHttpResult<WalletBean>> getAailableUsdt(@QueryMap BtbMap map);

    //获取合约账户里的可用保证金
    @GET("trade/contract/asset/info")
    Observable<BaseHttpResult<ContractAccountBean>> getContractAccount(@QueryMap BtbMap map);
}
