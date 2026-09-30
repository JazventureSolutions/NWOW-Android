package com.example.nwow.ui.main.nwow.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nwow.ui.main.nwow.model.NwowResponse
import com.example.nwow.ui.main.nwow.model.SuggestedPriceRequest
import com.example.nwow.ui.main.nwow.model.SuggestedPriceResponse
import com.example.nwow.ui.main.nwow.repository.NwowRepository
import com.example.nwow.utils.ResponseHandling
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NwowViewModel(private val repository: NwowRepository) : ViewModel() {

    fun getResponse(modelNo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getData(modelNo)
        }
    }

    val nwowResponse: MutableLiveData<ResponseHandling<NwowResponse>>
        get() = repository.nwowResponse


    fun getSuggestedPrice(suggestedPriceRequest: SuggestedPriceRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getSuggestedPrice(suggestedPriceRequest)
        }
    }

    val suggestedPriceResponse: MutableLiveData<ResponseHandling<SuggestedPriceResponse>>
        get() = repository.suggestedPriceResponse

    fun flushVariables() {
        nwowResponse.value = null
    }

    fun flushSuggestedPriceVariables() {
        suggestedPriceResponse.value = null
    }

}