package com.team2059.scouting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import android.transition.Fade;

import android.util.Base64;

import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.team2059.scouting.core.Team;
import org.team2059.scouting.core.frc2020.IrTeam;

import java.math.BigDecimal;


public class TeamActivity extends AppCompatActivity {

    private static final String TAG = "TeamActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        //set up back arrow in toolbar
        Toolbar toolbar = findViewById(R.id.team_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //getWindow().setEnterTransition(null);
            Fade fade = new Fade();
            fade.excludeTarget(R.id.navigation_toolbar, true);
            fade.excludeTarget(R.id.team_toolbar, true);
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);
            fade.excludeTarget(R.id.teamprofile_listview, true);
            getWindow().setExitTransition(fade);
            getWindow().setEnterTransition(fade);
        }

        /*postpone transition and being to load data*/
        supportPostponeEnterTransition();

        View header = getLayoutInflater().inflate(R.layout.teamprofile_header,null);

        ImageView imageView = header.findViewById(R.id.teamprofile_avatar);
        TextView name = header.findViewById(R.id.teamprofile_name);
        TextView number = header.findViewById(R.id.teamprofile_number);
        TextView rankingSummary = header.findViewById(R.id.teamprofile_ranking_summary);

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

        Team team = (Team) intent.getSerializableExtra("teamObject");

        //casting working properly and serializable dependacies working for subclasses
        IrTeam irTeam = (IrTeam) team;


        String rankPointAvg = new BigDecimal(String.valueOf(team.getRankPointAvg())).setScale(2, BigDecimal.ROUND_HALF_UP).toString();

        rankingSummary.setText("Rank " + team.getOverallRank() + " | Ranking Score: " + rankPointAvg);

//        imageView.setTransitionName(intent.getStringExtra("teamAvatarTransitionName"));
//        name.setTransitionName(intent.getStringExtra("teamNameTransitionName"));
//        number.setTransitionName(intent.getStringExtra("teamNumberTransitionName"));





        final ListView listView = findViewById(R.id.teamprofile_listview);
        listView.addHeaderView(header, null, false);
        String [] vals = {"Match 1", "Match 2", "Match 3", "Match 4", "Match 5", "Match 6", "Match 7", "Match 8"};


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, vals);
        MatchListAdapter matchListAdapter = new MatchListAdapter(this, 0, irTeam.getMatches());

        listView.setAdapter(matchListAdapter);


        /*when listView is completely loaded resume transition*/
        listView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                listView.removeOnLayoutChangeListener(this);
                supportStartPostponedEnterTransition();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toggleRankingsExpanded(view);
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void toggleRankingsExpanded(final View view) {


        final View breakdownContainer = view.findViewById(R.id.match_breakdown_container);
        breakdownContainer.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        int originalHeight = view.getHeight();
        int expandedHeightDelta = breakdownContainer.getMeasuredHeight();

        ValueAnimator valueAnimator;

        if (breakdownContainer.getVisibility() == View.INVISIBLE || breakdownContainer.getVisibility() == View.GONE) {
            breakdownContainer.setVisibility(View.VISIBLE);
            breakdownContainer.setEnabled(true);

            valueAnimator = ValueAnimator.ofInt(originalHeight, originalHeight + expandedHeightDelta);
        } else {

            valueAnimator = ValueAnimator.ofInt(originalHeight, originalHeight - expandedHeightDelta);

            Animation a = new AlphaAnimation(1.00f, 0.00f); // Fade out

            a.setDuration(200);
            // Set a listener to the animation and configure onAnimationEnd
            a.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    breakdownContainer.setVisibility(View.INVISIBLE);
                    breakdownContainer.setEnabled(false);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            // Set the animation on the custom view
            breakdownContainer.startAnimation(a);
        }
        valueAnimator.setDuration(400);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                view.requestLayout();
            }
        });
        valueAnimator.start();
    }




}