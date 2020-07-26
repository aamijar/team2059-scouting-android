package com.team2059.scouting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;

public class ConnectionDialog extends DialogFragment {


    private static final String ARG_MSG = "arg_msg";
    private static final String ARG_HANDLER = "arg_handler";


    private static ConnectionDialogCallback dialogCallback;

    private Context context;

    public interface ConnectionDialogCallback{
        void onRetryConnection();
        void onPair();
        void onDelete();
    }


    public static ConnectionDialog newInstance(BluetoothHandler bluetoothHandler){
        ConnectionDialog dialog = new ConnectionDialog();
        Bundle bundle = new Bundle();
        if(bluetoothHandler.getConnectionStatus()){
            bundle.putString(ARG_MSG, "Secure Connection ----> " + bluetoothHandler.getBluetoothDevice().getName());
        }
        else{
            bundle.putString(ARG_MSG, "Broken Connection --X-- " + bluetoothHandler.getBluetoothDevice().getName());
        }
        bundle.putParcelable(ARG_HANDLER, bluetoothHandler);
        dialog.setArguments(bundle);
        return dialog;
    }

    public static ConnectionDialog newInstance(BluetoothDevice device){
        ConnectionDialog dialog = new ConnectionDialog();
        Bundle bundle = new Bundle();
        String msg = "Would you like to pair with ";
        if(device.getName() == null){
            msg += "Unknown?";
        }
        else{
            msg += device.getName() + "?";
        }
        bundle.putString(ARG_MSG, msg);
        dialog.setArguments(bundle);
        return dialog;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog, null);
        View view1 = inflater.inflate(R.layout.listview_subtopic, null);
        final View view2 = inflater.inflate(R.layout.dialog_listview, null);

        final Typeface eagleBook = Typeface.createFromAsset(getActivity().getAssets(), "fonts/eagle_book.otf");
        final Typeface eagleLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/eagle_light.otf");

        builder.setTitle("Connection Status:")
                .setCustomTitle(view1)
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        if(getArguments().getString(ARG_MSG).substring(0, 1).equals("W")){
            builder.setView(view);
            builder.setPositiveButton("Pair", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialogCallback.onPair();
                }
            });
        }
        else if(getArguments().getString(ARG_MSG).substring(0, 1).equals("S")){
            builder.setView(view2);


            ArrayList<String> fileNames = FileManager.getDirs(getActivity());


            final RadioGroup radioGroup = view2.findViewById(R.id.dialog_radiogroup);
            RadioButton radioButton;
            for(String fileName : fileNames){
                radioButton = new RadioButton(getActivity());
                radioButton.setText(fileName.split(",")[0]);
                radioButton.setTypeface(eagleLight);
                radioButton.setButtonDrawable(R.drawable.radio_button_folder);

                float scale = getResources().getDisplayMetrics().density;
                int dpAsPixels = (int) (7*scale + 0.5f);
                radioButton.setPadding(dpAsPixels, dpAsPixels, 0, dpAsPixels);

                radioGroup.addView(radioButton);
            }

            TextView textView1 = view2.findViewById(R.id.dialog_listview_status);
            textView1.setText(getArguments().getString(ARG_MSG));



            builder.setPositiveButton("Delete",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialogCallback.onDelete();
                }
            });
            builder.setNegativeButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int radioButtonId = radioGroup.getCheckedRadioButtonId();
                    RadioButton checkedRadioButton = view2.findViewById(radioButtonId);

                    if(checkedRadioButton == null){
                        Toast.makeText(getActivity(), "No Competition Selected", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        try{
                            //send data to connected device
                            String dirName = checkedRadioButton.getText().toString();

                            String jsonString = FileManager.readFile(dirName + "/Competition.json", context);

                            BluetoothHandler bluetoothHandler = getArguments().getParcelable(ARG_HANDLER);
                            bluetoothHandler.write(dirName +  "," + jsonString);
                        }
                        catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }
                }
            });

        }
        else{
            builder.setView(view);
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialogCallback.onDelete();
                }
            });
            builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialogCallback.onRetryConnection();
                }
            });
        }

        final AlertDialog alertDialog = builder.create();

        TextView textView = view.findViewById(R.id.dialog_status);
        textView.setText(getArguments().getString(ARG_MSG));

        //set button fonts
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button neutralButton = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                neutralButton.setTypeface(eagleBook);
                positiveButton.setTypeface(eagleBook);
                negativeButton.setTypeface(eagleBook);

                neutralButton.setAllCaps(false);
                positiveButton.setAllCaps(false);
                negativeButton.setAllCaps(false);
            }
        });

        return alertDialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        BluetoothFragment fragment = (BluetoothFragment) getActivity().getSupportFragmentManager().findFragmentByTag("BluetoothFragment");
        dialogCallback = fragment;
        this.context = context;

    }

    @Override
    public void onResume() {
        super.onResume();
        //getDialog().getWindow().setLayout(600, 400);
//        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
//        int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
//        getDialog().getWindow().setLayout(width, height);
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

    }
}
