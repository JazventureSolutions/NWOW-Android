package com.example.nwow.ui.auth.repository

import com.example.nwow.config.API
import com.example.nwow.ui.auth.model.PasswordResetRequest
import com.example.nwow.ui.auth.model.PasswordResetResponse
import com.example.nwow.ui.auth.model.TotpConfirmRequest
import com.example.nwow.ui.auth.model.TotpConfirmResponse
import com.example.nwow.ui.auth.model.TotpDisableResponse
import com.example.nwow.ui.auth.model.TotpSetupResponse
import com.example.nwow.ui.auth.model.TotpStatusResponse
import com.example.nwow.ui.auth.model.TotpVerifyRequest
import com.example.nwow.ui.auth.model.TotpVerifyResponse

class TotpRepository(private val api: API) : BaseApiRepository() {

    suspend fun setup(): Result<TotpSetupResponse> =
        call { api.totpSetup() }

    suspend fun status(): Result<TotpStatusResponse> =
        call { api.totpStatus() }

    suspend fun confirm(code: String): Result<TotpConfirmResponse> =
        call { api.totpConfirm(TotpConfirmRequest(code)) }

    suspend fun disable(code: String): Result<TotpDisableResponse> =
        call { api.totpDisable(TotpConfirmRequest(code)) }

    suspend fun verify(username: String, code: String): Result<TotpVerifyResponse> =
        call { api.totpVerify(TotpVerifyRequest(username, code)) }

    suspend fun resetPassword(
        username: String,
        resetToken: String,
        newPassword: String
    ): Result<PasswordResetResponse> =
        call { api.resetPassword(PasswordResetRequest(username, resetToken, newPassword)) }
}
