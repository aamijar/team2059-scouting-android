/**
 * Purpose: To effectively obtain and analyze FRC Scout Data
 * main class that is run when app is opened
 * user interface implementation
 *
 * @author Anupam
 *
 */

package org.team2059.scouting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;


import org.team2059.scouting.core.frc2020.IrAuto;
import org.team2059.scouting.core.frc2020.IrControlPanel;
import org.team2059.scouting.core.frc2020.IrEndgame;
import org.team2059.scouting.core.frc2020.IrTeleop;

@Deprecated
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

    private Switch switch1;
    private Switch switch2;
    private Switch switch3;
    private Switch switch4;
    private Switch switch5;
    private Switch switch6;
    private Switch switch7;

    private DrawerLayout drawer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUI(findViewById(R.id.parentView));

        /*hides notification bar*/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hides keyboard onCreate()
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle(R.string.logo_title);
        //getSupportActionBar().setLogo(R.mipmap.hh_launcher_round);
        //getSupportActionBar().setDisplayUseLogoEnabled(true);


//        drawer = findViewById(R.id.drawer_layout);
//        //NavigationView navigationView = findViewById(R.id.nav_view);
//        //navigationView.setNavigationItemSelectedListener(this);
//
//
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
//                R.string.nav_drawer_open, R.string.nav_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();



        /*Initialize custom spinner with NC FRC teams list*/
        String [] teams = getIntent().getStringArrayExtra("com.team2059.scouting.teams");

        String [] teamNames = new String[teams.length];

        for (int i = 0; i < teams.length; i ++){
            String [] pieces = teams[i].split(",");
            teamNames[i] = pieces[0] + ", " + pieces[1];
        }




        //ImageView imageView = findViewById(R.id.testimage);
        //ImageView imageView1 = new ImageView();

        String [] parts = teams[0].split(",");

        byte [] bytes = Base64.decode(parts[2], Base64.DEFAULT);

        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        //imageView.setImageBitmap(bmp);

        final Spinner spinner = findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, teamNames);

        spinner.setAdapter(adapter);



        /*Initialize editText number widgets with value of 0*/
        low_att = findViewById(R.id.bot_cube);
        low_att.setText("0");
        low_made = findViewById(R.id.bot_cone);
        low_made.setText("0");
        out_att = findViewById(R.id.mid_cube);
        out_att.setText("0");
        out_made = findViewById(R.id.mid_cone);
        out_made.setText("0");
        inn_made = findViewById(R.id.top_cube);
        inn_made.setText("0");

        low_att2 = findViewById(R.id.bot_cube2);
        low_att2.setText("0");
        low_made2 = findViewById(R.id.bot_cone2);
        low_made2.setText("0");
        out_att2 = findViewById(R.id.mid_cube2);
        out_att2.setText("0");
        out_made2 = findViewById(R.id.mid_cone2);
        out_made2.setText("0");
        inn_made2 = findViewById(R.id.top_cube2);
        inn_made2.setText("0");



        /*Initialize increment up and down buttons*/
        ImageButton button1 = findViewById(R.id.increment_up1);
        ImageButton button2 = findViewById(R.id.increment_down1);
        ImageButton button3 = findViewById(R.id.increment_up2);
        ImageButton button4 = findViewById(R.id.increment_down2);
        ImageButton button5 = findViewById(R.id.increment_up3);
        ImageButton button6 = findViewById(R.id.increment_down3);
        ImageButton button7 = findViewById(R.id.increment_up4);
        ImageButton button8 = findViewById(R.id.increment_down4);
        ImageButton button9 = findViewById(R.id.increment_up5);
        ImageButton button10 = findViewById(R.id.increment_down5);

        ImageButton button11 = findViewById(R.id.increment_up7);
        ImageButton button12 = findViewById(R.id.increment_down7);
        ImageButton button13 = findViewById(R.id.increment_up8);
        ImageButton button14 = findViewById(R.id.increment_down8);
        ImageButton button15 = findViewById(R.id.increment_up9);
        ImageButton button16 = findViewById(R.id.increment_down9);
        ImageButton button17 = findViewById(R.id.increment_up10);
        ImageButton button18 = findViewById(R.id.increment_down10);
        ImageButton button19 = findViewById(R.id.increment_up11);
        ImageButton button20 = findViewById(R.id.increment_down11);


        /*Define Fonts from assets folder NOT from /res/font */
        Typeface eagleLight = Typeface.createFromAsset(getAssets(), "fonts/eagle_light.otf");
        Typeface eagleBook = Typeface.createFromAsset(getAssets(), "fonts/eagle_book.otf");
        Typeface eagleBold = Typeface.createFromAsset(getAssets(), "fonts/eagle_bold.otf");


        /*set fonts for switch widgets
        * minSdk < 26 and android:fontFamily gives warning
        * app:fontFamily is not working for switches
        *
        * Alt Solution: setting font programmatically
        * via typeface
        * */
        final Switch switch1 = findViewById(R.id.switch1);
        final Switch switch2 = findViewById(R.id.switch2);
        final Switch switch3 = findViewById(R.id.switch3);
        final Switch switch4 = findViewById(R.id.switch4);
        final Switch switch5 = findViewById(R.id.switch5);
        final Switch switch6 = findViewById(R.id.switch6);
        final Switch switch7 = findViewById(R.id.switch7);


        switch1.setTypeface(eagleLight, Typeface.BOLD);
        switch2.setTypeface(eagleLight, Typeface.BOLD);
        switch3.setTypeface(eagleLight, Typeface.BOLD);
        switch4.setTypeface(eagleLight, Typeface.BOLD);
        switch5.setTypeface(eagleLight, Typeface.BOLD);
        switch6.setTypeface(eagleLight, Typeface.BOLD);
        switch7.setTypeface(eagleLight, Typeface.BOLD);

        Button button = (Button)findViewById(R.id.submit); //submit button
        //Button buttonActivity = (Button) findViewById(R.id.button); //to switch between pages
        button.setTypeface(eagleBook);


        final EditText matchNumber = findViewById(R.id.match_number);
        final EditText notes = findViewById(R.id.notes);


        final Context context = getApplicationContext();




        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v){
                /*create Match Object and append data from scout sheet*/

                IrAuto auto = new IrAuto(switch1.isChecked(), Integer.parseInt(low_att.getText().toString()),
                        Integer.parseInt(low_made.getText().toString()), Integer.parseInt(out_att.getText().toString()),
                        Integer.parseInt(out_made.getText().toString()), Integer.parseInt(inn_made.getText().toString()));
                IrControlPanel controlPanel = new IrControlPanel(switch2.isChecked(), switch3.isChecked());
                IrTeleop teleop = new IrTeleop(Integer.parseInt(low_att2.getText().toString()), Integer.parseInt(low_made2.getText().toString()), Integer.parseInt(out_att2.getText().toString()), Integer.parseInt(out_made2.getText().toString()), Integer.parseInt(inn_made2.getText().toString()), controlPanel);
                IrEndgame endgame = new IrEndgame(switch5.isChecked(), switch4.isChecked(), switch6.isChecked(), switch7.isChecked());

                if(!matchNumber.getText().toString().equals(""))
                {
                    //IrMatch irMatch = new IrMatch(spinner.getSelectedItem().toString(), Integer.parseInt(matchNumber.getText().toString()), 45,
                    //        100, 2, "true", auto, teleop, endgame, notes.getText().toString());
                    try
                    {
                        //FileManager.writeToJsonFile("TEST_JSON.json", irMatch, context);
                    }
                    catch (Exception e)
                    {
                        Log.e("fileManager class error", "MainActivity started error");
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Match Number Missing", Toast.LENGTH_LONG).show();
                }

            }
        });

        /*increment buttons*/

        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {incrementUp(low_att);}
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {incrementDown(low_att);}
        });
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {incrementUp(low_made);}
        });
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {incrementDown(low_made);}
        });
        button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {incrementUp(out_att);}
        });
        button6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {incrementDown(out_att);}
        });
        button7.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {incrementUp(out_made);}
        });
        button8.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {incrementDown(out_made);}
        });
        button9.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {incrementUp(inn_made);}
        });
        button10.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {incrementDown(inn_made);}
        });



        button11.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {incrementUp(low_att2);}
        });
        button12.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {incrementDown(low_att2);}
        });
        button13.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {incrementUp(low_made2);}
        });
        button14.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {incrementDown(low_made2);}
        });
        button15.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {incrementUp(out_att2);}
        });
        button16.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {incrementDown(out_att2);}
        });
        button17.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {incrementUp(out_made2);}
        });
        button18.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {incrementDown(out_made2);}
        });
        button19.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {incrementUp(inn_made2);}
        });
        button20.setOnClickListener(new View.OnClickListener() {
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


//    @Override
//    public void onBackPressed(){
//        if(drawer.isDrawerOpen(GravityCompat.START)){
//            drawer.closeDrawer(GravityCompat.START);
//        }
//        else{
//            super.onBackPressed();
//        }
//    }


    public void openMasterView() {
        Intent intent = new Intent(this, MasterView.class);
        startActivity(intent);
    }


    public void incrementUp(EditText editText) {
        int val;
        if(!editText.getText().toString().equals(""))
        {val = Integer.parseInt(editText.getText().toString());}
        else
        {val = 0;}
        val ++;
        String str = Integer.toString(val);
        editText.setText(str);
    }
    public void incrementDown(EditText editText) {
        int val;
        if(!editText.getText().toString().equals("") && Integer.parseInt(editText.getText().toString()) > 0)
        {val = Integer.parseInt(editText.getText().toString());}
        else
        {val = 1;}
        val --;
        String str = Integer.toString(val);
        editText.setText(str);
    }


    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        View focusedView = activity.getCurrentFocus();
        if(focusedView!=null){
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }

    public void setupUI(View view) {

        // base case
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(MainActivity.this);
                    findViewById(R.id.parentView).requestFocus();
                    return false;
                }
            });
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
}
