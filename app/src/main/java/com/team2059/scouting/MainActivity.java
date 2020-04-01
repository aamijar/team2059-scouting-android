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
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/*widget imports*/
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {


    private EditText low_att;
    private EditText low_made;
    private EditText out_att;
    private EditText out_made;
    private EditText inn_made;

    private EditText low_att2;
    private EditText low_made2;
    private EditText out_att2;
    private EditText out_made2;
    private EditText inn_made2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.frc2020_teams, R.layout.spinner_item);
        spinner.setAdapter(adapter);

        low_att = findViewById(R.id.low_att);
        low_att.setText("0");
        low_made = findViewById(R.id.low_made);
        low_made.setText("0");
        out_att = findViewById(R.id.out_att);
        out_att.setText("0");
        out_made = findViewById(R.id.out_made);
        out_made.setText("0");
        inn_made = findViewById(R.id.inn_made);
        inn_made.setText("0");

        low_att2 = findViewById(R.id.low_att2);
        low_att2.setText("0");
        low_made2 = findViewById(R.id.low_made2);
        low_made2.setText("0");
        out_att2 = findViewById(R.id.out_att2);
        out_att2.setText("0");
        out_made2 = findViewById(R.id.out_made2);
        out_made2.setText("0");
        inn_made2 = findViewById(R.id.inn_made2);
        inn_made2.setText("0");



        ImageButton button1 = findViewById(R.id.increment_up);
        ImageButton button2 = findViewById(R.id.increment_down);
        ImageButton button3 = findViewById(R.id.increment_up2);
        ImageButton button4 = findViewById(R.id.increment_down2);
        ImageButton button5 = findViewById(R.id.increment_up3);
        ImageButton button6 = findViewById(R.id.increment_down3);
        ImageButton button7 = findViewById(R.id.increment_up4);
        ImageButton button8 = findViewById(R.id.increment_down4);
        ImageButton button9 = findViewById(R.id.increment_up5);
        ImageButton button10 = findViewById(R.id.increment_down5);


        ImageButton button11 = findViewById(R.id.increment_up6);
        ImageButton button12 = findViewById(R.id.increment_down6);
        ImageButton button13 = findViewById(R.id.increment_up7);
        ImageButton button14 = findViewById(R.id.increment_down7);
        ImageButton button15 = findViewById(R.id.increment_up8);
        ImageButton button16 = findViewById(R.id.increment_down8);
        ImageButton button17 = findViewById(R.id.increment_up9);
        ImageButton button18 = findViewById(R.id.increment_down9);
        ImageButton button19 = findViewById(R.id.increment_up10);
        ImageButton button20 = findViewById(R.id.increment_down10);





        Button button = (Button)findViewById(R.id.submit); //submit button

        //AssetManager am = getApplicationContext().getAssets();

//        Typeface typeface = Typeface.createFromAsset(am,
//                String.format(Locale.US, "font/%s", "eagle_book.otf"));
//        low_att.setTypeface(typeface);


        Typeface myFont = Typeface.createFromAsset(getAssets(), "fonts/eagle_light.otf");
        low_att.setTypeface(myFont);





        //Button buttonActivity = (Button) findViewById(R.id.button); //to switch between pages

        final Context context = getApplicationContext();

        //hides keyboard onCreate()
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


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

        /*increment buttons*/
        button1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {incrementUp(low_att);}
        });
        button2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {incrementDown(low_att);}
        });
        button3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {incrementUp(low_made);}
        });
        button4.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {incrementDown(low_made);}
        });
        button5.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {incrementUp(out_att);}
        });
        button6.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {incrementDown(out_att);}
        });
        button7.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {incrementUp(out_made);}
        });
        button8.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {incrementDown(out_made);}
        });
        button9.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {incrementUp(inn_made);}
        });
        button10.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {incrementDown(inn_made);}
        });



        button11.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {incrementUp(low_att2);}
        });
        button12.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {incrementDown(low_att2);}
        });
        button13.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {incrementUp(low_made2);}
        });
        button14.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {incrementDown(low_made2);}
        });
        button15.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {incrementUp(out_att2);}
        });
        button16.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {incrementDown(out_att2);}
        });
        button17.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {incrementUp(out_made2);}
        });
        button18.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {incrementDown(out_made2);}
        });
        button19.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {incrementUp(inn_made2);}
        });
        button20.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {incrementDown(inn_made2);}
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

    public void incrementUp(EditText editText)
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
    public void incrementDown(EditText editText)
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
