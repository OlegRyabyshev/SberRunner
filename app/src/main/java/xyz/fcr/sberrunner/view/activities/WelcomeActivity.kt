package xyz.fcr.sberrunner.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.databinding.ActivityWelcomeBinding
import xyz.fcr.sberrunner.view.fragments.welcome_fragments.WelcomeFragment

class WelcomeActivity : AppCompatActivity() {

    private var _binding: ActivityWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        _binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            openWelcomeScreen()
        }
    }

    private fun openWelcomeScreen() {
        val manager = supportFragmentManager
        manager
            .beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.welcome_container, WelcomeFragment())
            .commit()
    }
}