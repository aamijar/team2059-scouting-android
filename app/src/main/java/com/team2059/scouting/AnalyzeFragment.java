package com.team2059.scouting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.team2059.scouting.core.Match;
import org.team2059.scouting.core.Team;
import org.team2059.scouting.core.frc2020.IrMatch;
import org.team2059.scouting.core.frc2020.IrTeam;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AnalyzeFragment extends Fragment {
    private Activity activity;

    private com.team2059.scouting.Team[] teams;
    private String dirName;

    private static final String ARG_TEAMS = "arg_teams";
    private static final String ARG_DIRNAME = "arg_dirName";

    private ArrayList<Team> teamsList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public static AnalyzeFragment newInstance(com.team2059.scouting.Team[] teams, String dirName){
        AnalyzeFragment analyzeFragment = new AnalyzeFragment();
        Bundle args = new Bundle();

        args.putParcelableArray(ARG_TEAMS, teams);
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
        if(getArguments() != null && (teamsList == null || dirName == null)){
            teams = (com.team2059.scouting.Team[]) getArguments().getParcelableArray(ARG_TEAMS);
            //String jsonTeamsArr = getArguments().getString(ARG_TEAMS);
            //teams = gson.fromJson(jsonTeamsArr, Team[].class);
            dirName = getArguments().getString(ARG_DIRNAME);
        }



        String gsonStr = FileManager.readFile(dirName + "/my-data/Competition.json", activity);
        Type irMatchType = new TypeToken<ArrayList<IrMatch>>(){}.getType();
        ArrayList<IrMatch> irMatchArr = gson.fromJson(gsonStr, irMatchType);
        if(irMatchArr != null){
            prepareTeamsArr(irMatchArr);
            recyclerView = view.findViewById(R.id.analyze_recycleview);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(activity);
            adapter = new RecyclerViewAdapterTeam(teamsList);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }



        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }
    public void prepareTeamsArr(ArrayList<? extends Match> matches){
        teamsList = FileManager.createTeamsArr(matches);

        for(Team team : teamsList){
            Log.e("TEAM", "team name: " + team.getTeamName());
            Log.e("TEAM", "team number: " + team.getTeamNumber());
            Log.e("TEAM", "total points: " + team.getTotalPoints());

            for (IrMatch irMatch : team.getIrMatches()){
                Log.e("MATCH", "match number: " + irMatch.getMatchNumber());
            }
            //set team avatars
            for (com.team2059.scouting.Team team1: teams){
                if(team.getTeamName().equals(team1.getTeamName()) && team.getTeamNumber().equals(team1.getTeamNumber())){
                    team.setByteMapString(team1.getByteMapArr());
                }
            }
        }
    }

}
