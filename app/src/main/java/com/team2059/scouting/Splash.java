package com.team2059.scouting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.team2059.scouting.BuildConfig;

public class Splash extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 4000;

    private ImageView image;
    private TextView text;
    private String versionCode = BuildConfig.VERSION_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        text= findViewById(R.id.textView);
        image = findViewById(R.id.imageView);

        text.setText("v" + versionCode);

        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.rotation_cw);
        image.startAnimation(myanim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                Intent homeIntent = new Intent(Splash.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
            }
        }, SPLASH_TIMEOUT);
    }
}
