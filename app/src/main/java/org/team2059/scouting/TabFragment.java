package org.team2059.scouting;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.view.animation.DecelerateInterpolator;
import android.widget.RadioButton;
import android.widget.RadioGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

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

    static TabFragment newInstance(String dirName){
        TabFragment tabFragment = new TabFragment();
        Bundle args = new Bundle();

        //Gson gson = new Gson();
        //String jsonTeamsArr = gson.toJson(teams);

        //args.putString(ARG_TEAMS, jsonTeamsArr);
        //args.putParcelableArray(ARG_TEAMS, teams);
        args.putString(ARG_DIRNAME, dirName);
        //args.putParcelableArrayList(ARG_HANDLERS, bluetoothHandlers);
        tabFragment.setArguments(args);
        return tabFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_tabview, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        Log.e("TAG", "TABFRAGMENT RECREATED");
        MainFragment mainFragment;
        AnalyzeFragment analyzeFragment;
        if(getArguments() != null){
//            mainFragment = MainFragment.newInstance((Team[]) getArguments().getParcelableArray(ARG_TEAMS), getArguments().getString(ARG_DIRNAME),
//                    getArguments().<BluetoothHandler>getParcelableArrayList(ARG_HANDLERS));
//            analyzeFragment = AnalyzeFragment.newInstance((Team[]) getArguments().getParcelableArray(ARG_TEAMS), getArguments().getString(ARG_DIRNAME));

            mainFragment = MainFragment.newInstance(getArguments().getString(ARG_DIRNAME));
            analyzeFragment = AnalyzeFragment.newInstance(getArguments().getString(ARG_DIRNAME));
        }
        else{
            mainFragment = new MainFragment();
            analyzeFragment = new AnalyzeFragment();
        }

        //*Important to use childfragmentmanager since we need to display a fragment within fragment
        // , i.e main & analyze in tab
        final MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getChildFragmentManager(), mainFragment, analyzeFragment);

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
        //final AnalyzeFragment fragment = (AnalyzeFragment) myPagerAdapter.getItem(1);
        //final AnalyzeFragment fragment = (AnalyzeFragment) viewPager.getAdapter().instantiateItem(viewPager, 1);

        final AnalyzeFragment fragment = (AnalyzeFragment) myPagerAdapter.getRegisteredFragment(1);
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

        final RadioGroup radioGroup = view.findViewById(R.id.filter_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = view.findViewById(checkedId);
                viewPager.getAdapter().instantiateItem(viewPager, 1);
                AnalyzeFragment fragment = (AnalyzeFragment) myPagerAdapter.getRegisteredFragment(1);
                if(radioButton.getText().toString().equals(getString(R.string.filter_OPR))){
                    fragment.sortByOPR();
                }
                else if(radioButton.getText().toString().equals(getString(R.string.filter_ranking_score))){
                    fragment.sortByRankingScore();
                }
                else if(radioButton.getText().toString().equals(getString(R.string.filter_auto_points))){
                    fragment.sortByAutoPoints();
                }
                else if(radioButton.getText().toString().equals(getString(R.string.filter_teleop_points))){
                    fragment.sortByTeleopPoints();
                }
                else if(radioButton.getText().toString().equals(getString(R.string.filter_endgame_points))){
                    fragment.sortByEndgamePoints();
                }
                else if(radioButton.getText().toString().equals(getString(R.string.cube_count))){
                    fragment.sortByCubeCount();
                }
                else if(radioButton.getText().toString().equals(getString(R.string.cone_count))){
                    fragment.sortByConeCount();
                }
                else if(radioButton.getText().toString().equals(getString(R.string.links_count))){
                    fragment.sortByLinksCount();
                }
                else if(radioButton.getText().toString().equals(getString(R.string.bot_count))){
                    fragment.sortByBotCount();
                }
                else if(radioButton.getText().toString().equals(getString(R.string.mid_count))){
                    fragment.sortByMidCount();
                }
                else if(radioButton.getText().toString().equals(getString(R.string.top_count))){
                    fragment.sortByTopCount();
                }
                else if(radioButton.getText().toString().equals(getString(R.string.bot_cube_count))){
                    fragment.sortByBotCubeCount();
                }
                else if(radioButton.getText().toString().equals(getString(R.string.bot_cone_count))){
                    fragment.sortByBotConeCount();
                }
                else if(radioButton.getText().toString().equals(getString(R.string.mid_cube_count))){
                    fragment.sortByMidCubeCount();
                }
                else if(radioButton.getText().toString().equals(getString(R.string.mid_cone_count))){
                    fragment.sortByMidConeCount();
                }
                else if(radioButton.getText().toString().equals(getString(R.string.top_cube_count))){
                    fragment.sortByTopCubeCount();
                }
                else if(radioButton.getText().toString().equals(getString(R.string.top_cone_count))){
                    fragment.sortByTopConeCount();
                }
            }
        });



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
