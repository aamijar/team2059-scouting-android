package com.team2059.scouting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.Fade;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.team2059.scouting.core.Team;

public class TeamActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        Toolbar toolbar = findViewById(R.id.team_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(null);
        }


        supportPostponeEnterTransition();
        ImageView imageView = findViewById(R.id.teamprofile_avatar);
        TextView name = findViewById(R.id.teamprofile_name);
        TextView number = findViewById(R.id.teamprofile_number);
        TextView rankingSummary = findViewById(R.id.teamprofile_ranking_summary);

        Intent intent = getIntent();
        String byteMapArr = intent.getStringExtra("avatar");
        byte [] bytes = Base64.decode(byteMapArr, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageView.setImageBitmap(bmp);


        if(bytes.length > 0){
            //imageView.setBackgroundColor(getResources().getColor(R.color.frc_avatar_blue));
        }


        String teamName = intent.getStringExtra("teamName");
        String teamNumber = intent.getStringExtra("teamNumber");
        name.setText(teamName);
        number.setText(teamNumber);

        Team team = intent.getParcelableExtra("teamObject");
        Log.e("TAGGED", team.getTeamName());
        //Log.e("TAGGED", team.getRankPointAvg() + "");
        //rankingSummary.setText(team.getOverallRank() + " | " + team.getRankPointAvg());

        supportStartPostponedEnterTransition();


    }

}