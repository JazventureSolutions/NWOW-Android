package com.example.nwow.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.nwow.R
import com.example.nwow.databinding.ActivityLoginBinding
import com.example.nwow.ui.auth.TotpChallengeActivity
import com.example.nwow.ui.auth.TotpResetActivity
import com.example.nwow.ui.auth.TotpSetupActivity
import com.example.nwow.ui.main.MainActivity
import com.example.nwow.utils.NwowApplication
import com.example.nwow.utils.SessionManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: SessionManager
    private var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        binding.loginButton.setOnClickListener {
            attemptLogin()
        }

        binding.togglePassword.setOnClickListener {
            togglePasswordVisibility()
        }

        binding.forgotPasswordLink.setOnClickListener {
            startActivity(Intent(this, TotpResetActivity::class.java))
        }

        binding.password.setOnEditorActionListener { _, _, _ ->
            attemptLogin()
            true
        }
    }

    private fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible

        if (passwordVisible) {
            binding.password.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.togglePassword.setImageResource(R.drawable.visibility_on_icon)
            binding.togglePassword.contentDescription = getString(R.string.hide_password)
        } else {
            binding.password.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.togglePassword.setImageResource(R.drawable.visibility_off_icon)
            binding.togglePassword.contentDescription = getString(R.string.show_password)
        }

        binding.password.setSelection(binding.password.text.length)
    }

    private fun attemptLogin() {
        val username = binding.username.text.toString().trim()
        val password = binding.password.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            showError(getString(R.string.enter_credentials))
            return
        }

        hideError()
        setBusy(true)

        lifecycleScope.launch {
            val result = (application as NwowApplication).authRepository.login(username, password)

            result.onSuccess { login ->
                if (login.token.isNullOrBlank()) {
                    setBusy(false)
                    showError(getString(R.string.incorrect_credentials))
                    return@onSuccess
                }

                session.save(login)

                if (login.must_enrol == true) {
                    startActivity(
                        Intent(this@LoginActivity, TotpSetupActivity::class.java)
                            .putExtra(TotpSetupActivity.EXTRA_ENROLMENT, true)
                    )
                } else if (login.totp_required == true) {
                    // Password accepted, but the authenticator code is still
                    // required before any screen behind the login is shown.
                    startActivity(Intent(this@LoginActivity, TotpChallengeActivity::class.java))
                } else {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                }
                finish()
            }.onFailure { e ->
                setBusy(false)
                showError(e.message ?: getString(R.string.incorrect_credentials))
            }
        }
    }

    private fun setBusy(busy: Boolean) {
        binding.loginButton.isEnabled = !busy
        binding.loginButton.alpha = if (busy) 0.5f else 1f
    }

    private fun showError(message: String) {
        binding.errorText.text = message
        binding.errorText.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.errorText.visibility = View.GONE
    }
}
