package com.sbeve.jada.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.sbeve.jada.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    
    private val navController by lazy {
        findNavController(binding.mainNavHost.id)
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

/*
    private fun showSoftKeyboard(view: View) {
        val imm: InputMethodManager = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, 0)
    }
*/
}
