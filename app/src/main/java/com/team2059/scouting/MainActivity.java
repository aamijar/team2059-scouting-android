package com.team2059.scouting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText comments = (EditText)findViewById(R.id.entry_comment);
        final TextView test1 = (TextView)findViewById(R.id.textView);
        final

        Button button = (Button)findViewById(R.id.submit);

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v)
            {
                String user_entry = comments.getText().toString();
                test1.setText(user_entry);

            }
        });

        
    }
}
