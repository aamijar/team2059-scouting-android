package com.team2059.scouting;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.content.Context;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class MasterView extends AppCompatActivity {

    //declare widgets


    //primitive types

    //default setup
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_view);

        ImageButton imageButton = findViewById(R.id.refresh);
        final Context context = getApplicationContext();


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TableLayout tableLayout = findViewById(R.id.table_master);

                TableRow row = new TableRow(context);
                TextView text = new TextView(context);

                text.setText("team2059");
                //add column lines
                text.setBackgroundResource(R.drawable.textlines_col);

                row.addView(text);
                tableLayout.addView(row);
                //refreshData();
            }
        });
    }

    public void refreshData()
    {
        Context context = getApplicationContext();
        TableLayout tableLayout = findViewById(R.id.table_master);
        ArrayList<String> readData = new ArrayList<String>();
        readData = FileManager.readFromFile(context);

        TableRow row = new TableRow(context);
        TextView text = new TextView(context);

        for(String s: readData)
        {
            text.setText(s);
            row.addView(text);
            tableLayout.addView(row);
        }
    }

}
