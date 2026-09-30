package com.example.nwow.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.nwow.R
import com.example.nwow.databinding.ActivityTotpResetBinding
import com.example.nwow.ui.login.LoginActivity
import com.example.nwow.utils.NwowApplication
import com.example.nwow.utils.SessionManager
import kotlinx.coroutines.launch

class TotpResetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTotpResetBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityTotpResetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        session.username?.let { binding.resetUsername.setText(it) }

        binding.backIcon.setOnClickListener { finish() }
        binding.resetButton.setOnClickListener { attemptReset() }
        binding.toggleNew.setOnClickListener {
            toggleVisibility(binding.newPassword, binding.toggleNew)
        }
        binding.toggleConfirm.setOnClickListener {
            toggleVisibility(binding.confirmPassword, binding.toggleConfirm)
        }
    }

    private fun toggleVisibility(field: EditText, icon: ImageView) {
        val isHidden = field.transformationMethod is PasswordTransformationMethod

        if (isHidden) {
            field.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            icon.setImageResource(R.drawable.visibility_on_icon)
            icon.contentDescription = getString(R.string.hide_password)
        } else {
            field.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            icon.setImageResource(R.drawable.visibility_off_icon)
            icon.contentDescription = getString(R.string.show_password)
        }
        field.setSelection(field.text.length)
    }

    private fun attemptReset() {
        val username = binding.resetUsername.text.toString().trim()
        val code = binding.codeInput.text.toString().trim()
        val newPass = binding.newPassword.text.toString().trim()
        val confirm = binding.confirmPassword.text.toString().trim()

        if (username.isEmpty() || code.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            showError(getString(R.string.enter_all_fields))
            return
        }

        if (code.length != 6) {
            showError(getString(R.string.code_required))
            return
        }

        if (newPass != confirm) {
            showError(getString(R.string.passwords_do_not_match))
            return
        }

        if (newPass.length < 6) {
            showError(getString(R.string.passwords_too_short))
            return
        }

        hideError()
        binding.resetButton.isEnabled = false
        binding.resetButton.alpha = 0.5f

        lifecycleScope.launch {
            val verified = repository().verify(username, code)

            verified.onSuccess { tokenResponse ->
                val token = tokenResponse.reset_token
                if (token.isNullOrBlank()) {
                    finishResetUi()
                    showError(tokenResponse.message ?: getString(R.string.invalid_code))
                    return@onSuccess
                }

                applyNewPassword(username, token, newPass)
            }.onFailure { e ->
                finishResetUi()
                showError(e.message ?: getString(R.string.invalid_code))
            }
        }
    }

    private suspend fun applyNewPassword(username: String, token: String, newPass: String) {
        val reset = repository().resetPassword(username, token, newPass)

        reset.onSuccess {
            session.clear()
            finishResetUi()
            Toast.makeText(this, R.string.password_reset_done, Toast.LENGTH_LONG).show()
            startActivity(
                Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
            finish()
        }.onFailure { e ->
            finishResetUi()
            showError(e.message ?: getString(R.string.generic_network_error))
        }
    }

    private fun finishResetUi() {
        binding.resetButton.isEnabled = true
        binding.resetButton.alpha = 1f
    }

    private fun showError(message: String) {
        binding.errorText.text = message
        binding.errorText.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.errorText.visibility = View.GONE
    }

    private fun repository() = (application as NwowApplication).totpRepository
}
