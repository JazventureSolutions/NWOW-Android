package com.example.nwow.ui.main.zagger.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nwow.ui.main.zagger.model.PlaceOrderResponse
import com.example.nwow.ui.main.zagger.model.ZaggerResponse
import com.example.nwow.ui.main.zagger.repository.ZaggerRepository
import com.example.nwow.utils.ResponseHandling
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.RequestBody

class ZaggerViewModel(private val repository: ZaggerRepository) : ViewModel() {

    fun getResponse(modelNo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getData(modelNo)
        }
    }

    val zaggerResponse: MutableLiveData<ResponseHandling<ZaggerResponse>>
        get() = repository.zaggerResponse

    fun flushVariables() {
        zaggerResponse.value = null
    }

    fun placeOrder(requestBody: RequestBody) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.placeOrder(requestBody)
        }
    }

    val placeOrderResponse: MutableLiveData<ResponseHandling<PlaceOrderResponse>>
        get() = repository.placeOrderResponse

    fun flushPlaceOrderVariables() {
        placeOrderResponse.value = null
    }
}