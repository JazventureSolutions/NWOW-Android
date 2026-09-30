package com.example.nwow.ui.manage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.nwow.R
import com.example.nwow.databinding.ActivityAdminUsersBinding
import com.example.nwow.databinding.DialogEditUserBinding
import com.example.nwow.databinding.ItemUserBinding
import com.example.nwow.ui.auth.model.AdminUser
import com.example.nwow.utils.NwowApplication
import com.example.nwow.utils.SessionManager
import kotlinx.coroutines.launch

class AdminUsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminUsersBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityAdminUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        if (!session.isAdmin) {
            finish()
            return
        }

        binding.backIcon.setOnClickListener { finish() }

        binding.addUserToggle.setOnClickListener {
            val section = binding.addUserSection
            section.visibility = if (section.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        binding.createUserButton.setOnClickListener { createUser() }

        loadUsers()
    }

    private fun createUser() {
        val username = binding.createUserUsername.text.toString().trim()
        val name = binding.createUserName.text.toString().trim()
        val password = binding.createUserPassword.text.toString().trim()

        if (username.isEmpty() || name.isEmpty() || password.isEmpty()) {
            showError(getString(R.string.enter_all_fields))
            return
        }

        if (password.length < 6) {
            showError(getString(R.string.passwords_too_short))
            return
        }

        hideMessages()
        binding.createUserButton.isEnabled = false

        lifecycleScope.launch {
            val result = repository().adminCreateUser(username, name, password)
            binding.createUserButton.isEnabled = true

            result.onSuccess {
                binding.createUserUsername.text.clear()
                binding.createUserName.text.clear()
                binding.createUserPassword.text.clear()
                binding.addUserSection.visibility = View.GONE
                showStatus(getString(R.string.user_created))
                loadUsers()
            }.onFailure { e ->
                showError(e.message ?: getString(R.string.generic_network_error))
            }
        }
    }

    private fun loadUsers() {
        lifecycleScope.launch {
            val result = repository().adminUsers()

            result.onSuccess { response ->
                render(response.data.orEmpty())
            }.onFailure { e ->
                showError(e.message ?: getString(R.string.generic_network_error))
            }
        }
    }

    private fun render(users: List<AdminUser>) {
        binding.userList.removeAllViews()
        binding.emptyText.visibility = if (users.isEmpty()) View.VISIBLE else View.GONE

        users.forEach { user ->
            val row = ItemUserBinding.inflate(layoutInflater, binding.userList, false)

            row.userItemUsername.text = user.username.orEmpty()
            row.userItemName.text = user.name.orEmpty()
            row.userItemRole.text = getString(
                if (user.role == SessionManager.ROLE_ADMIN) R.string.role_admin else R.string.role_user
            )
            row.userItemAuth.text = getString(
                if (user.has_authenticator == true) R.string.authenticator_set
                else R.string.authenticator_not_set
            )

            row.userItemResetButton.setOnClickListener { resetPassword(user, row) }

            val hasAuth = user.has_authenticator == true
            row.userItemClearButton.isEnabled = hasAuth
            row.userItemClearButton.alpha = if (hasAuth) 1f else 0.4f
            row.userItemClearButton.setOnClickListener { clearAuthenticator(user, row) }

            row.userItemEditButton.setOnClickListener { showEditDialog(user) }

            val isSelf = user.username == session.username
            row.userItemDeleteButton.isEnabled = !isSelf
            row.userItemDeleteButton.alpha = if (isSelf) 0.4f else 1f
            row.userItemDeleteButton.setOnClickListener {
                if (isSelf) {
                    showError(getString(R.string.cannot_delete_self))
                } else {
                    confirmDelete(user)
                }
            }

            binding.userList.addView(row.root)
        }
    }

    private fun showEditDialog(user: AdminUser) {
        hideMessages()
        val dialogBinding = DialogEditUserBinding.inflate(layoutInflater)
        dialogBinding.editUserName.setText(user.name.orEmpty())
        dialogBinding.editUserUsername.setText(user.username.orEmpty())
        if (user.role == SessionManager.ROLE_ADMIN) {
            dialogBinding.editRoleAdmin.isChecked = true
        } else {
            dialogBinding.editRoleUser.isChecked = true
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.edit_user_title)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.save) { _, _ -> saveUser(user, dialogBinding) }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun saveUser(user: AdminUser, dialogBinding: DialogEditUserBinding) {
        val name = dialogBinding.editUserName.text.toString().trim()
        val username = dialogBinding.editUserUsername.text.toString().trim()
        val password = dialogBinding.editUserPassword.text.toString().trim()
        val role =
            if (dialogBinding.editRoleAdmin.isChecked) SessionManager.ROLE_ADMIN else "user"

        if (name.isEmpty() || username.isEmpty()) {
            showError(getString(R.string.enter_all_fields))
            return
        }

        if (password.isNotEmpty() && password.length < 6) {
            showError(getString(R.string.passwords_too_short))
            return
        }

        hideMessages()

        lifecycleScope.launch {
            val result = repository().adminUpdateUser(
                user.id,
                username,
                name,
                password.ifEmpty { null },
                role
            )

            result.onSuccess {
                showStatus(getString(R.string.user_updated))
                loadUsers()
            }.onFailure { e ->
                showError(e.message ?: getString(R.string.generic_network_error))
            }
        }
    }

    private fun confirmDelete(user: AdminUser) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_user)
            .setMessage(getString(R.string.confirm_delete_user, user.username.orEmpty()))
            .setPositiveButton(R.string.delete_user) { _, _ -> deleteUser(user) }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun deleteUser(user: AdminUser) {
        hideMessages()

        lifecycleScope.launch {
            val result = repository().adminDeleteUser(user.id)

            result.onSuccess {
                showStatus(getString(R.string.user_deleted, user.username.orEmpty()))
                loadUsers()
            }.onFailure { e ->
                showError(e.message ?: getString(R.string.generic_network_error))
            }
        }
    }

    private fun clearAuthenticator(user: AdminUser, row: ItemUserBinding) {
        hideMessages()
        row.userItemClearButton.isEnabled = false

        lifecycleScope.launch {
            val result = repository().adminClearAuthenticator(user.id)
            row.userItemClearButton.isEnabled = true

            result.onSuccess {
                showStatus(getString(R.string.authenticator_cleared, user.username.orEmpty()))
                loadUsers()
            }.onFailure { e ->
                showError(e.message ?: getString(R.string.generic_network_error))
            }
        }
    }

    private fun resetPassword(user: AdminUser, row: ItemUserBinding) {
        val password = row.userItemPassword.text.toString().trim()

        if (password.length < 6) {
            showError(getString(R.string.passwords_too_short))
            return
        }

        hideMessages()
        row.userItemResetButton.isEnabled = false

        lifecycleScope.launch {
            val result = repository().adminResetPassword(user.id, password)
            row.userItemResetButton.isEnabled = true

            result.onSuccess {
                row.userItemPassword.text.clear()
                showStatus(getString(R.string.password_reset, user.username.orEmpty()))
                loadUsers()
            }.onFailure { e ->
                showError(e.message ?: getString(R.string.generic_network_error))
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showStatus(message: String) {
        binding.statusText.text = message
        binding.statusText.visibility = View.VISIBLE
        showMessage(message)
    }

    private fun showError(message: String) {
        binding.errorText.text = message
        binding.errorText.visibility = View.VISIBLE
    }

    private fun hideMessages() {
        binding.errorText.visibility = View.GONE
        binding.statusText.visibility = View.GONE
    }

    private fun repository() = (application as NwowApplication).authRepository
}
