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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.transition.Fade;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.team2059.scouting.core.Match;
import org.team2059.scouting.core.Team;
import org.team2059.scouting.core.frc2023.CuMatch;
import org.team2059.scouting.core.frc2023.CuTeam;
// import org.team2059.scouting.core.frc2020.IrMatch;
// import org.team2059.scouting.core.frc2020.IrTeam;

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

    private ArrayList<Team> teamsList = new ArrayList<>();
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
        final Gson gson = new Gson();
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
        String gsonStr = CuFileManager.readFile(dirName, activity);
        Type irMatchType = new TypeToken<ArrayList<CuMatch>>(){}.getType();
        ArrayList<CuMatch> irMatchArr = gson.fromJson(gsonStr, irMatchType);
        if(irMatchArr != null){
            prepareTeamsArr(irMatchArr);
            recyclerView = view.findViewById(R.id.analyze_recycleview);
            //recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(activity);
//            sortByRankingScore();
//            for(int i = 0; i < teamsList.size(); i ++){
//                teamsList.get(i).setOverallRank(i + 1);
//            }

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

            final SwipeRefreshLayout refreshLayout = view.findViewById(R.id.fragment_analyze_refresh);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    String gsonStr = CuFileManager.readFile(dirName, activity);
                    Type irMatchType = new TypeToken<ArrayList<CuMatch>>(){}.getType();
                    ArrayList<CuMatch> irMatchArr = gson.fromJson(gsonStr, irMatchType);

                    //case where undo button was used until irMatchArr is empty
                    if(irMatchArr != null && irMatchArr.size() == 0){
                        teamsList.clear();
                        adapter.notifyDataSetChanged();
                    }


                    else if(irMatchArr != null){
                        Log.e("gotten here", irMatchArr.size() + "");
                        prepareTeamsArr(irMatchArr);
                        String attrFilter = adapter.getAttrFilter();
                        if(attrFilter != null){
                            if(attrFilter.equals(getString(R.string.filter_OPR))){
                                sortByOPR();
                            }
                            else if(attrFilter.equals(getString(R.string.filter_auto_points))){
                                sortByAutoPoints();
                            }
                            else if(attrFilter.equals(getString(R.string.filter_teleop_points))){
                                sortByTeleopPoints();
                            }
                            else if(attrFilter.equals(getString(R.string.filter_endgame_points))){
                                sortByEndgamePoints();
                            }
                            else if(attrFilter.equals(getString(R.string.cube_count))){
                                sortByCubeCount();
                            }
                            else if(attrFilter.equals(getString(R.string.cone_count))){
                                sortByConeCount();
                            }
                            else if(attrFilter.equals(getString(R.string.links_count))){
                                sortByLinksCount();
                            }
                            else if(attrFilter.equals(getString(R.string.bot_count))){
                                sortByBotCount();
                            }
                            else if(attrFilter.equals(getString(R.string.mid_count))){
                                sortByMidCount();
                            }
                            else if(attrFilter.equals(getString(R.string.top_count))){
                                sortByTopCount();
                            }
                            else if(attrFilter.equals(getString(R.string.bot_cube_count))){
                                sortByBotCubeCount();
                            }
                            else if(attrFilter.equals(getString(R.string.bot_cone_count))){
                                sortByBotConeCount();
                            }
                            else if(attrFilter.equals(getString(R.string.mid_cube_count))){
                                sortByMidCubeCount();
                            }
                            else if(attrFilter.equals(getString(R.string.mid_cone_count))){
                                sortByMidConeCount();
                            }
                            else if(attrFilter.equals(getString(R.string.top_cube_count))){
                                sortByTopCubeCount();
                            }
                            else if(attrFilter.equals(getString(R.string.top_cone_count))){
                                sortByTopConeCount();
                            }
                        }
                    }
                    refreshLayout.setRefreshing(false);
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
        teamsList.clear();
        Log.e("matches", matches.size() + "");
        teamsList.addAll(CuFileManager.createTeamsArr(matches));

        for(Team team : teamsList){

            //set team avatars
            for (org.team2059.scouting.Team team1: teams){
                if(team.getTeamName().equals(team1.getTeamName()) && team.getTeamNumber().equals(team1.getTeamNumber())){
                    team.setByteMapString(team1.getByteMapArr());
                }
            }
        }
        sortByRankingScore();
        for(int i = 0; i < teamsList.size(); i ++){
            teamsList.get(i).setOverallRank(i + 1);
        }

        if(adapter != null){

            adapter.notifyDataSetChanged();
            recyclerView.scheduleLayoutAnimation();
            Log.e("gotten here", teamsList.size() + "");
        }

    }

    public void sortByOPR(){
        Collections.sort(teamsList, new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {

                double opr1 = ((double) o1.getTotalPoints())/o1.getCuMatches().size();
                double opr2 = ((double) o2.getTotalPoints())/o2.getCuMatches().size();


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
        if(adapter == null || teamsList.get(0).getOverallRank() == 0){
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

                CuTeam team1 = (CuTeam) o1;
                CuTeam team2 = (CuTeam) o2;

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

                CuTeam team1 = (CuTeam) o1;
                CuTeam team2 = (CuTeam) o2;

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

                CuTeam team1 = (CuTeam) o1;
                CuTeam team2 = (CuTeam) o2;

                return team2.getEndgamePoints() - team1.getEndgamePoints();
            }
        });
        adapter.setAttrFilter(getString(R.string.filter_endgame_points));
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    public void sortByCubeCount(){
        Collections.sort(teamsList, new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {

                CuTeam team1 = (CuTeam) o1;
                CuTeam team2 = (CuTeam) o2;

                return team2.getCubeCount() - team1.getCubeCount();
            }
        });
        adapter.setAttrFilter(getString(R.string.cube_count));
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    public void sortByConeCount(){
        Collections.sort(teamsList, new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {

                CuTeam team1 = (CuTeam) o1;
                CuTeam team2 = (CuTeam) o2;

                return team2.getConeCount() - team1.getConeCount();
            }
        });
        adapter.setAttrFilter(getString(R.string.cone_count));
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    public void sortByLinksCount(){
        Collections.sort(teamsList, new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {

                CuTeam team1 = (CuTeam) o1;
                CuTeam team2 = (CuTeam) o2;

                return team2.getLinksCount() - team1.getLinksCount();
            }
        });
        adapter.setAttrFilter(getString(R.string.links_count));
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    public void sortByBotCount(){
        Collections.sort(teamsList, new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {

                CuTeam team1 = (CuTeam) o1;
                CuTeam team2 = (CuTeam) o2;

                return team2.getBotCount() - team1.getBotCount();
            }
        });
        adapter.setAttrFilter(getString(R.string.bot_count));
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    public void sortByMidCount(){
        Collections.sort(teamsList, new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {

                CuTeam team1 = (CuTeam) o1;
                CuTeam team2 = (CuTeam) o2;

                return team2.getMidCount() - team1.getMidCount();
            }
        });
        adapter.setAttrFilter(getString(R.string.mid_count));
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    public void sortByTopCount(){
        Collections.sort(teamsList, new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {

                CuTeam team1 = (CuTeam) o1;
                CuTeam team2 = (CuTeam) o2;

                return team2.getTopCount() - team1.getTopCount();
            }
        });
        adapter.setAttrFilter(getString(R.string.top_count));
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    public void sortByBotCubeCount(){
        Collections.sort(teamsList, new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {

                CuTeam team1 = (CuTeam) o1;
                CuTeam team2 = (CuTeam) o2;

                return team2.getBotCubeCount() - team1.getBotCubeCount();
            }
        });
        adapter.setAttrFilter(getString(R.string.bot_cube_count));
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    public void sortByBotConeCount(){
        Collections.sort(teamsList, new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {

                CuTeam team1 = (CuTeam) o1;
                CuTeam team2 = (CuTeam) o2;

                return team2.getBotConeCount() - team1.getBotConeCount();
            }
        });
        adapter.setAttrFilter(getString(R.string.bot_cone_count));
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    public void sortByMidCubeCount(){
        Collections.sort(teamsList, new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {

                CuTeam team1 = (CuTeam) o1;
                CuTeam team2 = (CuTeam) o2;

                return team2.getMidCubeCount() - team1.getMidCubeCount();
            }
        });
        adapter.setAttrFilter(getString(R.string.mid_cube_count));
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    public void sortByMidConeCount(){
        Collections.sort(teamsList, new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {

                CuTeam team1 = (CuTeam) o1;
                CuTeam team2 = (CuTeam) o2;

                return team2.getMidConeCount() - team1.getMidConeCount();
            }
        });
        adapter.setAttrFilter(getString(R.string.mid_cone_count));
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    public void sortByTopCubeCount(){
        Collections.sort(teamsList, new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {

                CuTeam team1 = (CuTeam) o1;
                CuTeam team2 = (CuTeam) o2;

                return team2.getTopCubeCount() - team1.getTopCubeCount();
            }
        });
        adapter.setAttrFilter(getString(R.string.top_cube_count));
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    public void sortByTopConeCount(){
        Collections.sort(teamsList, new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {

                CuTeam team1 = (CuTeam) o1;
                CuTeam team2 = (CuTeam) o2;

                return team2.getTopConeCount() - team1.getTopConeCount();
            }
        });
        adapter.setAttrFilter(getString(R.string.top_cone_count));
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }


}
