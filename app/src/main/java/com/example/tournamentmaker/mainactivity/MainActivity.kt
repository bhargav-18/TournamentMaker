package com.example.tournamentmaker.mainactivity

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tournamentmaker.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bottomNavBar: BottomNavigationView
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        toggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close)

        bottomNavBar = findViewById(R.id.bottom_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        navController = navHostFragment.findNavController()

        val navView = findViewById<NavigationView>(R.id.side_nav_view)

        appBarConfiguration = AppBarConfiguration.Builder(
            setOf(
                R.id.homeFragment,
                R.id.addTournamentFragment,
                R.id.searchTournamentFragment,
                R.id.signOutDialogFragment
            )
        ).setOpenableLayout(drawerLayout)
            .build()

        setupActionBarWithNavController(navController)
        bottomNavBar.setupWithNavController(navController)
        navView.setupWithNavController(navController)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(navView, navController)

        val headerView: View = navView.getHeaderView(0)
        val userEmail = headerView.findViewById<TextView>(R.id.text_view_user_email)
        val userName = headerView.findViewById<TextView>(R.id.text_view_user_name)
        val profile = headerView.findViewById<ConstraintLayout>(R.id.layout_profile_user)

        userEmail.text = Firebase.auth.currentUser?.email ?: "guest@gmail.com"
        userName.text = Firebase.auth.currentUser?.displayName ?: "Guest"
        profile.setOnClickListener {
            TODO()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            navController,
            appBarConfiguration
        ) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (bottomNavBar.selectedItemId == R.id.addTournamentFragment || bottomNavBar.selectedItemId == R.id.searchTournamentFragment)
            finishAffinity()
        else
            super.onBackPressed()
    }

}

const val MAIN_RESULT_OK = Activity.RESULT_FIRST_USER + 1