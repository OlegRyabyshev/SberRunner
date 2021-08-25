package xyz.fcr.sberrunner.presentation.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import xyz.fcr.sberrunner.R
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import xyz.fcr.sberrunner.databinding.ActivityMainBinding
import xyz.fcr.sberrunner.presentation.view.fragments.main_fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import xyz.fcr.sberrunner.presentation.App
import xyz.fcr.sberrunner.utils.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import xyz.fcr.sberrunner.utils.Constants.TAG_HOME
import xyz.fcr.sberrunner.utils.Constants.TAG_MAP
import xyz.fcr.sberrunner.utils.Constants.TAG_RUN
import xyz.fcr.sberrunner.utils.Constants.TAG_SETTINGS
import xyz.fcr.sberrunner.utils.Constants.TAG_PROGRESS
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var currentFragment: Fragment? = null

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            openScreen(HomeFragment(), TAG_HOME)
        }
    }

    private fun setTheme() {
        PreferenceManager.setDefaultValues(this, R.xml.settings_preference, false)

        App.appComponent.inject(this)

        when (sharedPreferences.getString("theme_key", "0")) {
            Configuration.UI_MODE_NIGHT_UNDEFINED.toString() ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_UNSPECIFIED)
            Configuration.UI_MODE_NIGHT_NO.toString() ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Configuration.UI_MODE_NIGHT_YES.toString() ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> openScreen(HomeFragment(), TAG_HOME)
                R.id.nav_map -> openScreen(MapFragment(), TAG_MAP)
                R.id.nav_progress -> openScreen(ProgressFragment(), TAG_PROGRESS)
                R.id.nav_settings -> openScreen(SettingsFragment(), TAG_SETTINGS)
            }
            true
        }

        binding.fabAction.setOnClickListener {
            openScreen(RunFragment(), TAG_RUN)
            binding.bottomNavigationView.uncheckAllItems()
        }
    }

    private fun BottomNavigationView.uncheckAllItems() {
        menu.setGroupCheckable(0, true, false)

        for (i in 0 until menu.size()) {
            menu.getItem(i).isChecked = false
        }

        menu.setGroupCheckable(0, true, true)
    }

    private fun openScreen(fragmentToOpen: Fragment, tag: String) {
        if (currentFragment?.tag == tag) return
        currentFragment = fragmentToOpen

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, fragmentToOpen, tag)
            .commit()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?) {
        if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            openScreen(RunFragment(), TAG_RUN)
            binding.bottomNavigationView.uncheckAllItems()
        }
    }
}