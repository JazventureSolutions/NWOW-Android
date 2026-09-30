package com.example.nwow.config

import com.example.nwow.ui.auth.model.AdminUserListResponse
import com.example.nwow.ui.auth.model.ChangePasswordRequest
import com.example.nwow.ui.auth.model.ChangePasswordResponse
import com.example.nwow.ui.auth.model.ClearAuthenticatorResponse
import com.example.nwow.ui.auth.model.CreateUserRequest
import com.example.nwow.ui.auth.model.CreateUserResponse
import com.example.nwow.ui.auth.model.DeleteUserResponse
import com.example.nwow.ui.auth.model.UpdateUserRequest
import com.example.nwow.ui.auth.model.UpdateUserResponse
import com.example.nwow.ui.auth.model.LoginRequest
import com.example.nwow.ui.auth.model.LoginResponse
import com.example.nwow.ui.auth.model.LogoutResponse
import com.example.nwow.ui.auth.model.MeResponse
import com.example.nwow.ui.auth.model.PasswordResetRequest
import com.example.nwow.ui.auth.model.PasswordResetResponse
import com.example.nwow.ui.auth.model.ResetPasswordRequest
import com.example.nwow.ui.auth.model.ResetPasswordResponse
import com.example.nwow.ui.auth.model.TotpConfirmRequest
import com.example.nwow.ui.auth.model.TotpConfirmResponse
import com.example.nwow.ui.auth.model.TotpDisableResponse
import com.example.nwow.ui.auth.model.TotpSetupResponse
import com.example.nwow.ui.auth.model.TotpStatusResponse
import com.example.nwow.ui.auth.model.TotpVerifyRequest
import com.example.nwow.ui.auth.model.TotpVerifyResponse
import com.example.nwow.ui.auth.model.VerifyLoginRequest
import com.example.nwow.ui.auth.model.VerifyLoginResponse
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

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("login")
    suspend fun login(
        @Body requestBody: LoginRequest
    ): Response<LoginResponse>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("login/verify")
    suspend fun verifyLogin(
        @Body requestBody: VerifyLoginRequest
    ): Response<VerifyLoginResponse>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("logout")
    suspend fun logout(): Response<LogoutResponse>

    @Headers("Accept: application/json")
    @GET("me")
    suspend fun me(): Response<MeResponse>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("password/change")
    suspend fun changePassword(
        @Body requestBody: ChangePasswordRequest
    ): Response<ChangePasswordResponse>

    @Headers("Accept: application/json")
    @POST("totp/setup")
    suspend fun totpSetup(): Response<TotpSetupResponse>

    @Headers("Accept: application/json")
    @POST("totp/status")
    suspend fun totpStatus(): Response<TotpStatusResponse>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("totp/confirm")
    suspend fun totpConfirm(
        @Body requestBody: TotpConfirmRequest
    ): Response<TotpConfirmResponse>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("totp/disable")
    suspend fun totpDisable(
        @Body requestBody: TotpConfirmRequest
    ): Response<TotpDisableResponse>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("totp/verify")
    suspend fun totpVerify(
        @Body requestBody: TotpVerifyRequest
    ): Response<TotpVerifyResponse>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("password/reset")
    suspend fun resetPassword(
        @Body requestBody: PasswordResetRequest
    ): Response<PasswordResetResponse>

    @Headers("Accept: application/json")
    @GET("admin/users")
    suspend fun adminUsers(): Response<AdminUserListResponse>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("admin/users")
    suspend fun adminCreateUser(
        @Body requestBody: CreateUserRequest
    ): Response<CreateUserResponse>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("admin/users/{id}/reset-password")
    suspend fun adminResetPassword(
        @Path("id") id: Int,
        @Body requestBody: ResetPasswordRequest
    ): Response<ResetPasswordResponse>

    @Headers("Accept: application/json")
    @POST("admin/users/{id}/clear-authenticator")
    suspend fun adminClearAuthenticator(
        @Path("id") id: Int
    ): Response<ClearAuthenticatorResponse>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @PUT("admin/users/{id}")
    suspend fun adminUpdateUser(
        @Path("id") id: Int,
        @Body requestBody: UpdateUserRequest
    ): Response<UpdateUserResponse>

    @Headers("Accept: application/json")
    @DELETE("admin/users/{id}")
    suspend fun adminDeleteUser(
        @Path("id") id: Int
    ): Response<DeleteUserResponse>

}
