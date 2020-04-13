package com.team2059.scouting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.team2059.scouting.frc_api_client.Competition;
import org.team2059.scouting.frc_api_client.Event;
import org.team2059.scouting.frc_api_client.HttpHandler;

import java.io.IOException;
import java.util.ArrayList;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Menu extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    TextView textView;
    HttpHandler hh = new HttpHandler();
    ArrayList<Competition> comps;
    ArrayList<String> display = new ArrayList<>();
    ArrayList<String> display2 = new ArrayList<>();
    String [] teams;
    int index;
    int indexDistrict;
    String [] temp = {"-"};
    Event [] events;

    Spinner spinner;
    Spinner spinner1;
    Spinner spinner2;
    Spinner spinner3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        final String AUTHORIZATION_KEY = "YOUR KEY HERE";
        final String HOST = "https://frc-api.firstinspires.org/v2.0/";

        final String [] CREDENTIALS = AUTHORIZATION_KEY.split(":");
        final String USER = CREDENTIALS[0];
        final String PASS = CREDENTIALS[1];


        hh.setToken(AUTHORIZATION_KEY);


        //Log.e("HTTP", Integer.toString(hh.getStatus()));


        final SwipeRefreshLayout refreshLayout = findViewById(R.id.swipe);

        textView = findViewById(R.id.menu_text1);
        Button button = findViewById(R.id.menu_button);

        final String[] array = {"District", "Regional", "Championship"};


        spinner1 = findViewById(R.id.menu_spinner1);
        spinner2 =  findViewById(R.id.menu_spinner2);
        spinner3 = findViewById(R.id.menu_spinner3);
        spinner = findViewById(R.id.spinner1); //main activity

        spinner1.setOnItemSelectedListener(this);


        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, R.layout.spinner_item, array);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.spinner_item, temp);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, R.layout.spinner_item, array);


        spinner1.setAdapter(adapter1);
        spinner2.setAdapter(adapter2);
        spinner3.setAdapter(adapter3);


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                String selected = spinner1.getSelectedItem().toString();
                String selected2 = spinner2.getSelectedItem().toString();
                indexDistrict = spinner2.getSelectedItemPosition();




                if(selected.equals("District")){
                    if(!selected2.equals("-")){
                        String districtCode = comps.get(indexDistrict).getCode();
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
                        String eventCode = comps.get(indexDistrict).getEvents()[index].getCode();
                        String query = "2020/teams?eventCode=" + eventCode;
                        Request request = hh.getRequest(query);
                        startCall(request, "teamOfEvent");

                    }
                    else{
                        String eventCode = comps.get(index).getCode();
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Menu.this, R.layout.spinner_item, temp);
        if(!parent.getSelectedItem().toString().equals("District")){
            spinner2.setAdapter(adapter);
        }
        else{
            spinner3.setAdapter(adapter);
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
                            comps = hh.getDistricts(jsonArray);
                        }
                        else if(key.equals("regional")){
                            display2.clear();
                            JSONArray jsonArray = hh.fetchAsJsonArr(myresponse, "Events");
                            comps = hh.getRegionals(jsonArray);
                        }
                        else if(key.equals("championship")){
                            display2.clear();
                            JSONArray jsonArray = hh.fetchAsJsonArr(myresponse, "Events");
                            comps = hh.getChampionships(jsonArray);
                        }
                        else if(key.equals("team")){
                            JSONArray jsonArray = hh.fetchAsJsonArr(myresponse, "teams");
                            teams = hh.getTeams(comps.get(index), jsonArray);
                        }
                        else if(key.equals("event")){
                            display2.clear();
                            JSONArray jsonArray = hh.fetchAsJsonArr(myresponse, "Events");
                            events = hh.getEvents(comps.get(indexDistrict), jsonArray);
                        }
                        else{
                            JSONArray jsonArray = hh.fetchAsJsonArr(myresponse, "teams");
                            teams = hh.getTeams(comps.get(indexDistrict).getEvents()[index], jsonArray);
                        }
                        if(!key.equals("team") && !key.equals("event")){
                            for(Competition comp : comps){
                                if(key.equals("district")){
                                    display.add(comp.getName());
                                }
                                else{
                                    display2.add(comp.getName());
                                }
                            }
                        }
                        else if(key.equals("event")){
                            for(Event e : events){
                                display2.add(e.getName());
                            }
                        }

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        Log.e("HTTP", e.toString());
                    }



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
            }
        });
    }


}
