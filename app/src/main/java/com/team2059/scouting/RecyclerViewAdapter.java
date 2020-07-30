package com.team2059.scouting;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

        public ViewHolder(@NonNull final View itemView, final OnItemClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.grid_folder_name);
            dateModified = itemView.findViewById(R.id.grid_folder_date);
            image = itemView.findViewById(R.id.grid_folder_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(v.getContext(), titles.get(getAdapterPosition()), Toast.LENGTH_SHORT).show();
                    if(listener != null){
                        final int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){

                            final ImageView imageView = new ImageView(itemView.getContext());
                            imageView.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.ic_folder_blue));

                            float scale = itemView.getResources().getDisplayMetrics().density;
                            int dpAsPixels = (int) (85*scale + 0.5f);
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpAsPixels);
                            RelativeLayout relativeLayout = itemView.findViewById(R.id.grid_folder_relative);
                            relativeLayout.addView(imageView, params);
                            Animation fillup = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.fillup);
                            imageView.startAnimation(fillup);

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    listener.onItemClick(position);
                                }
                            }, 300);

                        }
                    }
                }
            });

        }



    }

}
