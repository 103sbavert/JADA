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

    //public reference to the sharedPreferences for access in children fragments
    val applicationSharedPreferences: SharedPreferences by lazy {
        this.getPreferences(Context.MODE_PRIVATE)
    }

    //public reference to the inflated menu in the toolbar for access in children fragments
    lateinit var mainActivityMenu: Menu

    //public reference to the created alert dialog for access in children fragments
    lateinit var changeLanguageDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(activity_toolbar)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        activity_toolbar.setupWithNavController(navController, appBarConfiguration)

        //creating the alert dialog inside onCreateView instead of lazily to avoid the lag that
        //appears when the dialog is invoked for the first time
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
        //setting a reference to the toolbar menu to the public variable declared above
        mainActivityMenu = menu!!
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //invoke the changeLanguageDialog when the user selects the change_language menu item
        when (item.itemId) {
            R.id.change_language -> changeLanguageDialog.show()
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }
}
