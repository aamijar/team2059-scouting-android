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

//widget imports
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.util.ArrayList;
import java.text.DateFormat;
import java.util.Date;

import android.media.MediaScannerConnection;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //default setup for app
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create widget references
        final EditText comments = (EditText)findViewById(R.id.entry_comment); //multiline comments
        final EditText temp = (EditText)findViewById(R.id.editText);
        final TextView test1 = (TextView)findViewById(R.id.textView);
        Button button = (Button)findViewById(R.id.submit); //submit button
        Button buttonActivity = (Button) findViewById(R.id.button); //to switch between pages
        //declare primitive types
        final ArrayList<String> data = new ArrayList<String>();

        final Context context = getApplicationContext();


        //declare objects
        final DateFormat dateFormat = DateFormat.getDateTimeInstance();
        final Date date = new Date();


        //submit button function
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v)
            {
                String user_entry = comments.getText().toString();
                String user_entry2 = temp.getText().toString();

                //String fileName = temp.getText().toString();
                //name the file according to current date
                String fileName = dateFormat.format(date);

                //test1.setText(user_entry);
                data.add(user_entry);
                data.add(user_entry2);
                //FileManager.writeToFile(fileName, data, context);

                ArrayList<String> readData = new ArrayList<String>();
                //readData = FileManager.readFromFile(context);
                //test1.setText(readData.get(0) + " " + readData.get(1));


                /*
                create Match Object and append data from scout sheet
                 */
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

        buttonActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMasterView();
            }
        });
        
    }

    public void openMasterView()
    {
        Intent intent = new Intent(this, MasterView.class);
        startActivity(intent);
    }
}
