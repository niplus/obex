package com.fota.android.moudles.futures.model

import com.fota.android.http.ApiService
import com.fota.android.http.Http
import com.fota.android.moudles.futures.bean.*
import com.fota.android.moudles.mine.bean.FundDataBean
import com.ndl.lib_common.base.BaseRepository
import com.ndl.lib_common.base.Response

class FuturesRepository: BaseRepository() {

    /**
     *
     * @param contractId Int 合约ID
     * @param orderType Int 订单触发类型 1 限价 2 市价
     * @param triggerPrice String 触发价格
     * @param algoPrice String 委托价格，市价单不传
     * @param quantity String 数量
     * @param orderDirection Int 买卖方向
     * @return Response<String>
     */
    suspend fun conditionOrder(contractId: Int, orderType: Int, triggerPrice: String, algoPrice: String?, quantity: String, orderDirection: Int): Response<String>{
        return apiCall {
            Http.getRetrofit().create(ApiService::class.java).conditionOrder(ConditionOrderEntity(contractId, orderType, triggerPrice, algoPrice, quantity, orderDirection))
        }
    }

    suspend fun getConditionOrder(pageNo: Int, pageSize: Int): Response<ConditionOrdersBean>{
        return apiCall {
            Http.getRetrofit().create(ApiService::class.java).getConditionOrder(pageNo, pageSize, null, null)
        }
    }

    suspend fun getConditionHistoryOrder(pageNo: Int, pageSize: Int): Response<ConditionOrdersBean>{
        return apiCall {
            Http.getRetrofit().create(ApiService::class.java).getConditionHistoryOrder(pageNo, pageSize, null, null)
        }
    }

    /**
     * 止盈止损
     * @param contractId Int 合约id
     * @param buyAlgoPrice String
     * @param buyAlgoValue String
     * @param buyOrderType Int
     * @param buyTriggerPrice String
     * @param sellAlgoPrice String
     * @param sellAlgoValue String
     * @param sellOrderType Int
     * @param sellTriggerPrice String
     * @return Response<String>
     */
    suspend fun stopOrder(contractId: Int,
                          buyAlgoPrice: String?, buyAlgoValue: String?, buyOrderType: Int?, buyTriggerPrice: String?,
                          sellAlgoPrice: String?, sellAlgoValue: String?, sellOrderType: Int?, sellTriggerPrice: String?
    ): Response<String>{
        return apiCall {
            var stopLessOrder: StopLessOrder? = null
            if (buyTriggerPrice != null){
                stopLessOrder = StopLessOrder(buyAlgoPrice, buyAlgoValue, buyOrderType, buyTriggerPrice)
            }

            var stopLessOrder1: StopLessOrder? = null
            if (sellTriggerPrice != null){
                stopLessOrder1 = StopLessOrder(sellAlgoPrice, sellAlgoValue, sellOrderType, sellTriggerPrice)
            }
            Http.getRetrofit().create(ApiService::class.java).stopOrder(StopOrderEntity(contractId,stopLessOrder1, stopLessOrder))
        }
    }


    suspend fun cancelConditionOrder(orderId: String): Response<String>{
        return apiCall {
            Http.getRetrofit().create(ApiService::class.java).cancelConditionOrder(
                CancelOrderEntitiy(orderId)
            )
        }
    }

    suspend fun closeOrder(totalAmount: String?, contractId: Int, percent: String): Response<String>{
        return apiCall {
            Http.getRetrofit().create(ApiService::class.java).closeOrder(CloseOrderBean(totalAmount, contractId, percent))
        }
    }

    suspend fun getFundData(): Response<FundDataBean>{
        return apiCall {
            val hashMap = hashMapOf<String, Any>()
            hashMap["pageNo"] = "1"
            hashMap["pageSize"] = "100"
            Http.getRetrofit().create(ApiService::class.java).getFundData(getRequestBody(hashMap))
        }
    }


}