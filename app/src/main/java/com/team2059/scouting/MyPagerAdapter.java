package com.team2059.scouting;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class MyPagerAdapter extends FragmentStatePagerAdapter {

    private MainFragment mainFragment;
    private AnalyzeFragment analyzeFragment;

    public MyPagerAdapter(FragmentManager fm, MainFragment mainFragment, AnalyzeFragment analyzeFragment){
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mainFragment = mainFragment;
        this.analyzeFragment = analyzeFragment;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return mainFragment;
        }
        else{
            return analyzeFragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0){
            return "Scout Sheet";
        }else if(position == 1){
            return "Analyze";
        }
        else{
            return null;
        }

    }


}
