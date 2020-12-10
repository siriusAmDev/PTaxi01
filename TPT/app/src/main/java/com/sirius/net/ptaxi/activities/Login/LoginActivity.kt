package com.sirius.net.ptaxi.activities.Login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.findNavController
import com.sirius.net.ptaxi.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_TLink)
        setContentView(R.layout.activity_login)
        stylizeToolBar()

        val navController = findNavController(R.id.nav_host_fragment_login)
    }

    private fun stylizeToolBar() {
        val background = ResourcesCompat.getDrawable(resources,
            R.drawable.background, theme)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setBackgroundDrawable(background)
    }
    //a comment
}