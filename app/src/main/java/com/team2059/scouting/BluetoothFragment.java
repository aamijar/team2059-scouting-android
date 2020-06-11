package com.team2059.scouting;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import android.util.Log;

public class BluetoothFragment extends Fragment {

    private Activity activity;
    private View v;

    private final static int REQUEST_ENABLE_BT = 1;
    private final static int PERMISSION_REQUEST_LOCATION = 1;

    private final String ARG_HANDLERS = "arg handlers";

    private final String TAG = "BluetoothProtocol";

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;

    private BluetoothHandler bluetoothHandler;


    private ArrayList<BluetoothHandler> bluetoothHandlers;
    private List<BluetoothDevice> connectedDevices;



    private ArrayList<BluetoothDevice> deviceList;
    private List<BluetoothDevice> pairedDeviceList;

    private List<String> deviceIds;

    private List<String> topics;
    private List<String> pairedDevices;

    private Map<String, List<BluetoothDevice>> map;
    private ExpandableListView expandableListView;
    private MyExpandableAdapter expandableListAdapter;


    private Typeface eagleLight;
    private Typeface eagleBook;

    private BluetoothFragmentListener listener;



    public interface BluetoothFragmentListener{
        void onBluetoothHandlerAttached(ArrayList<BluetoothHandler> bluetoothHandlers);
    }




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_bluetooth, container, false);
        v = view;


        if(savedInstanceState != null){
            bluetoothHandlers = savedInstanceState.getParcelableArrayList(ARG_HANDLERS);
        }
        else{
            if(bluetoothHandlers == null){
                bluetoothHandlers = new ArrayList<>();
            }
        }

        eagleLight = Typeface.createFromAsset(activity.getAssets(), "fonts/eagle_light.otf");
        eagleBook = Typeface.createFromAsset(activity.getAssets(), "fonts/eagle_book.otf");
        //Typeface eagleBold = Typeface.createFromAsset(getAssets(), "fonts/eagle_bold.otf");

        deviceIds = new ArrayList<>();
        deviceList = new ArrayList<>();
        pairedDeviceList = new ArrayList<>();

        expandableListView = view.findViewById(R.id.bluetooth_listViewPaired);

        topics = new ArrayList<>();
        topics.add("Paired");
        topics.add("Available");
        topics.add("Connection Status");

        pairedDevices = new ArrayList<>();
        map = new HashMap<>();
        initExListView();
        expandableListAdapter = new MyExpandableAdapter(activity, topics, map, bluetoothHandlers);
        showConnectionStatus();

        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if(topics.get(groupPosition).equals("Available") && !isDuplicateConnection(deviceList.get(childPosition))){
                    bluetoothAdapter.cancelDiscovery();

                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
                        deviceList.get(childPosition).createBond();
                        bluetoothDevice = deviceList.get(childPosition);

                        bluetoothHandlers.add(new BluetoothHandler(activity, bluetoothDevice, new BluetoothHandler.BluetoothHandlerCallback() {
                            @Override
                            public void onBluetoothHandlerCallback() {
                                showConnectionStatus();
                            }
                        }));
                        bluetoothHandler = bluetoothHandlers.get(bluetoothHandlers.size() - 1);

                        bluetoothHandler.start();
                    }
                }
                else if(topics.get(groupPosition).equals("Paired") && !isDuplicateConnection(pairedDeviceList.get(childPosition))){
                    bluetoothAdapter.cancelDiscovery();

                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
                        pairedDeviceList.get(childPosition).createBond();
                        bluetoothDevice = pairedDeviceList.get(childPosition);

                        bluetoothHandlers.add(new BluetoothHandler(activity, bluetoothDevice, new BluetoothHandler.BluetoothHandlerCallback() {
                            @Override
                            public void onBluetoothHandlerCallback() {
                                showConnectionStatus();
                            }
                        }));
                        bluetoothHandler = bluetoothHandlers.get(bluetoothHandlers.size() - 1);

                        bluetoothHandler.start();
                    }
                }
                return false;
            }
        });


