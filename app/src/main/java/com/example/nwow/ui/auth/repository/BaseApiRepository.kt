package com.example.nwow.ui.auth.repository

import com.example.nwow.network_handler.NetworkUtils
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

abstract class BaseApiRepository {

    protected suspend fun <T> call(block: suspend () -> Response<T>): Result<T> {
        if (!NetworkUtils.isInternetAvailable()) {
            return Result.failure(IOException("No Internet"))
        }

        return try {
            val response = block()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                Result.failure(IOException(errorMessage(response)))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private fun <T> errorMessage(response: Response<T>): String {
        val raw = try {
            response.errorBody()?.string().orEmpty()
        } catch (e: Exception) {
            ""
        }

        if (raw.isNotBlank()) {
            try {
                val message = JSONObject(raw).optString("message")
                if (message.isNotBlank()) return message
            } catch (ignored: Exception) {
            }
        }

        return when (response.code()) {
            401 -> "Unauthenticated"
            403 -> "Admin access required"
            404 -> "User not found"
            410 -> "Reset code has expired. Request a new one."
            429 -> "Too many attempts. Please wait a few minutes."
            else -> "Could not reach the server"
        }
    }
}
