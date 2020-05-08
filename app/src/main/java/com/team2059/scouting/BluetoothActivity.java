package com.team2059.scouting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.team2059.scouting.Main;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity{

    private final static int REQUEST_ENABLE_BT = 1;
    private final static int PERMISSION_REQUEST_LOCATION = 1;
    private final UUID MY_UUID = UUID.fromString("ad9c9615-f9fc-4bf8-8314-2894e4568ad7");
    private final String TAG = "BluetoothProtocol";

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;



    private ProgressBar progressBar;
    private ListView listView;

    private ArrayList<BluetoothDevice> deviceList;
    private ArrayList<String> deviceIds;
    private ArrayAdapter<String> arrayAdapter;

    private Typeface eagleLight;
    private Typeface eagleBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE); //hide progress bar initially

        eagleLight = Typeface.createFromAsset(getAssets(), "fonts/eagle_light.otf");
        eagleBook = Typeface.createFromAsset(getAssets(), "fonts/eagle_book.otf");
        //Typeface eagleBold = Typeface.createFromAsset(getAssets(), "fonts/eagle_bold.otf");
        listView = findViewById(R.id.listView);

        deviceList = new ArrayList<>();
        deviceIds = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, deviceIds);
        listView.setAdapter(arrayAdapter);


        Button button = findViewById(R.id.bluetooth_connect);
        button.setTypeface(eagleBook);

        Button button_connect = findViewById(R.id.bluetooth_start_connection);
        button_connect.setTypeface(eagleBook);

        Button button_send = findViewById(R.id.bluetooth_send);
        button_send.setTypeface(eagleBook);

        Button button_next = findViewById(R.id.bluetooth_next);
        button_next.setTypeface(eagleBook);

        final EditText editText = findViewById(R.id.bluetooth_message);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableBluetooth();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothAdapter.cancelDiscovery();

                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
                    deviceList.get(position).createBond();
                    bluetoothDevice = deviceList.get(position);

                    start();
                }
            }
        });

        button_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startConnection();
            }
        });

        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte [] bytes = editText.getText().toString().getBytes(Charset.defaultCharset());
                write(bytes);
            }
        });

        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BluetoothActivity.this, Menu.class);
                startActivity(intent);
            }
        });



    }


    public void startConnection(){
        startBTConnection(bluetoothDevice);
    }


    //without uuid
    public void startBTConnection(BluetoothDevice device){
        Log.e(TAG, "startBTConnection:");
        startClient(device);
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
            //progressBar.setVisibility(View.VISIBLE);
        }

        //must ask for permission each time
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_REQUEST_LOCATION);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        IntentFilter filterMode = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(receiver, filterMode);
    }

    public void showPairedDevices(){

        LinearLayout layout = findViewById(R.id.bluetooth_paireddev);

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0){
            for(BluetoothDevice device : pairedDevices){
//                Log.e("Blue", device.getName());
//                Log.e("Blue", device.getAddress());
//                Toast.makeText(this, device.getName(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(this, device.getAddress(), Toast.LENGTH_SHORT).show();
                TextView row = new TextView(BluetoothActivity.this);
                String str = device.getName() + " " + device.getAddress();
                row.setText(str);
                row.setTypeface(eagleLight);
                layout.addView(row);

                deviceList.add(device);
                deviceIds.add(device.getName() + " " + device.getAddress());
                arrayAdapter.notifyDataSetChanged();
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_CANCELED){
            Log.e("Blue", "bluetooth off");
        }

        //resultCode is the number of seconds device is discoverable
        else{
            Log.e("Blue", "bluetooth enabled");
            bluetoothAdapter.startDiscovery();
            progressBar.setVisibility(View.VISIBLE);
            showPairedDevices();

        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.e(TAG, action);

            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                LinearLayout layout = findViewById(R.id.bluetooth_linearlayout);
                TextView row = new TextView(BluetoothActivity.this);
                String str;
                if(device.getName() == null){
                    str = "Unknown " + device.getAddress();
                }
                else{
                    str = device.getName() + " " + device.getAddress();
                }
                row.setText(str);
                row.setTypeface(eagleLight);
                layout.addView(row);

                deviceList.add(device);
                deviceIds.add(str);
                arrayAdapter.notifyDataSetChanged();
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
                        progressBar.setVisibility(View.INVISIBLE);
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        //Toast.makeText(BluetoothActivity.this, "WARNING: Bluetooth may be off", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver.isOrderedBroadcast()){
            unregisterReceiver(receiver);
            unregisterReceiver(receiver);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {


        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Unable to Discover Devices", Toast.LENGTH_LONG).show();
        }
    }

    //Socket-Server/Client Side

    /*server side*/

    private class AcceptThread extends Thread {

        private BluetoothServerSocket serverSocket;
        private final String APP_NAME = "frc2059 Scouting";



        public AcceptThread(){
            BluetoothServerSocket tmp = null;

            try{
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
            }
            catch (IOException e){
                Log.e("Blue", "Socket's listen method failed", e);
            }
            serverSocket = tmp;
        }

        public void run(){
            BluetoothSocket socket = null;

            while (true){
                try{
                    socket = serverSocket.accept();
                }
                catch (IOException e){
                    Log.e("Blue", "Socket's accept method failed", e);
                    break;
                }
                if(socket != null){
                    //TODO connection accepted, perform work on separate thread
                    connected(socket, bluetoothDevice);
                    Log.e(TAG, "CONNECTION ACCEPTED");
                    try {
                        serverSocket.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }

            }

        }

        public void cancel(){
            try {
                serverSocket.close();
            }
            catch (IOException e){
                Log.e("Blue", "Could not close connect socket", e);
            }
        }


    }

    /*client side*/
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device){
            BluetoothSocket tmp = null;
            mmDevice = device;

            try{
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e("Blue", "Socket's create() method failed");
            }
            mmSocket = tmp;

        }

        public void run(){
            bluetoothAdapter.cancelDiscovery();

            try {
                mmSocket.connect();
            } catch (IOException e) {
                try{
                    mmSocket.close();
                } catch (IOException ex) {
                    Log.e("Blue", "Could not close the client socket", ex);
                }
                Log.e("Blue", "Could not connect to UUID" + MY_UUID);
                return;
            }
            Log.e(TAG, "CLIENT CONNECTION SUCCEEDED");
            connected(mmSocket, mmDevice);
            //TODO connection succeeded, attempt work in separate thread
        }

        public void cancel(){
            try{
                mmSocket.close();
            } catch (IOException e) {
                Log.e("Blue", "Could not close the client socket", e);
            }
        }

    }


    public synchronized void start(){

        if(connectThread != null){
            connectThread.cancel();
            connectThread = null;
        }
        if(acceptThread == null){
            acceptThread = new AcceptThread();
            acceptThread.start();
        }

    }

    public void startClient(BluetoothDevice device){

        Log.e(TAG, "started Client");
        progressBar.setVisibility(View.VISIBLE);

        connectThread = new ConnectThread(device);
        connectThread.start();
    }

    private class ConnectedThread extends Thread{

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        private byte[] mmBuffer;

        public ConnectedThread(BluetoothSocket socket){

            Log.e("Blue", "Connected Thread : Starting");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                progressBar.setVisibility(View.INVISIBLE);
            }catch (Exception e){
                e.printStackTrace();
            }


            try{
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            }catch (IOException e){
                Log.e("Blue", "Connected Thread failed to get streams", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

        }

        public void run(){
            mmBuffer = new byte[1024];

            int bytes; // bytes returned from stream

            try {
                Log.e(TAG, Integer.toString(mmInStream.available()));
            }catch (IOException e){
                e.printStackTrace();
            }

            while(true){
                try{
                    bytes = mmInStream.read(mmBuffer);
                    final String incomingMessage = new String(mmBuffer, 0, bytes);
                    //Toast.makeText(BluetoothActivity.this, incomingMessage, Toast.LENGTH_SHORT).show();
                    Log.e("MESSAGE", "Input Stream: " + incomingMessage);

                    BluetoothActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BluetoothActivity.this, incomingMessage, Toast.LENGTH_LONG).show();
                        }
                    });


                } catch (IOException e) {
                    Log.e("Blue", "Unable to read from input stream", e);
                    break;
                }
            }
        }

        public void write(byte[] bytes){
            String text = new String(bytes, Charset.defaultCharset());
            Log.e("Blue", "writing to output stream" + text);

            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e("Blue", "write: Error writing to output stream " + e.getMessage());
            }
        }

        public void cancel(){
            try{
                mmSocket.close();
            } catch (IOException e) {
                Log.e("Blue", "Could not close connected socket", e);
            }
        }




    }

    public void connected(BluetoothSocket socket, BluetoothDevice device){

        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
    }

    public void write(byte[] out){
        //temp object
        ConnectedThread r;

        Log.e("Blue", "write called");
        connectedThread.write(out);

    }



}