//        arrayAdapter = new ArrayAdapter<>(activity, R.layout.spinner_item, deviceIds);
//        listView.setAdapter(arrayAdapter);


        Button button = view.findViewById(R.id.bluetooth_connect);
        button.setTypeface(eagleBook);

        Button button_connect = view.findViewById(R.id.bluetooth_start_connection);
        button_connect.setTypeface(eagleBook);

        Button button_send = view.findViewById(R.id.bluetooth_send);
        button_send.setTypeface(eagleBook);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableBluetooth();
            }
        });


        button_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bluetoothHandler.getAcceptThread() != null){
                    startBTConnection();
                }
            }
        });

        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bluetoothHandler.getConnectedThread() != null){
                    String input = "Hello"; //hardcoded message
                    bluetoothHandler.write(input);
                    listener.onBluetoothHandlerAttached(bluetoothHandlers);
                }
            }
        });

        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof BluetoothFragmentListener){
            activity = (Activity) context;
            listener = (BluetoothFragmentListener) context;
        }else{
            throw new RuntimeException(context.toString() + " must implement BluetoothFragmentListener");
        }
    }


    public void startBTConnection(){
        Log.e(TAG, "startBTConnection:");
        bluetoothHandler.startClient();
    }


    public void enableBluetooth(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter == null){
            Log.e("Blue", "Device does not support bluetooth");
        }
        else if(!bluetoothAdapter.isEnabled()){
            Intent discoverableIntent =
                    new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 10);
            startActivityForResult(discoverableIntent, REQUEST_ENABLE_BT);
        }
        else{
            showPairedDevices();
            bluetoothAdapter.startDiscovery();
        }

        //must ask for permission each time
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_REQUEST_LOCATION);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(receiver, filter);

        IntentFilter filterMode = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        activity.registerReceiver(receiver, filterMode);
    }

    public void showPairedDevices(){

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0){
            for(BluetoothDevice device : pairedDevices){

                String str = device.getName() + " " + device.getAddress();

                pairedDeviceList.add(device);
                this.pairedDevices.add(str);
                expandableListAdapter.notifyDataSetChanged();
            }
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_CANCELED){
            Log.e("Blue", "bluetooth access denied");
        }

        //resultCode is the number of seconds device is discoverable
        else{
            Log.e("Blue", "bluetooth enabled");
            showPairedDevices();
            bluetoothAdapter.startDiscovery();
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.e(TAG, action);

            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String str;
                if(device.getName() == null){
                    str = "Unknown " + device.getAddress();
                }
                else{
                    str = device.getName() + " " + device.getAddress();
                }

                deviceList.add(device);
                deviceIds.add(str);

                expandableListAdapter.notifyDataSetChanged();
            }
            if(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)){
                //Toast.makeText(BluetoothActivity.this, "Device Discoverable ENDED", Toast.LENGTH_SHORT).show();

                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (state){
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        //Toast.makeText(BluetoothActivity.this, "Device is discoverable", Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        //Toast.makeText(BluetoothActivity.this, "Device is not discoverable", Toast.LENGTH_LONG).show();
                        //progressBar.setVisibility(View.INVISIBLE);
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        //Toast.makeText(BluetoothActivity.this, "WARNING: Bluetooth may be off", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(receiver.isOrderedBroadcast()){
            activity.unregisterReceiver(receiver);
            activity.unregisterReceiver(receiver);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(activity, "Unable to Discover Devices", Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    public void initExListView(){
        connectedDevices = new ArrayList<>();

        map.put(topics.get(0), pairedDeviceList);
        map.put(topics.get(1), deviceList);
        map.put(topics.get(2), connectedDevices);
    }

    public void showConnectionStatus(){
        connectedDevices.clear();
        for(BluetoothHandler bh : bluetoothHandlers){
            BluetoothDevice device = bh.getBluetoothDevice();

            connectedDevices.add(device);
        }

        //must be run on correct thread, since showConnectionStatus can be run from
        // BlutoothHandler threads
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                expandableListAdapter.notifyDataSetChanged();
            }
        });
    }

    //to support orientation change
    //note: fragment is not destroyed until activity is destroyed
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ARG_HANDLERS, bluetoothHandlers);
    }

    public boolean isDuplicateConnection(BluetoothDevice bluetoothDevice){
        for(BluetoothHandler bh : bluetoothHandlers){
            if(bh.getBluetoothDevice().equals(bluetoothDevice)){
                return true;
            }
        }
        return false;
    }
}
