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

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            openScreen(HomeFragment())
        }

        if (FirebaseAuth.getInstance().currentUser == null){
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
                R.id.nav_home -> {
                    openScreen(HomeFragment())
                }
                R.id.nav_map -> openScreen(MapFragment())
                R.id.nav_you -> openScreen(YouFragment())
                R.id.nav_settings -> openScreen(SettingsFragment())
            }
            true
        }

        binding.fabAction.setOnClickListener {
            openScreen(RunFragment())
        }
    }

    private fun openScreen(fragmentToOpen: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, fragmentToOpen, TAG)
            .commit()
    }

    private companion object {
        private const val TAG = "TAG_MAIN_FRAGMENT"
    }
}