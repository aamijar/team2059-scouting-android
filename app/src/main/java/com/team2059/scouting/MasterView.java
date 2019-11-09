package com.team2059.scouting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.widget.ImageButton;
import android.widget.TableLayout;

public class MasterView extends AppCompatActivity {

    //widgets
    private ImageButton imageButton = (ImageButton) findViewById(R.id.refresh);
    private TableLayout tableLayout = (TableLayout) findViewById(R.id.table_master);

    //default setup
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_view);




    }

    public void refreshData()
    {


    }

}
