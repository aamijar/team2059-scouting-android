package com.team2059.scouting;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import org.json.simple.JSONArray;
import org.team2059.scouting.frc_api_client.Competition;
import org.team2059.scouting.frc_api_client.Event;
import org.team2059.scouting.frc_api_client.HttpHandler;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Menu extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    TextView textView;
    HttpHandler hh = new HttpHandler();
    //ArrayList<Competition> comps;

    ArrayList<Competition> districtComps = new ArrayList<>();
    ArrayList<Competition> regionalComps = new ArrayList<>();
    ArrayList<Competition> champComps = new ArrayList<>();

    ArrayList<String> display = new ArrayList<>();
    ArrayList<String> display2 = new ArrayList<>();
    String [] teams;
    int index;
    int indexDistrict;
    String [] temp = {"-"};
    Event [] events;

    Spinner spinner1;
    Spinner spinner2;
    Spinner spinner3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        final String AUTHORIZATION_KEY = "aamijar:E9B244D8-B9FF-4BD6-9BF7-AA763A72292B";
        final String HOST = "https://frc-api.firstinspires.org/v2.0/";

        final String [] CREDENTIALS = AUTHORIZATION_KEY.split(":");
        final String USER = CREDENTIALS[0];
        final String PASS = CREDENTIALS[1];


        hh.setToken(AUTHORIZATION_KEY);


        final SwipeRefreshLayout refreshLayout = findViewById(R.id.swipe);

        textView = findViewById(R.id.menu_text1);

        Button button = findViewById(R.id.menu_button);
        TextView sync = findViewById(R.id.menu_sync);

        Typeface eagleLight = Typeface.createFromAsset(getAssets(), "fonts/eagle_light.otf");
        Typeface eagleBook = Typeface.createFromAsset(getAssets(), "fonts/eagle_book.otf");
        Typeface eagleBold = Typeface.createFromAsset(getAssets(), "fonts/eagle_bold.otf");

        button.setTypeface(eagleBook);
        sync.setTypeface(eagleLight);


        final String[] array = {"District", "Regional", "Championship"};


        spinner1 = findViewById(R.id.menu_spinner1);
        spinner2 =  findViewById(R.id.menu_spinner2);
        spinner3 = findViewById(R.id.menu_spinner3);

        spinner1.setOnItemSelectedListener(this);
        spinner2.setOnItemSelectedListener(this);


        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, R.layout.spinner_item, array);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.spinner_item, temp);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, R.layout.spinner_item, temp);

        spinner1.setAdapter(adapter1);
        spinner2.setAdapter(adapter2);
        spinner3.setAdapter(adapter3);


        /*load comp data from user prefs*/
        loadData();


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                String selected = spinner1.getSelectedItem().toString();
                String selected2 = spinner2.getSelectedItem().toString();
                indexDistrict = spinner2.getSelectedItemPosition();




                if(selected.equals("District")){
                    if(!selected2.equals("-")){
                        String districtCode = districtComps.get(indexDistrict).getCode();
                        String query = "2020/events?districtCode=" + districtCode;
                        Request request = hh.getRequest(query);
                        startCall(request, "event");
                    }
                    else{
                        String query = "2020/districts";
                        Request request = hh.getRequest(query);
                        startCall(request, "district");
                    }
                }
                else if(selected.equals("Regional")){
                    String query = "2020/events";
                    Request request = hh.getRequest(query);
                    startCall(request, "regional");
                }
                else{
                    String query = "2020/events";
                    Request request = hh.getRequest(query);
                    startCall(request, "championship");
                }

                refreshLayout.setRefreshing(false);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(spinner3.getSelectedItem().toString().equals("-")){
                    Toast.makeText(Menu.this, "No Event Specified", Toast.LENGTH_LONG).show();
                }
                else{
                    index = spinner3.getSelectedItemPosition();
                    if(!spinner2.getSelectedItem().equals("-")){
                        String eventCode = districtComps.get(indexDistrict).getEvents()[index].getCode();
                        String query = "2020/teams?eventCode=" + eventCode;
                        Request request = hh.getRequest(query);
                        startCall(request, "teamOfEvent");

                    }
                    else{
                        String eventCode;
                        if(spinner1.getSelectedItem().toString().equals("Regional")){
                            eventCode = regionalComps.get(index).getCode();
                        }
                        else{
                            eventCode = champComps.get(index).getCode();
                        }
                        String query = "2020/teams?eventCode=" + eventCode;
                        Request request = hh.getRequest(query);
                        startCall(request, "team");
                    }

                }

            }
        });



    }
    public void openMainActivity(String [] teams){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("com.team2059.scouting.teams", teams);
        startActivity(intent);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.e("SPINNER", Integer.toString(parent.getSelectedItemPosition()));
        ArrayAdapter<String> tempAdapter = new ArrayAdapter<String>(Menu.this, R.layout.spinner_item, temp);




        if(parent.getSelectedItem().toString().equals("District")){
            spinner3.setAdapter(tempAdapter);
            display.clear();
            for(Competition comp:districtComps)
            {
                display.add(comp.getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Menu.this, R.layout.spinner_item, display);
            if(!display.isEmpty()){
                Log.e("NULL", display.toString());
                spinner2.setAdapter(adapter);
            }


            //showAsDialog((Spinner) parent);


        }
        else if(parent.getSelectedItem().toString().equals("Regional")){
            spinner2.setAdapter(tempAdapter);
            display2.clear();
            for(Competition comp:regionalComps)
            {
                display2.add(comp.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Menu.this, R.layout.spinner_item, display2);
            if(!display2.isEmpty()){
                spinner3.setAdapter(adapter);
            }
        }
        else if(parent.getSelectedItem().toString().equals("Championship")){
            spinner2.setAdapter(tempAdapter);
            display2.clear();
            for(Competition comp:champComps)
            {
                display2.add(comp.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Menu.this, R.layout.spinner_item, display2);
            if(!display2.isEmpty()){
                spinner3.setAdapter(adapter);
            }
        }
        else if(parent.getId() == R.id.menu_spinner2 && !spinner2.getSelectedItem().equals("-")){
            indexDistrict = spinner2.getSelectedItemPosition();


            if(!districtComps.isEmpty() && districtComps.get(indexDistrict).getEvents() != null)
            {
                display2.clear();
                for(Event e :districtComps.get(indexDistrict).getEvents()){
                    display2.add(e.getName());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Menu.this, R.layout.spinner_item, display2);
                if(!display2.isEmpty()){
                    spinner3.setAdapter(adapter);
                }
            }
            else{
                spinner3.setAdapter(tempAdapter);
            }

        }



    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /*first events api server call*/
    public void startCall(final Request request, final String key)
    {
        OkHttpClient client = new OkHttpClient();


        /*Asynchronous call using enqueue thread*/
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("HTTP", "NO INTERNET");
                Menu.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Menu.this, "NO INTERNET", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if(response.isSuccessful())
                {
                    final String myresponse = response.body().string();
                    try {
                        //comps.clear();
                        if(key.equals("district")){
                            display.clear();
                            JSONArray jsonArray = hh.fetchAsJsonArr(myresponse, "districts");
                            districtComps = hh.getDistricts(jsonArray);

                        }
                        else if(key.equals("regional")){
                            display2.clear();
                            JSONArray jsonArray = hh.fetchAsJsonArr(myresponse, "Events");
                            regionalComps = hh.getRegionals(jsonArray);
                        }
                        else if(key.equals("championship")){
                            display2.clear();
                            JSONArray jsonArray = hh.fetchAsJsonArr(myresponse, "Events");
                            champComps = hh.getChampionships(jsonArray);
                        }
                        else if(key.equals("team")){
                            JSONArray jsonArray = hh.fetchAsJsonArr(myresponse, "teams");

                            if(spinner1.getSelectedItem().equals("Regional")){
                                teams = hh.getTeams(regionalComps.get(index), jsonArray);
                            }
                            else{
                                teams = hh.getTeams(champComps.get(index), jsonArray);
                            }
                        }
                        else if(key.equals("event")){
                            display2.clear();
                            JSONArray jsonArray = hh.fetchAsJsonArr(myresponse, "Events");
                            events = hh.getEvents(districtComps.get(indexDistrict), jsonArray);
                        }
                        else{
                            JSONArray jsonArray = hh.fetchAsJsonArr(myresponse, "teams");
                            teams = hh.getTeams(districtComps.get(indexDistrict).getEvents()[index], jsonArray);
                        }

                        if(key.equals("district")){
                            for(Competition comp : districtComps){
                                display.add(comp.getName());
                            }
                        }
                        else if(key.equals("regional")){
                            for(Competition comp : regionalComps){
                                display2.add(comp.getName());
                            }
                        }
                        else if(key.equals("championship")){
                            for(Competition comp : champComps){
                                display2.add(comp.getName());
                            }
                        }
                        else if(key.equals("event")){
                            for(Event e : districtComps.get(indexDistrict).getEvents()){
                                display2.add(e.getName());
                            }
                        }

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        Log.e("HTTP", e.toString());
                    }


                    /*Run on main thread*/
                    Menu.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(key.equals("district")){
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(Menu.this, R.layout.spinner_item, display);
                                spinner2.setAdapter(adapter);
                            }
                            else if(key.equals("regional") || key.equals("championship") || key.equals("event")){
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(Menu.this, R.layout.spinner_item, display2);
                                spinner3.setAdapter(adapter);
                            }
                            else{
                                openMainActivity(teams);
                            }

                        }
                    });
                }
                else{
                    Log.e("HTTP", "Not Success");
                    Log.e("HTTP", request.headers().toString());
                    Log.e("HTTP", response.message());

                    Menu.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Menu.this, "HTTP Error: " + response.message(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }
                /*save comp data in user prefs*/
                saveData();
            }
        });
    }


    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String districtCompsJson = gson.toJson(districtComps);
        String regionalCompsJson = gson.toJson(regionalComps);
        String champCompsJson = gson.toJson(champComps);

        editor.putString("districtCompsJson", districtCompsJson);
        editor.putString("regionalCompsJson", regionalCompsJson);
        editor.putString("champCompsJson", champCompsJson);
        editor.apply();
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String districtCompsJson = sharedPreferences.getString("districtCompsJson", null);
        String regionalCompsJson = sharedPreferences.getString("regionalCompsJson", null);
        String champCompsJson = sharedPreferences.getString("champCompsJson", null);

        Type type = new TypeToken<ArrayList<Competition>>(){}.getType();

        districtComps = gson.fromJson(districtCompsJson, type);
        regionalComps = gson.fromJson(regionalCompsJson, type);
        champComps = gson.fromJson(champCompsJson, type);

        if(districtComps == null) {
            districtComps = new ArrayList<>();
        }
        if(regionalComps == null){
            regionalComps = new ArrayList<>();
        }
        if(champComps == null){
            champComps = new ArrayList<>();
        }

    }



    /*for spinner dialog box
    * not in use*/
    public void showAsDialog(final Spinner spinner){

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Select an Option");

//        LayoutInflater layoutInflater = this.getLayoutInflater();
//
//        View view = layoutInflater.inflate(R.layout.spinner_item_test, null);
//
//
//        b.setView(view);
//
//        Spinner mSpinner = view.findViewById(R.id.dia_spinner);
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, types);
//
//        mSpinner.setAdapter(adapter);

        String [] items = new String[spinner.getAdapter().getCount()];

        for(int i = 0; i < spinner.getAdapter().getCount(); i ++){
            items[i] = (String) spinner.getAdapter().getItem(i);
        }


        b.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                spinner.setSelection(which);
            }
        });

        b.show();
    }



}
