package com.example.nwow.config

import com.example.nwow.ui.main.nwow.model.NwowResponse
import com.example.nwow.ui.main.nwow.model.SuggestedPriceRequest
import com.example.nwow.ui.main.nwow.model.SuggestedPriceResponse
import com.example.nwow.ui.main.zagger.model.PlaceOrderResponse
import com.example.nwow.ui.main.zagger.model.ZaggerResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface API {

    @GET("webshop_products/index/1")
    suspend fun getZaggerData(
        @Header("Apikey") apiKey: String,
        @Query("model_no[]") modelNo: String
    ): Response<ZaggerResponse>

    @GET("products")
    suspend fun getNwowData(
        @Query("model") modelNo: String
    ): Response<NwowResponse>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("suggestedPrice")
    suspend fun getSuggestedPrice(
        @Body suggestedPriceRequest: SuggestedPriceRequest
    ): Response<SuggestedPriceResponse>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("zagerorder")
    suspend fun placeOrder(
        @Body requestBody: RequestBody
    ): Response<PlaceOrderResponse>

}