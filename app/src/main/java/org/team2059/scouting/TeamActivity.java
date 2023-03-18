package org.team2059.scouting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.transition.Fade;

import android.util.Base64;

import android.util.Log;
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


import org.team2059.scouting.core.Match;
import org.team2059.scouting.core.Team;
import org.team2059.scouting.core.frc2020.IrTeam;
import org.team2059.scouting.core.frc2023.CuTeam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class TeamActivity extends AppCompatActivity {

    private static final String TAG = "TeamActivity";

    private ArrayList<Boolean> states = new ArrayList<>();
    private ArrayList<Integer> heights = new ArrayList<>();

    private MatchListAdapter matchListAdapter;

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

        byte [] bytes;
        if(byteMapArr != null)
        {
            bytes = Base64.decode(byteMapArr, Base64.DEFAULT);

        }
        else{
            bytes = new byte[]{};
        }
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageView.setImageBitmap(bmp);

        /* set avatar backdrop according to preferences*/
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String avatarColor = sharedPreferences.getString("avatar_color", "Blue");
        if(avatarColor.equals("Blue")){
            imageView.setBackgroundResource(R.drawable.avatar_background);
        }
        else if(avatarColor.equals("Red")){
            imageView.setBackgroundResource(R.drawable.avatar_background_red);
        }


        String teamName = intent.getStringExtra("teamName");
        String teamNumber = intent.getStringExtra("teamNumber");
        name.setText(teamName);
        number.setText(teamNumber);

        Team team = (Team) intent.getSerializableExtra("teamObject");

        //casting working properly and serializable dependacies working for subclasses
        // IrTeam irTeam = (IrTeam) team;
        CuTeam irTeam = (CuTeam) team;


        String rankPointAvg = new BigDecimal(String.valueOf(team.getRankPointAvg())).setScale(2, BigDecimal.ROUND_HALF_UP).toString();

        rankingSummary.setText("Rank " + team.getOverallRank() + " | Ranking Score: " + rankPointAvg);

//        imageView.setTransitionName(intent.getStringExtra("teamAvatarTransitionName"));
//        name.setTransitionName(intent.getStringExtra("teamNameTransitionName"));
//        number.setTransitionName(intent.getStringExtra("teamNumberTransitionName"));





        final ListView listView = findViewById(R.id.teamprofile_listview);
        listView.addHeaderView(header, null, false);
        String [] vals = {"Match 1", "Match 2", "Match 3", "Match 4", "Match 5", "Match 6", "Match 7", "Match 8"};


        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, vals);

        matchListAdapter = new MatchListAdapter(this, 0, getSortedMatchList(irTeam.getMatches()), states, heights);

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
                //position = position + 1;
                //view.setClickable(false);
                Log.e("teamactivity", "onitemclick position:" + (position - 1));
                matchListAdapter.getStates().set(position - 1, !matchListAdapter.getStates().get(position - 1));
                //states.set(position - 1, !states.get(position - 1));
                //matchListAdapter.notifyDataSetChanged();
                int finalHeight = toggleRankingsExpanded(view);
                Log.e("teamactivity", "position " + (position - 1) + ": " + finalHeight);
                matchListAdapter.getHeights().set(position - 1, finalHeight);
                //heights.set(position - 1, finalHeight);
                //matchListAdapter.notifyDataSetChanged();

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


    public int toggleRankingsExpanded(final View view) {


        final View breakdownContainer = view.findViewById(R.id.match_breakdown_container);
        breakdownContainer.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        int originalHeight = view.getHeight();
        int expandedHeightDelta = breakdownContainer.getMeasuredHeight();
        int finalHeight;

        //heights.set(position, originalHeight);

        ValueAnimator valueAnimator;

        if (breakdownContainer.getVisibility() == View.INVISIBLE || breakdownContainer.getVisibility() == View.GONE) {
            Log.e("visible", "vis");
            breakdownContainer.setVisibility(View.VISIBLE);
            //breakdownContainer.setEnabled(true);

            valueAnimator = ValueAnimator.ofInt(originalHeight, originalHeight + expandedHeightDelta);
            finalHeight = originalHeight + expandedHeightDelta;
        } else {

            valueAnimator = ValueAnimator.ofInt(originalHeight, originalHeight - expandedHeightDelta);
            finalHeight = originalHeight - expandedHeightDelta;
            final Animation a = new AlphaAnimation(1.00f, 0.00f); // Fade out

            a.setDuration(200);
            // Set a listener to the animation and configure onAnimationEnd
            a.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    //view.findViewById(R.id.match_item_container).setEnabled(false);
                    Log.e("start collapse", "start collapse");
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Log.e("visible", "invis");
                    //breakdownContainer.setVisibility(View.INVISIBLE);
                    //breakdownContainer.setEnabled(false);
                    //view.findViewById(R.id.match_item_container).setEnabled(true);


                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    breakdownContainer.setVisibility(View.GONE);
                    //breakdownContainer.setEnabled(false);
                    Log.e("visible", "invis");
                    //matchListAdapter.notifyDataSetChanged();

                }
            }, a.getDuration());
            // Set the animation on the custom view
            //breakdownContainer.startAnimation(a);

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
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setEnabled(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
        return finalHeight;
    }

    private ArrayList<Match> getSortedMatchList(ArrayList<Match> matches){
        Collections.sort(matches, new Comparator<Match>() {
            @Override
            public int compare(Match m1, Match m2) {
                return m1.getMatchNumber() - m2.getMatchNumber();
            }
        });
        for(Match m: matches){
            states.add(false);
            heights.add(0);
        }

        return matches;
    }


}