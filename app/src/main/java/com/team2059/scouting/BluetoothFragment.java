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
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

public class BluetoothFragment extends Fragment implements ConnectionDialog.ConnectionDialogCallback {

    private Activity activity;
    private View v;

    private final static int REQUEST_ENABLE_BT = 1;
    private final static int PERMISSION_REQUEST_LOCATION = 1;

    private final String ARG_HANDLERS = "arg handlers";

    private final String TAG = "BluetoothProtocol";

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();

    private BluetoothHandler bluetoothHandler;
    private BluetoothHandler currentBluetoothHandler;

    private ArrayList<BluetoothHandler> bluetoothHandlers;
    private List<BluetoothDevice> connectedDevices;



    private List<BluetoothDevice> deviceList;
    private List<BluetoothDevice> pairedDeviceList;

    private List<String> deviceIds;

    private List<String> topics;
    //private List<String> pairedDevices;

    private Map<String, List<BluetoothDevice>> map;
    private ExpandableListView expandableListView;
    private MyExpandableAdapter expandableListAdapter;
    private List<Boolean> checkBoxStates = new ArrayList<>();

    private Button button_connect;

    private Typeface eagleLight;
    private Typeface eagleBook;




    @Override
    public void onRetryConnection() {

        //since we are retrying getbluetoothdevice should not be null
        bluetoothHandler.startClient(bluetoothHandler.getBluetoothDevice());
    }
    @Override
    public void onPair() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            bluetoothDevice.createBond();
            Toast.makeText(activity, "trying to pair", Toast.LENGTH_SHORT).show();

            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            activity.registerReceiver(bondStateReceiver, filter);

