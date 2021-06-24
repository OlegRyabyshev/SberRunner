package xyz.fcr.sberrunner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import xyz.fcr.sberrunner.fragments.welcome_fragments.WelcomeMainFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null){
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, WelcomeMainFragment())
                .commit()
        }
    }
}