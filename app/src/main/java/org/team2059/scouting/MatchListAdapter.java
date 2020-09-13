package org.team2059.scouting;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.google.android.material.tabs.TabLayout;

import org.team2059.scouting.core.Match;
import org.team2059.scouting.core.frc2020.IrMatch;

import java.util.ArrayList;

public class MatchListAdapter extends ArrayAdapter<Match> {
    private Activity activity;
    private ArrayList<Match> matches;
    private static LayoutInflater inflater = null;
    private ArrayList<Boolean> states;
    private ArrayList<Integer> heights;
    private int minHeight;


    public MatchListAdapter(Activity activity, int resource, ArrayList<Match> matches, ArrayList<Boolean> states, ArrayList<Integer> heights) {
        super(activity, resource, matches);
        this.activity = activity;
        this.matches = matches;
        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.states = states;
        this.heights = heights;
    }

    @Override
    public int getCount() {
        return matches.size();
    }

    public static class ViewHolder {
        public TextView matchNumber;
        public TextView matchResult;
        public TextView matchBreakdown;
        public LinearLayout matchBreakdownContainer;
    }

    @Nullable
    @Override
    public Match getItem(int position) {
        return matches.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;
        ViewHolder holder;
        if(convertView == null){
            v = inflater.inflate(R.layout.list_item_match, null);
            holder = new ViewHolder();

            holder.matchNumber = v.findViewById(R.id.match_number);
            holder.matchResult = v.findViewById(R.id.match_result);
            holder.matchBreakdown = v.findViewById(R.id.match_breakdown_textview);
            holder.matchBreakdownContainer = v.findViewById(R.id.match_breakdown_container);

            v.setTag(holder);
        }else{
            holder = (ViewHolder) v.getTag();
        }
        holder.matchNumber.setText("Match: " + matches.get(position).getMatchNumber());

        if(matches.get(position).getMatchResult().equals("win")){
            holder.matchResult.setText("W");
        }else if(matches.get(position).getMatchResult().equals("tie")){
            holder.matchResult.setText("T");
        }else{
            holder.matchResult.setText("L");
        }

        if(states.get(position)){
            holder.matchBreakdownContainer.setVisibility(View.VISIBLE);
            //holder.matchBreakdownContainer.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimaryDark));
            Log.e("matchlistadapter", "current height of position " + position + ": "  + heights.get(position));
            Log.e("VISIBLITY", String.valueOf(holder.matchBreakdownContainer.getVisibility()));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heights.get(position)));
            if(v.getAnimation() == null){
                //Log.e("matchlistadapter", "animation ended");
            }
            else{
                //Log.e("matchlistadapter", "animation in progress");
            }
            v.setLayoutParams(lp);
        }
        else{
            //v.clearAnimation();
            //holder.matchBreakdownContainer.clearAnimation();
            //v.findViewById(R.id.match_breakdown_container).setVisibility(View.INVISIBLE);
            holder.matchBreakdownContainer.setVisibility(View.GONE);
            //holder.matchBreakdownContainer.setBackgroundColor(activity.getResources().getColor(R.color.text_primary_light));
            //v.requestLayout();
            Log.e("matchlistadapter", "current height collapsed of position " + position + ": " + heights.get(position));
            //if(holder.matchBreakdownContainer.getVisibility())

            Log.e("VISIBLITY", String.valueOf(holder.matchBreakdownContainer.getVisibility()));
            if(v.getAnimation() == null){
                //Log.e("matchlistadapter", "animation ended");
            }
            else{
                //Log.e("matchlistadapter", "animation in progress");
            }
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heights.get(position)));
//            v.setLayoutParams(lp);

            if(heights.get(position) != 0){
                //Log.e("matchlistadapter", v.getHeight() + " position: " + position);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heights.get(position)));
                v.setLayoutParams(lp);
            }



            else{
//                int min = 1000;
//                for(int i = 0; i < heights.size(); i ++){
//                    Log.e("heights", "heights 0 = : " + heights.get(0));
//                    if(min > heights.get(i) && heights.get(i) != 0){
//
//                        min = heights.get(i);
//                    }
//                }
//                Log.e("min", "" + min);
//                if(position != 0 && min!= 1000){
//                    Log.e("min", "" + min);
//                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, min));
//                    v.setLayoutParams(lp);
//                }
                if(position != 0){
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, minHeight));
                    v.setLayoutParams(lp);
                }

                else{
                    final View finalV = v;
                    finalV.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            finalV.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            heights.set(position, finalV.getHeight());
                            if(position == 0){
                                minHeight = finalV.getHeight();
                            }
                            //finalV.getLayoutParams().height = finalV.getHeight();
                        }
                    });
                }
            }

        }
        //holder.matchBreakdownContainer.setVisibility(View.INVISIBLE);

//        v.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                states.set(position, !states.get(position));
//                int finalHeight = ((TeamActivity) activity).toggleRankingsExpanded(v);
//                Log.e("matchlistadapter", "finalheight: " + finalHeight);
//                heights.set(position, finalHeight);
//            }
//        });

        //in future check for different competition types
        if(matches.get(position) instanceof IrMatch){
            //down cast
            IrMatch irMatch = (IrMatch) matches.get(position);


            String matchBreakDownData = activity.getString(R.string.match_info, getSymbol(irMatch.getAuto().getInitLine()),
                    irMatch.getAuto().getLowerAttempt(),irMatch.getAuto().getLowerPort(), irMatch.getAuto().getUpperAttempt(), irMatch.getAuto().getOuterPort(), irMatch.getAuto().getInnerPort(),
                    irMatch.getTeleop().getLowerAttempt(),irMatch.getTeleop().getLowerPort(), irMatch.getTeleop().getUpperAttempt(), irMatch.getTeleop().getOuterPort(), irMatch.getTeleop().getInnerPort(),
                    getSymbol(irMatch.getTeleop().getControlPanel().getPosition()), getSymbol(irMatch.getTeleop().getControlPanel().getRotation()), getSymbol(irMatch.getEndgame().getClimbAttempt()),
                    getSymbol(irMatch.getEndgame().getClimb()), getSymbol(irMatch.getEndgame().getPark()), getSymbol(irMatch.getEndgame().getLevel()), irMatch.getPostGame().getNotes());
            holder.matchBreakdown.setText(matchBreakDownData);

            final String RP_INDICATOR = "\u25c6   ";
            final String RP_HARD_INDICATOR = "\u2756   ";


            if(irMatch.getPostGame().isStageThreeActivated()){
                holder.matchResult.setText(RP_HARD_INDICATOR + holder.matchResult.getText());
            }
            if(irMatch.getPostGame().isClimbRankPoint()){
                holder.matchResult.setText(RP_INDICATOR + holder.matchResult.getText());
            }


        }


        return v;
    }

    private String getSymbol(boolean result){
        String unicodeX = "\u2718";
        String unicodeCheck = "\u2714";
        if(result){
            return unicodeCheck;
        }else{
            return unicodeX;
        }
    }

    public ArrayList<Boolean> getStates(){
        return states;
    }
    public ArrayList<Integer> getHeights(){
        return heights;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

}
