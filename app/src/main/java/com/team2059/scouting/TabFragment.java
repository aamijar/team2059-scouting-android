package com.team2059.scouting;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.view.animation.DecelerateInterpolator;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class TabFragment extends Fragment {



    private static final String ARG_TEAMS = "arg_teams";
    private static final String ARG_DIRNAME = "arg_dirName";
    private static final String ARG_HANDLERS = "arg_handlers";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ConstraintLayout layoutMain;
    private ConstraintLayout filterContainer;
    private FloatingActionButton fab;
    private boolean isExpanded = false;

    private static final int ANIMATION_DURATION = 450;
    private static final float UNDIMMED_ALPHA = 0.0f;
    private static final float DIMMED_ALPHA = 0.7f;
    private View foregroundDim;

    static TabFragment newInstance(Team[] teams, String dirName, ArrayList<BluetoothHandler> bluetoothHandlers){
        TabFragment tabFragment = new TabFragment();
        Bundle args = new Bundle();

        //Gson gson = new Gson();
        //String jsonTeamsArr = gson.toJson(teams);

        //args.putString(ARG_TEAMS, jsonTeamsArr);
        //args.putParcelableArray(ARG_TEAMS, teams);
        args.putString(ARG_DIRNAME, dirName);
        args.putParcelableArrayList(ARG_HANDLERS, bluetoothHandlers);
        tabFragment.setArguments(args);
        return tabFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tabview, container, false);

        viewPager = view.findViewById(R.id.viewPager);

        MainFragment mainFragment;
        AnalyzeFragment analyzeFragment;
        if(getArguments() != null){
//            mainFragment = MainFragment.newInstance((Team[]) getArguments().getParcelableArray(ARG_TEAMS), getArguments().getString(ARG_DIRNAME),
//                    getArguments().<BluetoothHandler>getParcelableArrayList(ARG_HANDLERS));
//            analyzeFragment = AnalyzeFragment.newInstance((Team[]) getArguments().getParcelableArray(ARG_TEAMS), getArguments().getString(ARG_DIRNAME));

            mainFragment = MainFragment.newInstance(new Team[]{}, getArguments().getString(ARG_DIRNAME),
                    getArguments().<BluetoothHandler>getParcelableArrayList(ARG_HANDLERS));
            analyzeFragment = AnalyzeFragment.newInstance(new Team[]{}, getArguments().getString(ARG_DIRNAME));
        }
        else{
            mainFragment = new MainFragment();
            analyzeFragment = new AnalyzeFragment();
        }

        //*Important to use childfragmentmanager since we need to display a fragment within fragment
        // , i.e main & analyze in tab
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getChildFragmentManager(), mainFragment, analyzeFragment);

        viewPager.setAdapter(myPagerAdapter);

        tabLayout = view.findViewById(R.id.tabLayout2);
        tabLayout.setupWithViewPager(viewPager);

        //animate floating filter button
        fab = view.findViewById(R.id.analyze_floatingAction);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){ fab.hide(); }
                else{ fab.show(); }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        AnalyzeFragment fragment = (AnalyzeFragment) myPagerAdapter.getItem(1);

        layoutMain = view.findViewById(R.id.tab_layoutmain);
        filterContainer = view.findViewById(R.id.tab_filtercontainer);
        foregroundDim = view.findViewById(R.id.tab_foregroundDim);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFilterContainer();
            }
        });
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


    private void viewFilterContainer(){

        if(!isExpanded){

            int x = filterContainer.getRight();
            int y = filterContainer.getBottom();

            int startRadius = 0;
            int endRadius = (int) Math.hypot(layoutMain.getWidth(), layoutMain.getHeight());

            fab.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(),android.R.color.white,null)));
            fab.setImageResource(R.drawable.ic_close);

            ValueAnimator dimAnimation = ValueAnimator.ofFloat(UNDIMMED_ALPHA, DIMMED_ALPHA);
            dimAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    foregroundDim.setAlpha((float) animation.getAnimatedValue());
                }
            });
            dimAnimation.setDuration(ANIMATION_DURATION);

            Animator anim = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                anim = ViewAnimationUtils.createCircularReveal(filterContainer, x, y, startRadius, endRadius);
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        viewPager.setVisibility(View.INVISIBLE);
                        tabLayout.setVisibility(View.INVISIBLE);
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {}
                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });
                anim.setDuration(ANIMATION_DURATION);
                anim.setInterpolator(new DecelerateInterpolator());
            }
            filterContainer.setVisibility(View.VISIBLE);

            AnimatorSet animationSet = new AnimatorSet();
            animationSet.play(dimAnimation).with(anim);
            animationSet.start();
            isExpanded = true;
        }
        else{

            viewPager.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            int x = filterContainer.getRight();
            int y = filterContainer.getBottom();

            int startRadius = Math.max(filterContainer.getWidth(), filterContainer.getHeight());
            int endRadius = 0;

            fab.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(),R.color.colorAccent,null)));
            fab.setImageResource(R.drawable.ic_filter_white);

            ValueAnimator dimAnimation = ValueAnimator.ofFloat(DIMMED_ALPHA, UNDIMMED_ALPHA);
            dimAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    foregroundDim.setAlpha((float) animation.getAnimatedValue());
                }
            });
            dimAnimation.setDuration(ANIMATION_DURATION);

            Animator anim = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                anim = ViewAnimationUtils.createCircularReveal(filterContainer, x, y, startRadius, endRadius);
            }
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    filterContainer.setVisibility(View.GONE);
                }
                @Override
                public void onAnimationCancel(Animator animator) {}
                @Override
                public void onAnimationRepeat(Animator animator) {}
            });
            anim.setDuration(ANIMATION_DURATION);
            //anim.setInterpolator(new AccelerateInterpolator());

            AnimatorSet animationSet = new AnimatorSet();
            animationSet.play(dimAnimation).with(anim);
            animationSet.start();
            isExpanded = false;
        }

    }




}
