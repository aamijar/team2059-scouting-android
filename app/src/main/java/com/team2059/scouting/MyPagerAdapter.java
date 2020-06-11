package com.team2059.scouting;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class MyPagerAdapter extends FragmentStatePagerAdapter {

    private MainFragment mainFragment;


    public MyPagerAdapter(FragmentManager fm, MainFragment mainFragment){
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mainFragment = mainFragment;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return mainFragment;
        }
        else{
            return new BluetoothFragment();
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
        }else {
            return "Analyze";
        }
    }


}
