/**
 * Purpose: To effectively obtain and analyze FRC Scout Data
 * main class that is run when app is opened
 * user interface implementation
 *
 * @author Anupam
 */

package com.team2059.scouting;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/*widget imports*/

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;


import java.util.ArrayList;
import java.text.DateFormat;
import java.util.Date;



public class MainActivity extends AppCompatActivity {


    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.frc2020_teams, R.layout.spinner_item);
        spinner.setAdapter(adapter);

        editText = findViewById(R.id.low_att);
        editText.setText("0");


        Button button = (Button)findViewById(R.id.submit); //submit button
        ImageButton button1 = findViewById(R.id.increment_up);
        ImageButton button2 = findViewById(R.id.increment_down);

        //Button buttonActivity = (Button) findViewById(R.id.button); //to switch between pages

        //declare primitive types
        final Context context = getApplicationContext();

        //declare objects
        final DateFormat dateFormat = DateFormat.getDateTimeInstance();
        final Date date = new Date();


        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v)
            {

                /*create Match Object and append data from scout sheet*/
                Match match = new Match("The Hitchhikers, FRC 2059", 1, 45, 100, 2, true);
                try
                {   FileManager.writeToJsonFile("TEST_JSON.json", match, context);
                }
                catch (Exception e)
                {
                    Log.e("fileManager class error", "MainActivity started error");
                }
            }
        });

        button1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {incrementUp();}
        });
        button2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {incrementDown();}
        });


//        buttonActivity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openMasterView();
//            }
//        });




    }

    public void openMasterView()
    {
        Intent intent = new Intent(this, MasterView.class);
        startActivity(intent);
    }

    public void incrementUp()
    {
        int val;
        if(!editText.getText().toString().equals(""))
        {val = Integer.parseInt(editText.getText().toString());}
        else
        {val = 0;}
        val ++;
        String str = Integer.toString(val);
        editText.setText(str);
    }
    public void incrementDown()
    {
        int val;
        if(!editText.getText().toString().equals("") && Integer.parseInt(editText.getText().toString()) > 0)
        {val = Integer.parseInt(editText.getText().toString());}
        else
        {val = 1;}
        val --;
        String str = Integer.toString(val);
        editText.setText(str);
    }
}
