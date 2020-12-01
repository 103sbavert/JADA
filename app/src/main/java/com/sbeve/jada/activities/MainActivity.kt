package com.sbeve.jada.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sbeve.jada.R
import com.sbeve.jada.util.RetrofitInit
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment).navController
    }
    private val appBarConfiguration: AppBarConfiguration by lazy {
        AppBarConfiguration(navController.graph)
    }

    val applicationSharedPreferences: SharedPreferences by lazy {
        this.getPreferences(Context.MODE_PRIVATE)
    }

    lateinit var mainActivityMenu: Menu

    lateinit var changeLanguageDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(activity_toolbar)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        activity_toolbar.setupWithNavController(navController, appBarConfiguration)
        changeLanguageDialog = MaterialAlertDialogBuilder(this)
            .setTitle("Choose a language")
            .setSingleChoiceItems(
                RetrofitInit.supportedLanguages.first,
                applicationSharedPreferences.getInt(
                    getString(R.string.language_setting_key),
                    0
                )
            ) { dialogInterface, i ->
                applicationSharedPreferences
                    .edit()
                    .putInt(getString(R.string.language_setting_key), i)
                    .apply()
                dialogInterface.dismiss()
            }
            .create()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        mainActivityMenu = menu!!
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
