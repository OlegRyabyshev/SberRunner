package xyz.fcr.sberrunner.presentation.view.activities

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.databinding.ActivityMainBinding
import xyz.fcr.sberrunner.presentation.App
import xyz.fcr.sberrunner.presentation.view.fragments.main_fragments.*
import xyz.fcr.sberrunner.utils.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import xyz.fcr.sberrunner.utils.Constants.TAG_HOME
import xyz.fcr.sberrunner.utils.Constants.TAG_MAP
import xyz.fcr.sberrunner.utils.Constants.TAG_PROGRESS
import xyz.fcr.sberrunner.utils.Constants.TAG_RUN
import xyz.fcr.sberrunner.utils.Constants.TAG_SETTINGS
import xyz.fcr.sberrunner.utils.Constants.THEME_KEY
import javax.inject.Inject

/**
 * Главное активити. Содержит в себе BottomNavigationView с фрагментами (Home, Map, Run, Progress, Settings)
 */
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

        if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            openScreen(RunFragment(), TAG_RUN)
        } else if (savedInstanceState == null) {
            openScreen(HomeFragment(), TAG_HOME)
        }
    }

    /**
     * Установка темы (значение берется из SharedPreference)
     */
    private fun setTheme() {
        PreferenceManager.setDefaultValues(this, R.xml.settings_preference, false)

        App.appComponent.inject(this)

        when (sharedPreferences.getString(THEME_KEY, "0")) {
            Configuration.UI_MODE_NIGHT_UNDEFINED.toString() ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
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
                R.id.nav_run -> openScreen(RunFragment(), TAG_RUN)
                R.id.nav_progress -> openScreen(ProgressFragment(), TAG_PROGRESS)
                R.id.nav_settings -> openScreen(SettingsFragment(), TAG_SETTINGS)
            }
            true
        }
    }

    /**
     * Открытие фрагмента по кликам в BottomNavigationBar
     */
    private fun openScreen(fragmentToOpen: Fragment, tag: String) {
        if (currentFragment?.tag == tag) return
        currentFragment = fragmentToOpen

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, fragmentToOpen, tag)
            .commit()
    }

    /**
     * Открытие фрагмента бега по клику на нотификацию активного сервиса бега
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            openScreen(RunFragment(), TAG_RUN)
        }
    }
}