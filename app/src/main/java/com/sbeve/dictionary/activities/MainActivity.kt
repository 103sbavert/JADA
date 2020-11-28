package com.sbeve.dictionary.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sbeve.dictionary.R
import com.sbeve.dictionary.util.RetrofitInit
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment).navController
    }
    private val appBarConfiguration: AppBarConfiguration by lazy {
        AppBarConfiguration(navController.graph)
    }

    private val activitySharedPreferences: SharedPreferences by lazy {
        this.getSharedPreferences("application", Context.MODE_PRIVATE)
    }

    lateinit var activityMenu: Menu

    val changeLanguageDialog by lazy {
        val savedSetting: Int = activitySharedPreferences.getInt("language_setting_key", 0)
        MaterialAlertDialogBuilder(this)
            .setTitle("Choose a language")
            .setSingleChoiceItems(
                RetrofitInit.supportedLanguages.first,
                savedSetting
            ) { dialogInterface, i ->
                RetrofitInit.changeLanguage(i)
                activitySharedPreferences
                    .edit()
                    .putInt("language_setting_key", i)
                    .apply()
                dialogInterface.dismiss()
            }
            .create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(activity_toolbar)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        activity_toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        activityMenu = menu!!
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.change_language -> changeLanguageDialog.show()
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }
}
