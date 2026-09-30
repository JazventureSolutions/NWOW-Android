package com.example.nwow.ui.auth.model

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String?,
    val username: String?,
    val name: String?,
    val role: String?,
    val must_enrol: Boolean?,
    val totp_required: Boolean?
)

data class VerifyLoginRequest(
    val code: String
)

data class VerifyLoginResponse(
    val success: Boolean?,
    val verified: Boolean?
)

data class TotpDisableResponse(
    val success: Boolean?,
    val message: String?
)

data class ClearAuthenticatorResponse(
    val success: Boolean?,
    val message: String?
)

data class MeResponse(
    val username: String?,
    val name: String?,
    val role: String?,
    val must_enrol: Boolean?
)

data class LogoutResponse(
    val success: Boolean?
)

data class ChangePasswordRequest(
    val current_password: String,
    val new_password: String
)

data class ChangePasswordResponse(
    val success: Boolean?,
    val message: String?
)

data class AdminUser(
    val id: Int,
    val username: String?,
    val name: String?,
    val role: String?,
    val has_authenticator: Boolean?
)

data class AdminUserListResponse(
    val data: List<AdminUser>?
)

data class CreateUserRequest(
    val username: String,
    val name: String,
    val password: String
)

data class CreateUserResponse(
    val success: Boolean?,
    val user: AdminUser?
)

data class ResetPasswordRequest(
    val new_password: String
)

data class ResetPasswordResponse(
    val success: Boolean?,
    val message: String?
)

data class UpdateUserRequest(
    val username: String,
    val name: String,
    val password: String? = null,
    val role: String? = null
)

data class UpdateUserResponse(
    val success: Boolean?,
    val user: AdminUser?
)

data class DeleteUserResponse(
    val success: Boolean?,
    val message: String?
)
