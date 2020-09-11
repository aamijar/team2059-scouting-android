package org.team2059.scouting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.transition.Fade;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.team2059.scouting.core.Match;
import org.team2059.scouting.core.Team;
import org.team2059.scouting.core.frc2020.IrMatch;
import org.team2059.scouting.core.frc2020.IrTeam;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AnalyzeFragment extends Fragment {
    private Activity activity;

    private org.team2059.scouting.Team[] teams;
    private String dirName;

    private static final String ARG_TEAMS = "arg_teams";
    private static final String ARG_DIRNAME = "arg_dirName";

    private ArrayList<Team> teamsList;
    private RecyclerView recyclerView;
    private RecyclerViewAdapterTeam adapter;
    private RecyclerView.LayoutManager layoutManager;

    public static AnalyzeFragment newInstance(String dirName){
        AnalyzeFragment analyzeFragment = new AnalyzeFragment();
        Bundle args = new Bundle();

        //args.putParcelableArray(ARG_TEAMS, teams);
        //args.putString(ARG_TEAMS, jsonTeamsArr);
        args.putString(ARG_DIRNAME, dirName);

        analyzeFragment.setArguments(args);
        return analyzeFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analyze, container, false);
        Gson gson = new Gson();
        //setRetainInstance(true);
        postponeEnterTransition();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //activity.getWindow().setExitTransition(null);
            Fade fade = new Fade();
            fade.excludeTarget(R.id.navigation_toolbar, true);
            fade.excludeTarget(R.id.team_toolbar, true);
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);
            fade.excludeTarget(R.id.teamprofile_listview, true);
            activity.getWindow().setExitTransition(fade);
            activity.getWindow().setEnterTransition(fade);
        }


        if(getArguments() != null && (teamsList == null || dirName == null)){
            //teams = (com.team2059.scouting.Team[]) getArguments().getParcelableArray(ARG_TEAMS);
            //String jsonTeamsArr = getArguments().getString(ARG_TEAMS);
            //teams = gson.fromJson(jsonTeamsArr, Team[].class);
            dirName = getArguments().getString(ARG_DIRNAME);
        }

        SharedPreferences sharedPreferences = activity.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        String jsonTeamsArr = sharedPreferences.getString("com.team2059.scouting." + dirName, null);
        Team [] tmpteams = gson.fromJson(jsonTeamsArr, Team[].class);

        teams = new org.team2059.scouting.Team[tmpteams.length];
        for(int i = 0; i < tmpteams.length; i ++){
            teams[i] = new org.team2059.scouting.Team(tmpteams[i].getTeamName(), tmpteams[i].getTeamNumber(), tmpteams[i].getbyteMapString());
        }

        //In future add different Type objects to correlate with new Competitions
        String gsonStr = FileManager.readFile(dirName, activity);
        Type irMatchType = new TypeToken<ArrayList<IrMatch>>(){}.getType();
        ArrayList<IrMatch> irMatchArr = gson.fromJson(gsonStr, irMatchType);
        if(irMatchArr != null){
            prepareTeamsArr(irMatchArr);
            recyclerView = view.findViewById(R.id.analyze_recycleview);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(activity);
            sortByRankingScore();
            for(int i = 0; i < teamsList.size(); i ++){
                teamsList.get(i).setOverallRank(i + 1);
            }

            adapter = new RecyclerViewAdapterTeam(teamsList);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            adapter.setViewHolderListener(new RecyclerViewAdapterTeam.ViewHolderListener() {
                @Override
                public void onTeamClick(int position, ImageView avatar, TextView teamName, TextView teamNumber) {
                    //Toast.makeText(activity, "" + position, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activity, TeamActivity.class);

                    Pair[] pairs = new Pair[3];
                    pairs[0] = new Pair<View, String>(avatar, ViewCompat.getTransitionName(avatar));
                    pairs[1] = new Pair<View, String>(teamName, ViewCompat.getTransitionName(teamName));
                    pairs[2] = new Pair<View, String>(teamNumber, ViewCompat.getTransitionName(teamNumber));

                    //ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                    //        avatar, ViewCompat.getTransitionName(avatar));
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairs);

                    intent.putExtra("avatar", (String) teamsList.get(position).getbyteMapString());

                    intent.putExtra("teamName", teamName.getText().toString());
                    intent.putExtra("teamNumber", teamNumber.getText().toString());
                    intent.putExtra("teamObject", teamsList.get(position));

                    intent.putExtra("teamAvatarTransitionName", ViewCompat.getTransitionName(avatar));
                    intent.putExtra("teamNameTransitionName", ViewCompat.getTransitionName(teamName));
                    intent.putExtra("teamNumberTransitionName", ViewCompat.getTransitionName(teamNumber));

                    startActivity(intent, options.toBundle());
                }
            });

        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 2000ms
                Log.e("ELEMENT", "DONE");
                startPostponedEnterTransition();
            }
        }, 2000);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }
    public void prepareTeamsArr(ArrayList<? extends Match> matches){
        teamsList = FileManager.createTeamsArr(matches);
        //Log.e("TEAMLIST", teamsList.get(0).getTeamName());
        for(Team team : teamsList){
            Log.e("TEAM", "team name: " + team.getTeamName());
            Log.e("TEAM", "team number: " + team.getTeamNumber());
            Log.e("TEAM", "total points: " + team.getTotalPoints());

            for (IrMatch irMatch : team.getIrMatches()){
                Log.e("MATCH", "match number: " + irMatch.getMatchNumber());
            }
            //set team avatars
            for (org.team2059.scouting.Team team1: teams){
                if(team.getTeamName().equals(team1.getTeamName()) && team.getTeamNumber().equals(team1.getTeamNumber())){
                    team.setByteMapString(team1.getByteMapArr());
                }
            }
        }
    }

    public void sortByOPR(){
        Collections.sort(teamsList, new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {

                double opr1 = ((double) o1.getTotalPoints())/o1.getIrMatches().size();
                double opr2 = ((double) o2.getTotalPoints())/o2.getIrMatches().size();


                //add negative to sort in descending order
                return -Double.compare(opr1, opr2);
            }
        });
        adapter.setAttrFilter(getString(R.string.filter_OPR));
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    public void sortByRankingScore(){
        //initial sort
        Log.e("TEAMLIST2", teamsList.size() + "");
        if(adapter == null){
            Collections.sort(teamsList, new Comparator<Team>() {
                @Override
                public int compare(Team o1, Team o2) {
                    //add negative to sort in descending order
                    return -Double.compare(o1.getRankPointAvg(), o2.getRankPointAvg());
                }
            });
        }
        //use overall rank (set after initial sort) to ensure
        // relative-rank = overall rank in case of ties
        else{
            Collections.sort(teamsList, new Comparator<Team>() {
                @Override
                public int compare(Team o1, Team o2) {
                    return o1.getOverallRank() - o2.getOverallRank();
                }
            });
            adapter.setAttrFilter(getString(R.string.filter_ranking_score));
            adapter.notifyDataSetChanged();
            recyclerView.scheduleLayoutAnimation();
        }
    }
    public void sortByAutoPoints(){
        Collections.sort(teamsList, new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {

                IrTeam team1 = (IrTeam) o1;
                IrTeam team2 = (IrTeam) o2;

                return team2.getAutoPoints() - team1.getAutoPoints();
            }
        });
        adapter.setAttrFilter(getString(R.string.filter_auto_points));
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    public void sortByTeleopPoints(){
        Collections.sort(teamsList, new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {

                IrTeam team1 = (IrTeam) o1;
                IrTeam team2 = (IrTeam) o2;

                return team2.getTeleopPoints() - team1.getTeleopPoints();
            }
        });
        adapter.setAttrFilter(getString(R.string.filter_teleop_points));
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    public void sortByEndgamePoints(){
        Collections.sort(teamsList, new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {

                IrTeam team1 = (IrTeam) o1;
                IrTeam team2 = (IrTeam) o2;

                return team2.getEndgamePoints() - team1.getEndgamePoints();
            }
        });
        adapter.setAttrFilter(getString(R.string.filter_endgame_points));
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    public void sortByAutoPowerCellCount(){
        Collections.sort(teamsList, new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {

                IrTeam team1 = (IrTeam) o1;
                IrTeam team2 = (IrTeam) o2;

                return team2.getAutoPowercellCount() - team1.getAutoPowercellCount();
            }
        });
        adapter.setAttrFilter(getString(R.string.filter_auto_powercell_count));
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    public void sortByTeleopPowerCellCount(){
        Collections.sort(teamsList, new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {

                IrTeam team1 = (IrTeam) o1;
                IrTeam team2 = (IrTeam) o2;

                return team2.getTeleopPowercellCount() - team1.getTeleopPowercellCount();
            }
        });
        adapter.setAttrFilter(getString(R.string.filter_teleop_powercell_count));
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    public void sortByClimbCount(){
        Collections.sort(teamsList, new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {

                IrTeam team1 = (IrTeam) o1;
                IrTeam team2 = (IrTeam) o2;

                return team2.getClimbCount() - team1.getClimbCount();
            }
        });
        adapter.setAttrFilter(getString(R.string.filter_climb_count));
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }


}
