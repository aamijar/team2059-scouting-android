package com.team2059.scouting;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class TabFragment extends Fragment {



    private static final String ARG_TEAMS = "arg_teams";
    private static final String ARG_DIRNAME = "arg_dirName";
    private static final String ARG_HANDLERS = "arg_handlers";

    static TabFragment newInstance(String [] teams, String dirName, ArrayList<BluetoothHandler> bluetoothHandlers){
        TabFragment tabFragment = new TabFragment();
        Bundle args = new Bundle();
        args.putStringArray(ARG_TEAMS, teams);
        args.putString(ARG_DIRNAME, dirName);
        args.putParcelableArrayList(ARG_HANDLERS, bluetoothHandlers);
        tabFragment.setArguments(args);
        return tabFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tabview, container, false);

        ViewPager viewPager = view.findViewById(R.id.viewPager);

        MainFragment mainFragment;

        if(getArguments() != null){
            mainFragment = MainFragment.newInstance(getArguments().getStringArray(ARG_TEAMS), getArguments().getString(ARG_DIRNAME),
                    getArguments().<BluetoothHandler>getParcelableArrayList(ARG_HANDLERS));

        }
        else{
            mainFragment = new MainFragment();
        }

        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getActivity().getSupportFragmentManager(), mainFragment);

        viewPager.setAdapter(myPagerAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout2);
        tabLayout.setupWithViewPager(viewPager);

//        tabLayout.getTabAt(2).setIcon(R.drawable.ic_bluetooth_black);
//
//        tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.text_primary_dark), PorterDuff.Mode.SRC_IN);
//
//
//
//
//        LinearLayout layout = ((LinearLayout) ((LinearLayout) tabLayout.getChildAt(0)).getChildAt(2));
//        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
//        layoutParams.weight = 0f;
//        layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
//        layout.setLayoutParams(layoutParams);

        return view;
    }





}
