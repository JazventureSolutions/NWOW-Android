package com.example.nwow.ui.changepassword

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
import com.example.nwow.databinding.ActivityChangePasswordBinding
import com.example.nwow.ui.auth.TotpResetActivity
import com.example.nwow.ui.login.LoginActivity
import com.example.nwow.utils.NwowApplication
import com.example.nwow.utils.SessionManager
import kotlinx.coroutines.launch

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        binding.backIcon.setOnClickListener { finish() }

        binding.changeButton.setOnClickListener { attemptChange() }

        binding.forgotPasswordLink.setOnClickListener {
            startActivity(Intent(this, TotpResetActivity::class.java))
        }

        binding.toggleCurrent.setOnClickListener {
            toggleVisibility(binding.currentPassword, binding.toggleCurrent)
        }
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

    private fun attemptChange() {
        val current = binding.currentPassword.text.toString().trim()
        val newPass = binding.newPassword.text.toString().trim()
        val confirm = binding.confirmPassword.text.toString().trim()

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            showError(getString(R.string.enter_all_fields))
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
        setBusy(true)

        lifecycleScope.launch {
            val result = (application as NwowApplication).authRepository
                .changePassword(current, newPass)

            setBusy(false)

            result.onSuccess {
                session.clear()
                Toast.makeText(this@ChangePasswordActivity, R.string.password_changed, Toast.LENGTH_LONG).show()

                startActivity(
                    Intent(this@ChangePasswordActivity, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
                finish()
            }.onFailure { e ->
                showError(e.message ?: getString(R.string.password_change_failed))
            }
        }
    }

    private fun setBusy(busy: Boolean) {
        binding.changeButton.isEnabled = !busy
        binding.changeButton.alpha = if (busy) 0.5f else 1f
    }

    private fun showError(message: String) {
        binding.errorText.text = message
        binding.errorText.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.errorText.visibility = View.GONE
    }
}
