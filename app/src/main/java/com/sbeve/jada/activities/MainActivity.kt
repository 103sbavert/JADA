package com.sbeve.jada.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.sbeve.jada.R
import com.sbeve.jada.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment).navController
    }
    private val appBarConfiguration: AppBarConfiguration by lazy {
        AppBarConfiguration(navController.graph)
    }

    //public reference to the sharedPreferences for access in children fragments
    val applicationPreferences: SharedPreferences by lazy {
        getPreferences(Context.MODE_PRIVATE)
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }
}
