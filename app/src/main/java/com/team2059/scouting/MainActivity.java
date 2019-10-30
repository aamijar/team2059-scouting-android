package com.team2059.scouting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create widget refrences
        final EditText comments = (EditText)findViewById(R.id.entry_comment);
        final TextView test1 = (TextView)findViewById(R.id.textView);

        Button button = (Button)findViewById(R.id.submit);

        //declare primitive types
        final ArrayList<String> data = new ArrayList<String>();
        final Context context = getApplicationContext();

        //submit button function
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v)
            {
                String user_entry = comments.getText().toString();
                test1.setText(user_entry);
                data.add(user_entry);
                //FileManager.writeFile(data);
                FileManager.writeToFile(data context);

            }
        });



        
    }
}
