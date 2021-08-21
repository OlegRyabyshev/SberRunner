package xyz.fcr.sberrunner.presentation.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

import xyz.fcr.sberrunner.R;

public class SplashScreenActivity extends AppCompatActivity {

    LottieAnimationView lottie;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        lottie = findViewById(R.id.lottie_splash_screen);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }, 2000);
    }
}
