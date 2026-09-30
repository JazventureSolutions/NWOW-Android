package com.example.nwow.ui.main.zagger.model


import com.google.gson.annotations.SerializedName

data class PlaceOrderResponse(
    @SerializedName("error")
    var error: List<String?>?,
    @SerializedName("success")
    var success: String?
)