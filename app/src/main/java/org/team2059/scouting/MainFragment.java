package org.team2059.scouting;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

//import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.json.simple.parser.ParseException;
import org.team2059.scouting.core.frc2020.IrAuto;
import org.team2059.scouting.core.frc2020.IrControlPanel;
import org.team2059.scouting.core.frc2020.IrEndgame;
import org.team2059.scouting.core.frc2020.IrMatch;
import org.team2059.scouting.core.frc2020.IrPostGame;
import org.team2059.scouting.core.frc2020.IrTeleop;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainFragment extends Fragment implements BluetoothHandler.BluetoothHandlerCallback {

    private Activity activity;
    private View view;

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

    private Team[] teams;
    private String dirName;

    private static final String ARG_TEAMS = "arg_teams";
    private static final String ARG_DIRNAME = "arg_dirName";
    private static  final String ARG_HANDLERS = "arg_handlers";

    private static final String TAG = "MainFragment";

    private Spinner spinner;

    private SwitchCompat switch1;
    private SwitchCompat switch2;
    private SwitchCompat switch3;
    private SwitchCompat switch4;
    private SwitchCompat switch5;
    private SwitchCompat switch6;
    private SwitchCompat switch7;
    private SwitchCompat switch8;
    private SwitchCompat switch9;

    private EditText matchNumber;
    private EditText notes;
    private RadioGroup radioGroup;


    static MainFragment newInstance(String dirName) {
        MainFragment mainFragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DIRNAME, dirName);

        mainFragment.setArguments(args);
        return mainFragment;
    }

    @Override
    public void onBluetoothHandlerCallback(BluetoothHandler bluetoothHandler) {

    }

    @Override
    public void onConnectionCallback() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_main, container, false);
        view = v;

        setupUI(v.findViewById(R.id.parentView));

        spinner = v.findViewById(R.id.spinner1);

        if(getArguments() != null){
            //String jsonTeamsArr = getArguments().getString(ARG_TEAMS);
            //teams = (Team[]) getArguments().getParcelableArray(ARG_TEAMS);
            dirName = getArguments().getString(ARG_DIRNAME);
            Gson gson = new Gson();
            SharedPreferences sharedPreferences = activity.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
            String jsonTeamsArr = sharedPreferences.getString("com.team2059.scouting." + dirName, null);
            org.team2059.scouting.core.Team[] tmpteams = gson.fromJson(jsonTeamsArr, org.team2059.scouting.core.Team[].class);

            // * Note in future check if tmpteams is null
            teams = new Team[tmpteams.length];

            for(int i = 0; i < tmpteams.length; i ++){
                teams[i] = new Team(tmpteams[i].getTeamName(), tmpteams[i].getTeamNumber(), tmpteams[i].getbyteMapString());
            }



            if(teams != null && teams.length != 0){
                updateTeams();
            }
            else{
                ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, R.layout.spinner_item, new String[]{"No Teams Found, 0000"});
                spinner.setAdapter(adapter);
            }
            //bluetoothHandlers = (ArrayList<BluetoothHandler>) getArguments().getSerializable(ARG_HANDLERS);
            //bluetoothHandlers = getArguments().getParcelableArrayList(ARG_HANDLERS);
        }



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
        //Typeface eagleLight = Typeface.createFromAsset(activity.getAssets(), "fonts/eagle_light.otf");
        //Typeface eagleBook = Typeface.createFromAsset(activity.getAssets(), "fonts/eagle_book.otf");
        //Typeface eagleBold = Typeface.createFromAsset(activity.getAssets(), "fonts/eagle_bold.otf");


        switch1 = v.findViewById(R.id.switch1);
        switch2 = v.findViewById(R.id.switch2);
        switch3 = v.findViewById(R.id.switch3);
        switch4 = v.findViewById(R.id.switch4);
        switch5 = v.findViewById(R.id.switch5);
        switch6 = v.findViewById(R.id.switch6);
        switch7 = v.findViewById(R.id.switch7);
        switch8 = v.findViewById(R.id.switch8);
        switch9 = v.findViewById(R.id.switch9);


        Button button = v.findViewById(R.id.submit); //submit button


        matchNumber = v.findViewById(R.id.match_number);
        notes = v.findViewById(R.id.notes);
        radioGroup = v.findViewById(R.id.main_radiogroup);

        ImageButton undoButton = v.findViewById(R.id.undo);
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(activity)
                        .setTitle("Undo entry")
                        .setMessage("Are you sure you want to undo your last recorded match?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    FileManager.undoLastMatchSheet(dirName + "/my-data/Competition.json", activity);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIconAttribute(android.R.attr.alertDialogIcon)
                        .show();
            }
        });



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
                    RadioButton radioButton = view.findViewById(radioGroup.getCheckedRadioButtonId());
                    if(radioButton != null){
                        IrPostGame postGame = new IrPostGame(radioButton.getText().toString().toLowerCase(), switch9.isChecked(), switch8.isChecked(), notes.getText().toString());

                        IrMatch irMatch = new IrMatch(spinner.getSelectedItem().toString(), Integer.parseInt(matchNumber.getText().toString()), auto, teleop, endgame, postGame);
                        try
                        {
                            FileManager.writeToJsonFile(dirName + "/my-data/Competition.json", irMatch, activity);
                            clearSheet();
                        }
                        catch (Exception e)
                        {
                            Log.e(TAG, "write to json file exception");
                            Toast.makeText(getActivity(), "File could not be written", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(activity, "Match Result Missing!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(activity, "Match Number Missing!", Toast.LENGTH_LONG).show();
                }
            }
        });

        /*increment buttons*/

        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                incrementUp(low_att);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                incrementDown(low_att);
            }
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


        /*Firebase Firestore Test*/

