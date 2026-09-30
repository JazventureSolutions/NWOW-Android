package com.example.nwow.ui.auth.model

data class TotpSetupResponse(
    val username: String,
    val account: String,
    val issuer: String,
    val secret: String,
    val otpauth_url: String,
    val digits: Int,
    val period: Int,
    val enabled: Boolean
)

data class TotpStatusResponse(
    val enabled: Boolean,
    val setup_started: Boolean
)

data class TotpConfirmRequest(
    val code: String
)

data class TotpConfirmResponse(
    val enabled: Boolean,
    val backup_codes: List<String>?,
    val message: String?
)

data class TotpVerifyRequest(
    val username: String,
    val code: String
)

data class TotpVerifyResponse(
    val reset_token: String?,
    val expires_in: Int?,
    val message: String?
)

data class PasswordResetRequest(
    val username: String,
    val reset_token: String,
    val new_password: String
)

data class PasswordResetResponse(
    val success: Boolean?,
    val message: String?
)
