package com.example.nwow.ui.main.zagger.repository

import androidx.lifecycle.MutableLiveData
import com.example.nwow.config.API
import com.example.nwow.network_handler.NetworkUtils
import com.example.nwow.ui.main.zagger.model.PlaceOrderResponse
import com.example.nwow.ui.main.zagger.model.ZaggerResponse
import com.example.nwow.utils.ResponseHandling
import okhttp3.RequestBody

class ZaggerRepository(
    private val api: API,
    private val nwowApi: API,
) {

    private val zaggerLiveData = MutableLiveData<ResponseHandling<ZaggerResponse>>()
    val zaggerResponse: MutableLiveData<ResponseHandling<ZaggerResponse>>
        get() = zaggerLiveData

    private val placeOrderLiveData = MutableLiveData<ResponseHandling<PlaceOrderResponse>>()
    val placeOrderResponse: MutableLiveData<ResponseHandling<PlaceOrderResponse>>
        get() = placeOrderLiveData

    suspend fun getData(modelNo: String) {
        if (NetworkUtils.isInternetAvailable()) {
            try {
                val result = api.getZaggerData("79c44c81-3590-469e-84c2-54fa1e119b11", modelNo)
                if (result.isSuccessful) zaggerLiveData.postValue(ResponseHandling.Success(result.body()))
                else zaggerLiveData.postValue(ResponseHandling.Error("Error"))

            } catch (e: Exception) {
                e.printStackTrace()
                zaggerLiveData.postValue(ResponseHandling.Error(e.message))
            }

        } else
            zaggerLiveData.postValue(ResponseHandling.Error("No Internet"))
    }

    suspend fun placeOrder(requestBody: RequestBody) {
        if (NetworkUtils.isInternetAvailable()) {
            try {
                val result = nwowApi.placeOrder(requestBody)
                if (result.isSuccessful)
                    placeOrderLiveData.postValue(ResponseHandling.Success(result.body()))
                else
                    placeOrderLiveData.postValue(ResponseHandling.Error("Error"))

            } catch (e: Exception) {
                e.printStackTrace()
                placeOrderLiveData.postValue(ResponseHandling.Error(e.message))
            }

        } else
            placeOrderLiveData.postValue(ResponseHandling.Error("No Internet"))
    }


}