//        Map<String, String> map = new HashMap<>();
//        map.put("name", "TEST");
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        db.collection("districts").add(map);







        return v;
    }

    private void incrementUp(EditText editText) {
        int val;
        if(!editText.getText().toString().equals(""))
        {val = Integer.parseInt(editText.getText().toString());}
        else
        {val = 0;}
        val ++;
        String str = Integer.toString(val);
        editText.setText(str);
    }
    private void incrementDown(EditText editText) {
        int val;
        if(!editText.getText().toString().equals("") && Integer.parseInt(editText.getText().toString()) > 0)
        {val = Integer.parseInt(editText.getText().toString());}
        else
        {val = 1;}
        val --;
        String str = Integer.toString(val);
        editText.setText(str);
    }


    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        View focusedView = activity.getCurrentFocus();
        if(focusedView!=null){
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }

    private void setupUI(View view) {

        // base case
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(activity);
                    MainFragment.this.view.findViewById(R.id.parentView).requestFocus();
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
            activity = (Activity) context;
        }
    }

    private void updateTeams(){

        String [] teamNames = new String[teams.length];

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        if(sharedPreferences.getBoolean("placeholder_teams", false)){
            teamNames = new String[teams.length + 5];

            for (int i = 0; i < teamNames.length - 5; i ++){
                teamNames[i] = teams[i].getTeamName() + ", " + teams[i].getTeamNumber();
            }
            teamNames[teamNames.length - 5] = "Team 9999, 9999";
            teamNames[teamNames.length - 4] = "Team 9998, 9998";
            teamNames[teamNames.length - 3] = "Team 9997, 9997";
            teamNames[teamNames.length - 2] = "Team 9996, 9996";
            teamNames[teamNames.length - 1] = "Team 9995, 9995";
        }
        else{
            for (int i = 0; i < teams.length; i ++){
                teamNames[i] = teams[i].getTeamName() + ", " + teams[i].getTeamNumber();
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, R.layout.spinner_item, teamNames);
        spinner.setAdapter(adapter);
    }

    private void clearSheet(){
        matchNumber.getText().clear();
        notes.getText().clear();
        radioGroup.clearCheck();
        spinner.setSelection(0);

        low_att.setText("0");
        low_made.setText("0");
        out_att.setText("0");
        out_made.setText("0");
        inn_made.setText("0");
        low_att2.setText("0");
        low_made2.setText("0");
        out_att2.setText("0");
        out_made2.setText("0");
        inn_made2.setText("0");

        switch1.setChecked(false);
        switch2.setChecked(false);
        switch3.setChecked(false);
        switch4.setChecked(false);
        switch5.setChecked(false);
        switch6.setChecked(false);
        switch7.setChecked(false);
        switch8.setChecked(false);
        switch9.setChecked(false);
    }



}