            //show paired device in new paired device list

        }
    }
    @Override
    public void onDelete() {
        if(bluetoothHandler.getConnectedThread() != null){
            bluetoothHandler.getConnectedThread().cancel();
        }
        bluetoothHandler.setDelete();

        bluetoothHandlers.remove(bluetoothHandler);
        Log.e(TAG, Integer.toString(bluetoothHandlers.size()));

        showConnectionStatus();
    }





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_bluetooth, container, false);
        v = view;


        if(savedInstanceState != null){
            bluetoothHandlers = savedInstanceState.getParcelableArrayList(ARG_HANDLERS);
            if(bluetoothHandlers == null){
                bluetoothHandlers = new ArrayList<>();
            }
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

        //pairedDevices = new ArrayList<>();
        map = new HashMap<>();
        initExListView();
        expandableListAdapter = new MyExpandableAdapter(activity, topics, map, bluetoothHandlers, checkBoxStates);
        initBluetoothHandler();
        //in case of orientation change or screen change
        if(bluetoothHandlers.size() > 0){
            showConnectionStatus();
        }

        expandableListView.setAdapter(expandableListAdapter);

        button_connect = view.findViewById(R.id.bluetooth_connect);
        //button_connect.setTypeface(eagleBook);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(final ExpandableListView parent, View v, final int groupPosition, long id) {

                //checks the state before the click is registered to expand or collapse
                //thus, if groupExpanded is true then user is currently closing the group
//                if(parent.isGroupExpanded(groupPosition) && topics.get(groupPosition).equals("Paired")){
//                    bluetoothDevices.clear();
//                    button_connect.setText("Connect");
//                }
                return false;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if(topics.get(groupPosition).equals("Available")){
                    bluetoothAdapter.cancelDiscovery();

                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
                        bluetoothDevice = deviceList.get(childPosition);
                        openDialog(bluetoothDevice);
                    }
                }
                else if(topics.get(groupPosition).equals("Paired")){
                    bluetoothAdapter.cancelDiscovery();
                    bluetoothDevice = pairedDeviceList.get(childPosition);

                    CheckBox checkBox = v.findViewById(R.id.checkBox);
                    checkBox.toggle();

                    if(!bluetoothDevices.contains(bluetoothDevice)){
                        bluetoothDevices.add(bluetoothDevice);

                    }
                    else {
                        bluetoothDevices.remove(bluetoothDevice);
                    }

                    if(bluetoothDevices.size() < 1){
                        button_connect.setText("Connect");
                    }
                    else{
                        button_connect.setText("Connect (" + bluetoothDevices.size() + ")");
                    }

                    checkBoxStates.set(childPosition, checkBox.isChecked());



                }
                else if(topics.get(groupPosition).equals("Connection Status")){
                    bluetoothHandler = bluetoothHandlers.get(childPosition);
                    openDialog(bluetoothHandler);
                }
                return false;
            }
        });


        Button button_scan = view.findViewById(R.id.bluetooth_scan);
        //button_scan.setTypeface(eagleBook);


        button_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableBluetooth();
            }
        });


        button_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBTConnection();
            }
        });

        final LinearLayout linearLayout = view.findViewById(R.id.bluetooth_linear);

        final ImageButton info = view.findViewById(R.id.bluetooth_info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(linearLayout.getVisibility() == View.INVISIBLE){
                    //linearLayout.setAlpha(0f);
                    linearLayout.setVisibility(View.VISIBLE);

                    Animation scaleUp = AnimationUtils.loadAnimation(activity, R.anim.scaleup);

                    linearLayout.startAnimation(scaleUp);

                    //info.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    info.setBackgroundTintList(activity.getResources().getColorStateList(R.color.colorPrimaryDark));
                    info.setColorFilter(ContextCompat.getColor(activity, R.color.text_primary_light), android.graphics.PorterDuff.Mode.SRC_IN);

                    TextView name = view.findViewById(R.id.bluetooth_devname);
                    SpannableString nickname = new SpannableString("Your Device Nickname: " + Settings.Secure.getString(activity.getContentResolver(), "bluetooth_name"));
                    nickname.setSpan(new UnderlineSpan(), 22, nickname.length(), 0);
                    name.setText(nickname);
                    TextView address = view.findViewById(R.id.bluetooth_devaddress);
                    address.setText("You can rename your device in the settings menu");
                }
                else{
                    Animation scaleDown = AnimationUtils.loadAnimation(activity, R.anim.scaledown);
                    linearLayout.startAnimation(scaleDown);
                    linearLayout.setVisibility(View.INVISIBLE);

                    info.setBackgroundTintList(activity.getResources().getColorStateList(R.color.text_primary_light));
                    info.setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);
                }

            }
        });

        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }


    public void startBTConnection(){
//        if(bluetoothDevice != null && isDuplicateConnection(bluetoothDevice) < 0){
//            Log.e(TAG, "startBTConnection:");
//            currentBluetoothHandler.startClient(bluetoothDevice);
//        }
        if(bluetoothDevices.size() > 0){
            for(int i = 0; i < bluetoothDevices.size(); i ++){

                if(isDuplicateConnection(bluetoothDevices.get(i)) < 0){
                    BluetoothHandler bluetoothHandler = new BluetoothHandler(activity, new BluetoothHandler.BluetoothHandlerCallback() {
                        @Override
                        public void onBluetoothHandlerCallback(BluetoothHandler bluetoothHandler) {
                            if(!bluetoothHandlers.contains(bluetoothHandler) && !bluetoothHandler.getDelete()){

//                                int index = isDuplicateConnection(bluetoothHandler.getBluetoothDevice());
//                                if(index >= 0){
//                                    bluetoothHandlers.remove(index);
//                                }
                                bluetoothHandlers.add(bluetoothHandler);
                                Log.e("TAG", "moving " + bluetoothHandler.getBluetoothDevice() + "to permanent");
                            }
                            showConnectionStatus();
                        }

                        @Override
                        public void onConnectionCallback() {

                        }
                    });
                    bluetoothHandler.startClient(bluetoothDevices.get(i));
                }
                else{
                    Toast.makeText(activity,
                            bluetoothDevices.get(i).getName() + " is already connected or attempted to connect", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else{
            Toast.makeText(activity, "No device(s) selected", Toast.LENGTH_SHORT).show();
        }
//        else if(bluetoothDevice == null){
//            Toast.makeText(activity, "No device(s) selected", Toast.LENGTH_SHORT).show();
//        }
//        else{
//            Toast.makeText(activity, "Device(s) already connected or attempted to connect", Toast.LENGTH_SHORT).show();
//        }
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

        pairedDeviceList.clear();
        bluetoothDevices.clear();
        checkBoxStates.clear();
        button_connect.setText("Connect");
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0){
            for(BluetoothDevice device : pairedDevices){

                //String str = device.getName() + " " + device.getAddress();

                pairedDeviceList.add(device);
                //this.pairedDevices.add(str);
                checkBoxStates.add(false);

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

    private final BroadcastReceiver bondStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, action);

            if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                final int state = device.getBondState();

                switch (state){
                    case BluetoothDevice.BOND_NONE:
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        pairedDeviceList.add(device);
                        expandableListAdapter.notifyDataSetChanged();
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
    }

    public void initBluetoothHandler(){

        currentBluetoothHandler = new BluetoothHandler(activity, new BluetoothHandler.BluetoothHandlerCallback() {
            @Override
            public void onBluetoothHandlerCallback(BluetoothHandler bluetoothHandler) {
                if(!bluetoothHandlers.contains(bluetoothHandler) && !bluetoothHandler.getDelete()){

                    int index = isDuplicateConnection(bluetoothHandler.getBluetoothDevice());
                    if(index >= 0){
                        bluetoothHandlers.remove(index);
                    }
                    bluetoothHandlers.add(bluetoothHandler);
                    Log.e("TAG", "moving " + bluetoothHandler.getBluetoothDevice() + "to permanent");
                }
                showConnectionStatus();
                if(!bluetoothHandler.getInitAttemptDone()){
                    bluetoothHandler.setInitAttemptDone();
                    initBluetoothHandler();

                }
            }

            @Override
            public void onConnectionCallback() {

            }
        });

        currentBluetoothHandler.start();
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
            Log.e("BEST", device.toString());
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

    public int isDuplicateConnection(BluetoothDevice bluetoothDevice){
        for(int i = 0; i < bluetoothHandlers.size(); i ++){
            if(bluetoothHandlers.get(i).getBluetoothDevice().equals(bluetoothDevice)){
                return i;
            }
        }
        return -1;
    }





    public void openDialog(BluetoothHandler bluetoothHandler){
        ConnectionDialog dialog = ConnectionDialog.newInstance(bluetoothHandler);
        dialog.show(getActivity().getSupportFragmentManager(), "dialog");


    }

    public void openDialog(BluetoothDevice device){
        ConnectionDialog dialog = ConnectionDialog.newInstance(device);
        dialog.show(getActivity().getSupportFragmentManager(), "dialog");
    }

}
