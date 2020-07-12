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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class ConnectionDialog extends DialogFragment {


    private static final String ARG_MSG = "arg_msg";
    private static final String ARG_HANDLER = "arg_handler";


    private static ConnectionDialogCallback dialogCallback;

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
            msg += "Unknown @ " + device.getAddress() + "?";
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
        builder.setView(view)
                .setTitle("Connection Status:")
                .setCustomTitle(view1)
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        if(getArguments().getString(ARG_MSG).substring(0, 1).equals("W")){
            builder.setPositiveButton("Pair", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialogCallback.onPair();
                }
            });
        }
        else if(getArguments().getString(ARG_MSG).substring(0, 1).equals("S")){
            builder.setPositiveButton("Delete",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialogCallback.onDelete();
                }
            });

        }
        else{
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
        final Typeface eagleBook = Typeface.createFromAsset(getActivity().getAssets(), "fonts/eagle_book.otf");
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

//                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.
//                        getDrawable(getActivity(), R.drawable.ic_info_black), null);

            }
        });

        return alertDialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        BluetoothFragment fragment = (BluetoothFragment) getActivity().getSupportFragmentManager().findFragmentByTag("BluetoothFragment");
        dialogCallback = fragment;
    }


}
