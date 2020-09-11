package org.team2059.scouting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.BuildCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPreferences.getString("theme", "Set By Battery Saver");

        if(theme.equals("Dark")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else if(theme.equals("Light")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        else if(BuildCompat.isAtLeastQ()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
        }

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

        splashAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                if(!splashDone){
                    openMainActivity();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
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
            splashDone = true;
            openMainActivity();
            image.clearAnimation();
            return true;
        }
    }
}
