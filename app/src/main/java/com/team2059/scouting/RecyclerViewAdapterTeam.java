package com.team2059.scouting;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.team2059.scouting.core.Team;
import org.team2059.scouting.core.frc2020.IrTeam;

import java.util.ArrayList;

public class RecyclerViewAdapterTeam extends RecyclerView.Adapter<RecyclerViewAdapterTeam.ViewHolder> {

    private ArrayList<Team> teams;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView avatar;
        private TextView position;
        private TextView rank;
        private TextView record;
        private TextView teamName;
        private TextView teamNumber;
        private TextView attr1;
        private TextView attr2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.team_card_avatar);
            position = itemView.findViewById(R.id.team_card_position);
            rank = itemView.findViewById(R.id.team_card_rank);
            record = itemView.findViewById(R.id.team_card_record);
            teamName = itemView.findViewById(R.id.team_card_name);
            teamNumber = itemView.findViewById(R.id.team_card_number);
            attr1 = itemView.findViewById(R.id.team_card_attr1);
            attr2 = itemView.findViewById(R.id.team_card_attr2);


        }
    }

    public RecyclerViewAdapterTeam(ArrayList<Team> teams){
        this.teams = teams;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_team_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Team team = teams.get(position);
        if(team instanceof IrTeam){
            IrTeam irTeam = (IrTeam) team;

            byte [] bytes = Base64.decode(irTeam.getbyteMapString(), Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            holder.avatar.setImageBitmap(bmp);
            holder.position.setText(Integer.toString(position + 1));
            holder.rank.setText("Rank 1");
            holder.record.setText(Integer.toString(irTeam.getTotalPoints()));
            holder.teamName.setText(irTeam.getTeamName());
            holder.teamNumber.setText(irTeam.getTeamNumber());
            holder.attr1.setText(Integer.toString(irTeam.getAutoPowercellCount()));
            holder.attr2.setText(Integer.toString(irTeam.getTeleopPowercellCount()));
        }

    }

    @Override
    public int getItemCount() {
        return teams.size();
    }
}
