package com.example.nwow.ui.main.nwow.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.nwow.config.API
import com.example.nwow.network_handler.NetworkUtils
import com.example.nwow.ui.main.nwow.model.NwowResponse
import com.example.nwow.ui.main.nwow.model.SuggestedPriceRequest
import com.example.nwow.ui.main.nwow.model.SuggestedPriceResponse
import com.example.nwow.utils.ResponseHandling
import okhttp3.RequestBody

class NwowRepository(
    private val api: API,
) {

    private val nwowLiveData = MutableLiveData<ResponseHandling<NwowResponse>>()
    val nwowResponse: MutableLiveData<ResponseHandling<NwowResponse>>
        get() = nwowLiveData

    private val suggestedPriceLiveData = MutableLiveData<ResponseHandling<SuggestedPriceResponse>>()
    val suggestedPriceResponse: MutableLiveData<ResponseHandling<SuggestedPriceResponse>>
        get() = suggestedPriceLiveData


    suspend fun getData(model: String) {
        if (NetworkUtils.isInternetAvailable()) {
            try {
                val result = api.getNwowData(model)
                if (result.isSuccessful)
                    nwowLiveData.postValue(ResponseHandling.Success(result.body()))
                else
                    nwowLiveData.postValue(ResponseHandling.Error("Error"))

            } catch (e: Exception) {
                e.printStackTrace()
                nwowLiveData.postValue(ResponseHandling.Error(e.message))
            }

        } else nwowLiveData.postValue(ResponseHandling.Error("No Internet"))
    }

    suspend fun getSuggestedPrice(suggestedPriceRequest: SuggestedPriceRequest) {
        if (NetworkUtils.isInternetAvailable()) {
            try {
                val result = api.getSuggestedPrice(suggestedPriceRequest)
                if (result.isSuccessful)
                    suggestedPriceLiveData.postValue(ResponseHandling.Success(result.body()))
                else
                    suggestedPriceLiveData.postValue(ResponseHandling.Error("Error"))

            } catch (e: Exception) {
                e.printStackTrace()
                suggestedPriceLiveData.postValue(ResponseHandling.Error(e.message))
            }

        } else
            suggestedPriceLiveData.postValue(ResponseHandling.Error("No Internet"))
    }

}
