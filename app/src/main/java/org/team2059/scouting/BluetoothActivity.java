package org.team2059.scouting;

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

//import org.team2059.scouting.Main;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

@Deprecated


public class BluetoothActivity extends AppCompatActivity{

    private final static int REQUEST_ENABLE_BT = 1;
    private final static int PERMISSION_REQUEST_LOCATION = 1;
    private final UUID MY_UUID = UUID.fromString("ad9c9615-f9fc-4bf8-8314-2894e4568ad7");
    private final String TAG = "BluetoothProtocol";

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
//    private AcceptThread acceptThread;
//    private ConnectThread connectThread;
//    private ConnectedThread connectedThread;



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

//        progressBar = findViewById(R.id.progressBar);
//        progressBar.setVisibility(View.INVISIBLE); //hide progress bar initially
//
//        eagleLight = Typeface.createFromAsset(getAssets(), "fonts/eagle_light.otf");
//        eagleBook = Typeface.createFromAsset(getAssets(), "fonts/eagle_book.otf");
//        //Typeface eagleBold = Typeface.createFromAsset(getAssets(), "fonts/eagle_bold.otf");
//        listView = findViewById(R.id.listView);
//
//        deviceList = new ArrayList<>();
//        deviceIds = new ArrayList<>();
//        arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, deviceIds);
//        listView.setAdapter(arrayAdapter);
//
//
//        Button button = findViewById(R.id.bluetooth_connect);
//        button.setTypeface(eagleBook);
//
//        Button button_connect = findViewById(R.id.bluetooth_start_connection);
//        button_connect.setTypeface(eagleBook);
//
//        Button button_send = findViewById(R.id.bluetooth_send);
//        button_send.setTypeface(eagleBook);
//
//        Button button_next = findViewById(R.id.bluetooth_next);
//        button_next.setTypeface(eagleBook);
//
//        final EditText editText = findViewById(R.id.bluetooth_message);
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                enableBluetooth();
//            }
//        });
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                bluetoothAdapter.cancelDiscovery();
//
//                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
//                    deviceList.get(position).createBond();
//                    bluetoothDevice = deviceList.get(position);
//
//                    start();
//                }
//            }
//        });
//
//        button_connect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startConnection();
//            }
//        });
//
//        button_send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                byte [] bytes = editText.getText().toString().getBytes(Charset.defaultCharset());
//                write(bytes);
//            }
//        });
//
//        button_next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(BluetoothActivity.this, Menu.class);
//                startActivity(intent);
//            }
//        });



    }






}
