package com.sirius.net.ptaxi.activities.Main

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import com.shreyaspatil.material.navigationview.MaterialNavigationView
import com.sirius.net.ptaxi.R
import com.sirius.net.ptaxi.activities.Login.LoginActivity
import java.util.*

class MainActivity : AppCompatActivity(){

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val viewModel: MainViewModel by viewModels()
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var SharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_TLink)
        setContentView(R.layout.activity_main)

        SharedPrefs =  getSharedPreferences("TLINK", MODE_PRIVATE)

        stylizeToolBar()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: MaterialNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration =
            AppBarConfiguration(setOf(R.id.nav_order, R.id.nav_profile), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_order -> {
                    navController.navigate(R.id.nav_order)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_offres -> {
                    navController.navigate(R.id.nav_offres)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_reservation -> {
                    navController.navigate(R.id.nav_reservation)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_profile -> {
                    navController.navigate(R.id.nav_profile)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_dcnx -> {
                    SharedPrefs.edit().putBoolean("IS_USER_LOGED", false).apply()
                    SharedPrefs.edit().putString("name", "").apply()
                    SharedPrefs.edit().putString("surname", "").apply()
                    SharedPrefs.edit().putString("mail", "").apply()
                    SharedPrefs.edit().putString("tel1", "").apply()
                    SharedPrefs.edit().putString("tel2", "").apply()
                    SharedPrefs.edit().putString("uid", "").apply()

                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun stylizeToolBar() {
        val background = ResourcesCompat.getDrawable(resources,
                R.drawable.background, theme)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setBackgroundDrawable(background)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}