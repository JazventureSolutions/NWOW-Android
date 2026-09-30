package com.example.nwow.ui.auth.repository

import com.example.nwow.config.API
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
import com.example.nwow.ui.auth.model.ResetPasswordRequest
import com.example.nwow.ui.auth.model.ResetPasswordResponse
import com.example.nwow.ui.auth.model.VerifyLoginRequest
import com.example.nwow.ui.auth.model.VerifyLoginResponse

class AuthRepository(private val api: API) : BaseApiRepository() {

    suspend fun login(username: String, password: String): Result<LoginResponse> =
        call { api.login(LoginRequest(username, password)) }

    suspend fun verifyLogin(code: String): Result<VerifyLoginResponse> =
        call { api.verifyLogin(VerifyLoginRequest(code)) }

    suspend fun logout(): Result<LogoutResponse> =
        call { api.logout() }

    suspend fun me(): Result<MeResponse> =
        call { api.me() }

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Result<ChangePasswordResponse> =
        call { api.changePassword(ChangePasswordRequest(currentPassword, newPassword)) }

    suspend fun adminUsers(): Result<AdminUserListResponse> =
        call { api.adminUsers() }

    suspend fun adminCreateUser(
        username: String,
        name: String,
        password: String
    ): Result<CreateUserResponse> =
        call { api.adminCreateUser(CreateUserRequest(username, name, password)) }

    suspend fun adminResetPassword(userId: Int, newPassword: String): Result<ResetPasswordResponse> =
        call { api.adminResetPassword(userId, ResetPasswordRequest(newPassword)) }

    suspend fun adminClearAuthenticator(userId: Int): Result<ClearAuthenticatorResponse> =
        call { api.adminClearAuthenticator(userId) }

    suspend fun adminUpdateUser(
        userId: Int,
        username: String,
        name: String,
        password: String?,
        role: String?
    ): Result<UpdateUserResponse> =
        call { api.adminUpdateUser(userId, UpdateUserRequest(username, name, password, role)) }

    suspend fun adminDeleteUser(userId: Int): Result<DeleteUserResponse> =
        call { api.adminDeleteUser(userId) }
}
