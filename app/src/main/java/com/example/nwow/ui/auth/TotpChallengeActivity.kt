package com.example.nwow.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.nwow.R
import com.example.nwow.databinding.ActivityTotpChallengeBinding
import com.example.nwow.ui.login.LoginActivity
import com.example.nwow.ui.main.MainActivity
import com.example.nwow.utils.NwowApplication
import com.example.nwow.utils.SessionManager
import kotlinx.coroutines.launch

/**
 * Second half of login: the password was already accepted, the 6-digit
 * authenticator code must still be entered before any screen is shown.
 */
class TotpChallengeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTotpChallengeBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityTotpChallengeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        val name = session.name?.trim().orEmpty()
        val displayName = if (name.isNotEmpty()) name else session.username.orEmpty()
        binding.challengeName.text = displayName

        binding.backIcon.setOnClickListener { cancel() }
        binding.verifyButton.setOnClickListener { verify() }
        binding.cancelButton.setOnClickListener { cancel() }
    }

    override fun onBackPressed() {
        cancel()
    }

    private fun cancel() {
        session.clear()
        startActivity(
            Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        finish()
    }

    private fun verify() {
        val code = binding.codeInput.text.toString().trim()

        if (code.length != 6) {
            showError(getString(R.string.code_required))
            return
        }

        hideError()
        setBusy(true)

        lifecycleScope.launch {
            val result = (application as NwowApplication).authRepository.verifyLogin(code)
            setBusy(false)

            result.onSuccess {
                startActivity(
                    Intent(this@TotpChallengeActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
                finish()
            }.onFailure { e ->
                binding.codeInput.text.clear()
                binding.codeInput.requestFocus()
                showError(e.message ?: getString(R.string.invalid_code))
            }
        }
    }

    private fun setBusy(busy: Boolean) {
        binding.verifyButton.isEnabled = !busy
        binding.verifyButton.alpha = if (busy) 0.5f else 1f
    }

    private fun showError(message: String) {
        binding.errorText.text = message
        binding.errorText.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.errorText.visibility = View.GONE
    }
}
