package org.team2059.scouting;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OpenFragment extends Fragment {

    private List<String> fileNames;
    private List<Integer> images;
    private RecyclerViewAdapter adapter;

    private ArrayList<String> titles;
    private ArrayList<String> dates;

    private Context context;

    private OpenFragmentListener listener;

    public interface OpenFragmentListener{
        void onInputOpenSend(String dirName);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_open, container, false);


        //Typeface eagleLight = Typeface.createFromAsset(getContext().getAssets(), "fonts/eagle_light.otf");
        Typeface eagleBook = Typeface.createFromAsset(context.getAssets(), "fonts/eagle_book.otf");
        //Typeface eagleBold = Typeface.createFromAsset(getContext().getAssets(), "fonts/eagle_bold.otf");


        RecyclerView recyclerView = view.findViewById(R.id.fragment_open_recylerView);

        fileNames = FileManager.getDirs(context);
        images = new ArrayList<>();

        titles = new ArrayList<>();
        dates = new ArrayList<>();

        parseFileAttrs();

        adapter = new RecyclerViewAdapter(titles, dates, images, getContext());

        // show recycler view in grid
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2,
                GridLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                String dirName = titles.get(position);

                listener.onInputOpenSend(dirName);
            }
        });



        ImageButton button = view.findViewById(R.id.fragment_open_sortABC);
        //button.setTypeface(eagleBook);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortAlphabetical();
            }
        });

        ImageButton button2 = view.findViewById(R.id.fragment_open_sortDate);
        //button2.setTypeface(eagleBook);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByDateModified();

            }
        });

        return view;
    }

    private void sortAlphabetical(){
        Collections.sort(fileNames, String.CASE_INSENSITIVE_ORDER);
        parseFileAttrs();
        adapter.notifyDataSetChanged();
    }
    private void sortByDateModified(){
        Collections.sort(fileNames, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String [] fileDesc = o1.split(",");
                String [] fileDesc2 = o2.split(",");

                DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
                //DateFormat format = DateFormat.getDateInstance();
                try {
                    Date date1 = format.parse(fileDesc[1].substring(1));
                    Date date2 = format.parse(fileDesc2[1].substring(1));

                    return -(date1.compareTo(date2));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        parseFileAttrs();
        adapter.notifyDataSetChanged();
    }

    private void parseFileAttrs(){
        images.clear();
        titles.clear();
        dates.clear();

        for(String fileName : fileNames){

            //same image for every grid card
            images.add(R.drawable.ic_folder_blue_outline);

            //parse fileDesc from FileManager with format filename + date modified
            String [] fileDesc = fileName.split(",");
            titles.add(fileDesc[0]);

            String date = fileDesc[1].substring(1, 4) + ", " + fileDesc[1].substring(5, 11) +
                    ", " + fileDesc[1].substring(fileDesc[1].length() - 4);

            dates.add(date);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof OpenFragmentListener){
            listener = (OpenFragmentListener) context;
            this.context = context;
        }else {
            throw new RuntimeException(context.toString() + "must implement OpenFragmentListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
