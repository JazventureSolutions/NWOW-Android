package com.example.nwow.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.nwow.R
import com.example.nwow.databinding.ActivityMainBinding
import com.example.nwow.databinding.NavHeaderMainBinding
import com.example.nwow.ui.auth.TotpSetupActivity
import com.example.nwow.ui.changepassword.ChangePasswordActivity
import com.example.nwow.ui.login.LoginActivity
import com.example.nwow.ui.manage.AdminUsersActivity
import com.example.nwow.utils.NwowApplication
import com.example.nwow.utils.SessionManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var navViewHeaderBinding: NavHeaderMainBinding
    private lateinit var session: SessionManager

    companion object {
        lateinit var binding: ActivityMainBinding
        fun changeActivityName(name: String) {
            binding.appbar.activityName.text = name
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        init()

        session = SessionManager(this)
        showIdentity()

        binding.appbar.menuIcon.setOnClickListener {
            openCloseNavigationDrawer()
        }

        navViewHeaderBinding.optionZagger.setOnClickListener {
            navigateToFragments(R.id.zaggerFragment)
            openCloseNavigationDrawer()
        }

        navViewHeaderBinding.optionNwow.setOnClickListener {
            navigateToFragments(R.id.nwowFragment)
            openCloseNavigationDrawer()
        }

        navViewHeaderBinding.optionChangePassword.setOnClickListener {
            openCloseNavigationDrawer()
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        navViewHeaderBinding.optionAuthenticator.setOnClickListener {
            openCloseNavigationDrawer()
            startActivity(Intent(this, TotpSetupActivity::class.java))
        }

        navViewHeaderBinding.optionManageUsers.visibility =
            if (session.isAdmin) View.VISIBLE else View.GONE

        navViewHeaderBinding.optionManageUsers.setOnClickListener {
            openCloseNavigationDrawer()
            startActivity(Intent(this, AdminUsersActivity::class.java))
        }

        navViewHeaderBinding.optionLogout.setOnClickListener {
            openCloseNavigationDrawer()
            performLogout()
        }
    }

    private fun showIdentity() {
        val username = session.username.orEmpty()
        val name = session.name?.trim().orEmpty()
        val displayName = if (name.isNotEmpty()) name else username

        binding.appbar.userName.text = displayName
        binding.appbar.userUsername.text = username

        navViewHeaderBinding.headerUserName.text = displayName
        navViewHeaderBinding.headerUserUsername.text = username
        navViewHeaderBinding.headerUserRole.text = getString(
            if (session.isAdmin) R.string.role_admin else R.string.role_user
        )
    }

    private fun performLogout() {
        lifecycleScope.launch {
            runCatching { (application as NwowApplication).authRepository.logout() }
        }
        session.clear()
        startActivity(
            Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        finish()
    }

    private fun init() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        navController = navHostFragment.navController

        val viewHeader = binding.navView.getHeaderView(0)
        navViewHeaderBinding = NavHeaderMainBinding.bind(viewHeader)
    }

    private fun navigateToFragments(fragment: Int) {
        navController.navigateUp()
        navController.navigate(fragment)
    }

    private fun openCloseNavigationDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        else
            binding.drawerLayout.openDrawer(GravityCompat.START)
    }

}
