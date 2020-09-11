package org.team2059.scouting;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import org.team2059.scouting.core.Match;
import org.team2059.scouting.core.frc2020.IrMatch;

import java.util.ArrayList;

public class MatchListAdapter extends ArrayAdapter<Match> {
    private Activity activity;
    private ArrayList<Match> matches;
    private static LayoutInflater inflater = null;


    public MatchListAdapter(Activity activity, int resource, ArrayList<Match> matches) {
        super(activity, resource, matches);
        this.activity = activity;
        this.matches = matches;
        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return matches.size();
    }

    public static class ViewHolder {
        public TextView matchNumber;
        public TextView matchResult;
        public TextView matchBreakdown;
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
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;
        ViewHolder holder;
        if(convertView == null){
            v = inflater.inflate(R.layout.list_item_match, null);
            holder = new ViewHolder();

            holder.matchNumber = v.findViewById(R.id.match_number);
            holder.matchResult = v.findViewById(R.id.match_result);
            holder.matchBreakdown = v.findViewById(R.id.match_breakdown_textview);

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


}
