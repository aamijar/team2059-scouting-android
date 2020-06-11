package com.team2059.scouting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {


    private List<String> titles;
    private List<String> dates;
    private List<Integer> images;
    private LayoutInflater inflater;

    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }


    public RecyclerViewAdapter(List<String> titles, List<String> dates, List<Integer> images, Context context){
        this.titles = titles;
        this.dates = dates;
        this.images = images;
        this.inflater = LayoutInflater.from(context);
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_grid, parent, false);

        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(titles.get(position));
        holder.dateModified.setText(dates.get(position));
        holder.image.setImageResource(images.get(position));

    }

    @Override
    public int getItemCount() {
        return titles.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private TextView dateModified;
        private ImageView image;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.grid_folder_name);
            dateModified = itemView.findViewById(R.id.grid_folder_date);
            image = itemView.findViewById(R.id.grid_folder_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(v.getContext(), titles.get(getAdapterPosition()), Toast.LENGTH_SHORT).show();
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }



    }

}
