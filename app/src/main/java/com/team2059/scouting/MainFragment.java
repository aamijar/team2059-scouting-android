package com.team2059.scouting;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.team2059.scouting.frc2020.IrAuto;
import org.team2059.scouting.frc2020.IrControlPanel;
import org.team2059.scouting.frc2020.IrEndgame;
import org.team2059.scouting.frc2020.IrMatch;
import org.team2059.scouting.frc2020.IrTeleop;

public class MainFragment extends Fragment {

    private Activity a_activity;
    private View a_view;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_main, container, false);
        a_view = v;
        setupUI(v.findViewById(R.id.parentView));
        final Spinner spinner = v.findViewById(R.id.spinner1);

        /*Initialize editText number widgets with value of 0*/
        low_att = v.findViewById(R.id.low_att);
        low_att.setText("0");
        low_made = v.findViewById(R.id.low_made);
        low_made.setText("0");
        out_att = v.findViewById(R.id.out_att);
        out_att.setText("0");
        out_made = v.findViewById(R.id.out_made);
        out_made.setText("0");
        inn_made = v.findViewById(R.id.inn_made);
        inn_made.setText("0");

        low_att2 = v.findViewById(R.id.low_att2);
        low_att2.setText("0");
        low_made2 = v.findViewById(R.id.low_made2);
        low_made2.setText("0");
        out_att2 = v.findViewById(R.id.out_att2);
        out_att2.setText("0");
        out_made2 = v.findViewById(R.id.out_made2);
        out_made2.setText("0");
        inn_made2 = v.findViewById(R.id.inn_made2);
        inn_made2.setText("0");

        /*Initialize increment up and down buttons*/
        ImageButton button1 = v.findViewById(R.id.increment_up);
        ImageButton button2 = v.findViewById(R.id.increment_down);
        ImageButton button3 = v.findViewById(R.id.increment_up2);
        ImageButton button4 = v.findViewById(R.id.increment_down2);
        ImageButton button5 = v.findViewById(R.id.increment_up3);
        ImageButton button6 = v.findViewById(R.id.increment_down3);
        ImageButton button7 = v.findViewById(R.id.increment_up4);
        ImageButton button8 = v.findViewById(R.id.increment_down4);
        ImageButton button9 = v.findViewById(R.id.increment_up5);
        ImageButton button10 = v.findViewById(R.id.increment_down5);

        ImageButton button11 = v.findViewById(R.id.increment_up6);
        ImageButton button12 = v.findViewById(R.id.increment_down6);
        ImageButton button13 = v.findViewById(R.id.increment_up7);
        ImageButton button14 = v.findViewById(R.id.increment_down7);
        ImageButton button15 = v.findViewById(R.id.increment_up8);
        ImageButton button16 = v.findViewById(R.id.increment_down8);
        ImageButton button17 = v.findViewById(R.id.increment_up9);
        ImageButton button18 = v.findViewById(R.id.increment_down9);
        ImageButton button19 = v.findViewById(R.id.increment_up10);
        ImageButton button20 = v.findViewById(R.id.increment_down10);


        /*Define Fonts from assets folder NOT from /res/font */
        Typeface eagleLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/eagle_light.otf");
        Typeface eagleBook = Typeface.createFromAsset(getActivity().getAssets(), "fonts/eagle_book.otf");
        Typeface eagleBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/eagle_bold.otf");


        final Switch switch1 = v.findViewById(R.id.switch1);
        final Switch switch2 = v.findViewById(R.id.switch2);
        final Switch switch3 = v.findViewById(R.id.switch3);
        final Switch switch4 = v.findViewById(R.id.switch4);
        final Switch switch5 = v.findViewById(R.id.switch5);
        final Switch switch6 = v.findViewById(R.id.switch6);
        final Switch switch7 = v.findViewById(R.id.switch7);


        switch1.setTypeface(eagleLight, Typeface.BOLD);
        switch2.setTypeface(eagleLight, Typeface.BOLD);
        switch3.setTypeface(eagleLight, Typeface.BOLD);
        switch4.setTypeface(eagleLight, Typeface.BOLD);
        switch5.setTypeface(eagleLight, Typeface.BOLD);
        switch6.setTypeface(eagleLight, Typeface.BOLD);
        switch7.setTypeface(eagleLight, Typeface.BOLD);

        Button button = v.findViewById(R.id.submit); //submit button
        //Button buttonActivity = (Button) findViewById(R.id.button); //to switch between pages
        button.setTypeface(eagleBook);


        final EditText matchNumber = v.findViewById(R.id.match_number);
        final EditText notes = v.findViewById(R.id.notes);


        final Context context = getActivity();




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
                    IrMatch irMatch = new IrMatch(spinner.getSelectedItem().toString(), Integer.parseInt(matchNumber.getText().toString()), 45,
                            100, 2, "true", auto, teleop, endgame, notes.getText().toString());
                    try
                    {
                        FileManager.writeToJsonFile("TEST_JSON.json", irMatch, context);
                    }
                    catch (Exception e)
                    {
                        Log.e("fileManager class error", "MainActivity started error");
                    }
                }
                else{
                    Toast.makeText(getActivity(), "Match Number Missing", Toast.LENGTH_LONG).show();
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




        return v;
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
                    hideSoftKeyboard(getActivity());
                    a_view.findViewById(R.id.parentView).requestFocus();
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


    /*called before onCreateView is called*/
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof Activity){
            a_activity = (Activity) context;
        }
    }
}
