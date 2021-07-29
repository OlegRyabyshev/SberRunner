package xyz.fcr.sberrunner.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.ui.fragments.welcome_fragments.WelcomeMainFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main)

        if (FirebaseAuth.getInstance().currentUser == null){
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, WelcomeMainFragment())
                .commit()
        } else {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, MainScreenFragment())
                .commit()
        }
    }
}