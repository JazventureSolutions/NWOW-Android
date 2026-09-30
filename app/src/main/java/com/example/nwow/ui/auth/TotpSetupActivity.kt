package com.example.nwow.ui.auth

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.nwow.R
import com.example.nwow.databinding.ActivityTotpSetupBinding
import com.example.nwow.ui.login.LoginActivity
import com.example.nwow.ui.main.MainActivity
import com.example.nwow.utils.NwowApplication
import com.example.nwow.utils.SessionManager
import kotlinx.coroutines.launch

class TotpSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTotpSetupBinding
    private lateinit var session: SessionManager

    private var setupSecret: String? = null
    private var enrolmentMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityTotpSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        enrolmentMode = intent.getBooleanExtra(EXTRA_ENROLMENT, false)

        val name = session.name?.trim().orEmpty()
        val displayName = if (name.isNotEmpty()) name else session.username.orEmpty()
        binding.signedInAs.text = getString(R.string.signed_in_as, displayName)

        binding.backIcon.setOnClickListener {
            if (enrolmentMode) {
                leaveWithoutSetup()
            } else {
                finish()
            }
        }
        binding.getKeyButton.setOnClickListener { generateKey() }
        binding.copyKeyButton.setOnClickListener { copyKey() }
        binding.activateButton.setOnClickListener { activate() }
        binding.doneButton.setOnClickListener { onDone() }
        binding.removeButton.setOnClickListener { removeAuthenticator() }

        if (enrolmentMode) {
            binding.setupRequiredNote.visibility = View.VISIBLE
            generateKey()
        } else {
            loadStatus()
        }
    }

    override fun onBackPressed() {
        if (enrolmentMode) {
            leaveWithoutSetup()
        } else {
            super.onBackPressed()
        }
    }

    private fun leaveWithoutSetup() {
        Toast.makeText(this, R.string.logged_out_setup_needed, Toast.LENGTH_LONG).show()
        goToLogin()
    }

    private fun goToLogin() {
        session.clear()
        startActivity(
            Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        finish()
    }

    private fun onDone() {
        if (enrolmentMode) {
            startActivity(
                Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
            finish()
        } else {
            finish()
        }
    }

    private fun loadStatus() {
        lifecycleScope.launch {
            val result = repository().status()
            result.onSuccess { status ->
                if (status.enabled) showAlreadyActive()
            }.onFailure { }
        }
    }

    private fun generateKey() {
        hideError()
        binding.getKeyButton.isEnabled = false

        lifecycleScope.launch {
            val result = repository().setup()
            binding.getKeyButton.isEnabled = true

            result.onSuccess { data ->
                setupSecret = data.secret
                binding.secretText.text = groupSecret(data.secret)
                binding.instructionText.text =
                    getString(R.string.scan_or_enter, data.account)
                binding.secretSection.visibility = View.VISIBLE
                binding.codeInput.visibility = View.VISIBLE
                binding.activateButton.visibility = View.VISIBLE
                binding.codeInput.text.clear()
                binding.codeInput.requestFocus()
            }.onFailure { e ->
                showError(e.message ?: getString(R.string.generic_network_error))
            }
        }
    }

    private fun activate() {
        val secret = setupSecret
        val code = binding.codeInput.text.toString().trim()

        if (secret == null) {
            showError(getString(R.string.session_expired))
            return
        }

        if (code.length != 6) {
            showError(getString(R.string.code_required))
            return
        }

        hideError()
        binding.activateButton.isEnabled = false

        lifecycleScope.launch {
            val result = repository().confirm(code)
            binding.activateButton.isEnabled = true

            result.onSuccess { data ->
                binding.codeInput.text.clear()
                showBackupCodes(data.backup_codes)
            }.onFailure { e ->
                showError(e.message ?: getString(R.string.invalid_code))
            }
        }
    }

    private fun removeAuthenticator() {
        val code = binding.removeCodeInput.text.toString().trim()

        if (code.length < 4) {
            showError(getString(R.string.code_required))
            return
        }

        hideError()
        binding.removeButton.isEnabled = false

        lifecycleScope.launch {
            val result = repository().disable(code)
            binding.removeButton.isEnabled = true

            result.onSuccess {
                binding.removeCodeInput.text.clear()
                Toast.makeText(this@TotpSetupActivity, R.string.authenticator_removed, Toast.LENGTH_LONG).show()
                showCanEnrol()
            }.onFailure { e ->
                binding.removeCodeInput.text.clear()
                showError(e.message ?: getString(R.string.invalid_code))
            }
        }
    }

    private fun copyKey() {
        val secret = setupSecret ?: return
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("NWOW setup key", secret))
        Toast.makeText(this, R.string.key_copied, Toast.LENGTH_SHORT).show()
    }

    private fun showBackupCodes(codes: List<String>?) {
        val display = codes.orEmpty().joinToString("\n")

        binding.secretSection.visibility = View.GONE
        binding.codeInput.visibility = View.GONE
        binding.activateButton.visibility = View.GONE
        binding.errorText.visibility = View.GONE
        binding.getKeyButton.visibility = View.GONE

        binding.backupCodesText.text = display
        binding.backupSection.visibility = View.VISIBLE

        Toast.makeText(this, R.string.authenticator_activated, Toast.LENGTH_LONG).show()
    }

    private fun showAlreadyActive() {
        binding.activeNote.visibility = View.VISIBLE
        binding.getKeyButton.visibility = View.GONE
        binding.removeSection.visibility = View.VISIBLE
    }

    private fun showCanEnrol() {
        binding.activeNote.visibility = View.GONE
        binding.removeSection.visibility = View.GONE
        binding.getKeyButton.visibility = View.VISIBLE
        binding.getKeyButton.isEnabled = true
        binding.getKeyButton.alpha = 1f
        binding.secretSection.visibility = View.GONE
        binding.codeInput.visibility = View.GONE
        binding.activateButton.visibility = View.GONE
        binding.backupSection.visibility = View.GONE
        setupSecret = null
    }

    private fun groupSecret(secret: String) =
        secret.chunked(4).joinToString(" ")

    private fun showError(message: String) {
        binding.errorText.text = message
        binding.errorText.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.errorText.visibility = View.GONE
    }

    private fun repository() = (application as NwowApplication).totpRepository

    companion object {
        const val EXTRA_ENROLMENT = "enrolment_mode"
    }
}
