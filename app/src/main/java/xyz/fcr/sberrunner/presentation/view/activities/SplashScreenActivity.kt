package xyz.fcr.sberrunner.presentation.view.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import android.os.Bundle
import xyz.fcr.sberrunner.R
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import xyz.fcr.sberrunner.presentation.App
import javax.inject.Inject
import android.app.Activity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var lottie: LottieAnimationView

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash_screen)
        App.appComponent.inject(this)

        lottie = findViewById(R.id.lottie_splash_screen)
        Handler(Looper.getMainLooper()).postDelayed({
            when {
                firebaseAuth.currentUser != null -> initActivity(MainActivity::class.java)
                else -> initActivity(WelcomeActivity::class.java)
            }
        }, 2000)
    }

    private fun initActivity(activity: Class<out Activity>){
        val intent = Intent(this, activity)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
        startActivity(intent)
        finish()
    }
}