package com.example.tournamentmaker.authactivity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.tournamentmaker.R

class AuthActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_auth) as NavHostFragment
        navController = navHostFragment.findNavController()


        setupActionBarWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.authFragment) {
                actionBar?.hide()
            } else {
                actionBar?.show()
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

const val AUTH_RESULT_OK = Activity.RESULT_FIRST_USER