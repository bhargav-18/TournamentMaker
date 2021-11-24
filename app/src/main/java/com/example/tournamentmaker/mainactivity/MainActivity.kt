package com.example.tournamentmaker.mainactivity

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tournamentmaker.R
import com.example.tournamentmaker.notification.NotificationService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bottomNavBar: BottomNavigationView
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle
    private val users = FirebaseFirestore.getInstance().collection("users")

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
                R.id.joinedTournamentsFragment,
                R.id.addTournamentFragment,
                R.id.searchTournamentFragment
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
            navController.navigate(R.id.action_global_profileFragment)
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        CoroutineScope(Dispatchers.IO).launch {
            FirebaseMessaging.getInstance().token.await().also {
                NotificationService.token = it
                users.document(Firebase.auth.currentUser!!.uid)
                    .update("token", it).await()
            }
        }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        var a = true

        if (toggle.onOptionsItemSelected(item)) {
            navController.addOnDestinationChangedListener { _, destination, _ ->

                when (destination.id) {

                    R.id.setupTournamentFragment -> {
                        a = false
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }

                    R.id.profileFragment -> {
                        a = false
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }

                    R.id.manageParticipantsFragment -> {
                        a = false
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }

                    R.id.createMatchesFragment -> {
                        a = false
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }

                    R.id.resultsFragment -> {
                        a = false
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }

                    R.id.standingsFragment -> {
                        a = false
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }

                    R.id.matchesFragment -> {
                        a = false
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }

                }
            }
        }

        return a
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
const val BASE_URL = "https://fcm.googleapis.com"
const val SERVER_KEY =
    "AAAAFLKFOYI:APA91bGdu5JD9KqfvcMVJR9q0LPGCyRX0HkpFLJgFtg61_u7q4oSP0U6AZmY347renrPimJ4N6_W7LW6NjvV1RIfoALzKC1NdTnVAY7-AjyIQawHgWkLmup6LY6PqRXegD1kqIchnlMs"
const val CONTENT_TYPE = "application/json"
const val TOPIC = "/topics/myTopic2"
