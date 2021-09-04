package xyz.fcr.sberrunner.presentation.view.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import xyz.fcr.sberrunner.presentation.App
import javax.inject.Inject

/**
 * Активити SplashScreen.
 * Служит для вывода изображение и осуществяет переход на окна логина и регистрации, если пользователя не существует.
 */
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.appComponent.inject(this)

        when {
            firebaseAuth.currentUser != null -> startActivity(MainActivity::class.java)
            else -> startActivity(WelcomeActivity::class.java)
        }
    }

    /**
     * Переход на новое активити
     */
    private fun startActivity(activity: Class<out Activity>) {
        val intent = Intent(this, activity)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
        startActivity(intent)
        finish()
    }
}
