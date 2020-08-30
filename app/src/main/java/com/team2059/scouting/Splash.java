package com.team2059.scouting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GestureDetectorCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash extends AppCompatActivity {

    private ImageView image;
    private GestureDetectorCompat mDetector;

    private boolean splashDone = false;
    private final static int SPLASH_TIMEOUT = 4000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final String versionCode = "v" + BuildConfig.VERSION_NAME;
        TextView text;

        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        text= findViewById(R.id.textView);
        image = findViewById(R.id.imageView);

        text.setText(versionCode);

        Animation splashAnim = AnimationUtils.loadAnimation(this, R.anim.rotation_cw);
        image.startAnimation(splashAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                //Intent homeIntent = new Intent(Splash.this, MainActivity.class);
                if(!splashDone) {openMainActivity();}
                finish();
            }
        }, SPLASH_TIMEOUT);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


    public void openMainActivity()
    {
        Intent intent = new Intent(this, NavigationDrawer.class);
        startActivity(intent);
    }



    class MyGestureListener extends GestureDetector.SimpleOnGestureListener
    {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onDown(MotionEvent e)
        {
            Log.d(DEBUG_TAG, "onDown" + e.toString());
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(DEBUG_TAG, "onSingleTapUp" + e.toString());
            openMainActivity();
            image.clearAnimation();
            splashDone = true;
            return true;
        }
    }
}
