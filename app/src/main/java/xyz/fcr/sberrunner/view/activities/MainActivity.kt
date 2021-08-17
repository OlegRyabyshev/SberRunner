package xyz.fcr.sberrunner.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import xyz.fcr.sberrunner.R
import android.content.Intent
import androidx.fragment.app.Fragment
import xyz.fcr.sberrunner.databinding.ActivityMainBinding
import xyz.fcr.sberrunner.view.fragments.main_fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            openScreen(HomeFragment(), TAG_HOME)
        }

        if (FirebaseAuth.getInstance().currentUser == null) {
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> openScreen(HomeFragment(), TAG_HOME)
                R.id.nav_map -> openScreen(MapFragment(), TAG_MAP)
                R.id.nav_you -> openScreen(YouFragment(), TAG_YOU)
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

    private companion object {
        private const val TAG_HOME = "TAG_HOME"
        private const val TAG_MAP = "TAG_MAP"
        private const val TAG_RUN = "TAG_RUN"
        private const val TAG_YOU = "TAG_YOU"
        private const val TAG_SETTINGS = "TAG_SETTINGS"
    }
}