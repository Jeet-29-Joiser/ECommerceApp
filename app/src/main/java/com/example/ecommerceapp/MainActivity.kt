package com.example.ecommerceapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.ecommerceapp.ui.auth.AuthActivity
import com.example.ecommerceapp.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val sessionManager = SessionManager(this)
        val tvUserAction = findViewById<TextView>(R.id.tvUserAction)

        updateUserAction(tvUserAction, sessionManager)

        tvUserAction.setOnClickListener {
            if (sessionManager.isLoggedIn()) {
                showLogoutDialog(sessionManager, tvUserAction, navController)
            } else {
                startActivity(Intent(this, AuthActivity::class.java))
            }
        }

        findViewById<android.view.View>(R.id.navHome).setOnClickListener {
            navController.navigate(R.id.homeFragment)
        }

        findViewById<android.view.View>(R.id.navCategories).setOnClickListener {
            navController.navigate(R.id.allCategoriesFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        val sessionManager = SessionManager(this)
        val tvUserAction = findViewById<TextView>(R.id.tvUserAction)
        updateUserAction(tvUserAction, sessionManager)
    }

    private fun updateUserAction(tvUserAction: TextView, sessionManager: SessionManager) {
        if (sessionManager.isLoggedIn()) {
            tvUserAction.text = sessionManager.getUserName()
        } else {
            tvUserAction.text = "Login"
        }
    }

    private fun showLogoutDialog(
        sessionManager: SessionManager,
        tvUserAction: TextView,
        navController: androidx.navigation.NavController
    ) {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Do you want to log out?")
            .setPositiveButton("Yes") { _, _ ->
                sessionManager.logout()
                updateUserAction(tvUserAction, sessionManager)
                navController.navigate(R.id.homeFragment)
            }
            .setNegativeButton("No", null)
            .show()
    }
}