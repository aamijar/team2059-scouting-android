package com.team2059.scouting;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;


class BluetoothHandler implements Parcelable {

    private final UUID MY_UUID = UUID.fromString("ad9c9615-f9fc-4bf8-8314-2894e4568ad7");
    private String TAG = "BluetoothProtocol";

    private Activity activity;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;

    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;


    private boolean connectionStatus = false;
    private boolean initAttemptDone = false;
    private boolean delete = false;

    private BluetoothHandlerCallback callback;

    protected BluetoothHandler(Parcel in) {
        TAG = in.readString();
        bluetoothDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
    }

    public static final Creator<BluetoothHandler> CREATOR = new Creator<BluetoothHandler>() {
        @Override
        public BluetoothHandler createFromParcel(Parcel in) {
            return new BluetoothHandler(in);
        }

        @Override
        public BluetoothHandler[] newArray(int size) {
            return new BluetoothHandler[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(TAG);
        dest.writeParcelable(bluetoothDevice, flags);
    }

    public interface BluetoothHandlerCallback{
        void onBluetoothHandlerCallback(BluetoothHandler bluetoothHandler);
        void onConnectionCallback();
    }



    BluetoothHandler(Activity activity, BluetoothHandlerCallback callback){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.activity = activity;
        this.callback = callback;
    }


    private class AcceptThread extends Thread {

        private BluetoothServerSocket serverSocket;


        AcceptThread(){
            BluetoothServerSocket tmp = null;

            try{
                String APP_NAME = "frc2059 Scouting";
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
                    bluetoothDevice = socket.getRemoteDevice();
                    connected(socket);
                    Log.e(TAG, "CONNECTION ACCEPTED");
                    writeMessage(bluetoothDevice.getName() + " has established a bluetooth connection your device");
                    cancel();
                    connectionStatus = true;
                    callback.onBluetoothHandlerCallback(BluetoothHandler.this);
                    callback.onConnectionCallback();
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

        //private final BluetoothDevice mmDevice;

        ConnectThread(BluetoothDevice device){
            bluetoothDevice = device;
            BluetoothSocket tmp = null;

            try{
                tmp = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);

            } catch (IOException e) {
                Log.e("Blue", "Socket's create method failed");
            }
            mmSocket = tmp;
        }

        public void run(){
            bluetoothAdapter.cancelDiscovery();

            Log.e("Blue", Boolean.toString(mmSocket.isConnected()));
            try {
                mmSocket.connect();

            } catch (IOException e) {
                try{
                    mmSocket.close();
                } catch (IOException ex) {
                    Log.e("Blue", "Could not close the client socket", ex);
                    //progressBar.setVisibility(View.INVISIBLE);
                }
                Log.e("Blue", "before stacktrace");
                e.printStackTrace();
                Log.e("Blue", "Could not connect to UUID" + MY_UUID);
                writeMessage("Could not connect to " + bluetoothDevice.getName());
                //progressBar.setVisibility(View.INVISIBLE);
                callback.onBluetoothHandlerCallback(BluetoothHandler.this);
                return;
            }

            Log.e(TAG, "CLIENT CONNECTION SUCCEEDED");
            connected(mmSocket);
            writeMessage("Successfully connected to " + bluetoothDevice.getName());
            connectionStatus = true;
            callback.onBluetoothHandlerCallback(BluetoothHandler.this);
            callback.onConnectionCallback();
            //TODO connection succeeded, attempt work in separate thread

        }

        void cancel(){
            try{
                mmSocket.close();
            } catch (IOException e) {
                Log.e("Blue", "Could not close the client socket", e);
            }
        }

    }

    synchronized void start(){
        if(connectThread != null){
            connectThread.cancel();
            connectThread = null;
        }
        if(acceptThread == null){
            acceptThread = new AcceptThread();
            acceptThread.start();
        }
    }

    void startClient(BluetoothDevice device){
        Log.e(TAG, "started Client");

        if(acceptThread != null){
            acceptThread.cancel();
        }
        if(connectThread != null){
            connectThread.cancel();
            connectThread = null;
        }
        connectThread = new ConnectThread(device);
        connectThread.start();
    }

    public class ConnectedThread extends Thread{

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        private String completeMessage = null;
        private int dataSize = 0;

        ConnectedThread(BluetoothSocket socket){

            Log.e("Blue", "Connected Thread : Starting");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

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
            byte[] mmBuffer = new byte[1024];

            int bytes; // bytes returned from stream

            try {
                Log.e(TAG, Integer.toString(mmInStream.available()));
            }catch (IOException e){
                e.printStackTrace();
            }

            while(true){
                try{
                    bytes = mmInStream.read(mmBuffer);
                    //bytes = mmInStream.read();
                    final String incomingMessage = new String(mmBuffer, 0, bytes);

                    if(dataSize == 0){
//                        if(completeMessage != null){
//                            Log.e("FINAL MESSAGE", completeMessage);
//                        }
                        dataSize = Integer.parseInt(incomingMessage.split(",", 3)[0]);
                        Log.e("Initial Size", "" + dataSize);
                        int digits = incomingMessage.split(",", 3)[0].length();
                        //subtract current bytes from this packet and exclude header digits and comma
                        dataSize -= bytes - digits - 1;
                        completeMessage = incomingMessage.substring(digits + 1);
                        Log.e("MESSAGE", incomingMessage);
                        Log.e("Modisize", "" + dataSize);

                    }
                    else{
                        dataSize -= bytes;
                        completeMessage += incomingMessage;
                        Log.e("MESSAGE", incomingMessage);
                        Log.e("Modisize", "" + dataSize);
                        if(dataSize == 0){
                            Log.e("FINAL MESSAGE", completeMessage.substring(completeMessage.length() - 10));
                            FileManager.writeFromBluetoothResponse(completeMessage, bluetoothDevice, activity);
                        }
                    }


                    //Log.e("MESSAGE", Integer.toString(bytes));
                    //Log.e("MESSAGE", Integer.toString(incomingMessage.length()));
                    //Log.e("MESSAGE", "Input Stream: " + incomingMessage);
                    //writeMessage(incomingMessage);

                    //writeMessage("received data " + incomingMessage.split(",",3)[0]);




                } catch (IOException e) {
                    Log.e("Blue", "Unable to read from input stream", e);
                    cancel(); //TODO confirm socket closes
                    connectionStatus = false;
                    callback.onBluetoothHandlerCallback(BluetoothHandler.this);
                    break;
                }
            }
        }

        void write(byte[] bytes){
            String text = new String(bytes, Charset.defaultCharset());
            Log.e("Blue", "writing to output stream: " + text);

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

    private void connected(BluetoothSocket socket){

        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
    }

    void write(String input){
        byte [] bytes = input.getBytes(Charset.defaultCharset());
        Log.e("write", Integer.toString(bytes.length));
        int size = bytes.length;
        input = size + "," + input;

        bytes = input.getBytes(Charset.defaultCharset());
        Log.e("Blue", "write called");
        connectedThread.write(bytes);
    }



    private void writeMessage(final String msg){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    AcceptThread getAcceptThread(){
        return acceptThread;
    }

    ConnectThread getConnectThread(){
        return connectThread;
    }

    ConnectedThread getConnectedThread(){
        return connectedThread;
    }

    public boolean getConnectionStatus(){
        return connectionStatus;
    }

    public BluetoothDevice getBluetoothDevice(){
        return bluetoothDevice;
    }

    public boolean getInitAttemptDone(){
        return initAttemptDone;
    }
    public void setInitAttemptDone(){
        initAttemptDone = true;
    }

    public void setDelete(){
        delete = true;
    }
    public boolean getDelete(){
        return delete;
    }


}
