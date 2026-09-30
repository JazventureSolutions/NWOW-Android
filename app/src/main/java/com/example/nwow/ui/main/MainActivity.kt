package com.example.nwow.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.nwow.R
import com.example.nwow.databinding.ActivityMainBinding
import com.example.nwow.databinding.NavHeaderMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var navViewHeaderBinding: NavHeaderMainBinding

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