package org.team2059.scouting;


import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.bluetooth.BluetoothClass.Device.*;

public class MyExpandableAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> topics;
    private Map<String, List<BluetoothDevice>> map;
    private ArrayList<BluetoothHandler> bluetoothHandlers;
    private List<Boolean> checkBoxStates;

    private static final int GROUP_TYPE_1 = 0;
    private static final int CHILD_TYPE_1 = 0;
    private static final int CHILD_TYPE_2 = 1;
    private static final int CHILD_TYPE_3 = 2;


    public MyExpandableAdapter(Context context, List<String> topics, Map<String,
            List<BluetoothDevice>> map, ArrayList<BluetoothHandler> bluetoothHandlers, List<Boolean> checkBoxStates){
        this.context = context;
        this.topics = topics;
        this.map = map;
        this.bluetoothHandlers = bluetoothHandlers;
        this.checkBoxStates = checkBoxStates;
    }


    @Override
    public int getGroupCount() {
        return topics.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        //Log.e("TAG", Integer.toString(groupPosition));
        return map.get(topics.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return topics.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        //Log.e("TAG", groupPosition + "|" + childPosition);
        return map.get(topics.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String title = (String) getGroup(groupPosition);
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_topic, null);
        }
        TextView textView = convertView.findViewById(R.id.topic_textview);
        textView.setText(title);
        ImageView imageView = convertView.findViewById(R.id.topic_imageview);
        if(isExpanded){
            imageView.setImageResource(R.drawable.ic_expand_less_black_24dp);
        }
        else{
            imageView.setImageResource(R.drawable.ic_expand_more_black_24dp);
        }


        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        BluetoothDevice device = (BluetoothDevice) getChild(groupPosition, childPosition);
        String title = device.getName();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        int childType = getChildType(groupPosition, childPosition);

        if(convertView == null || convertView.getTag().equals(childType)){
            switch (childType){
                case CHILD_TYPE_1:
                    convertView = inflater.inflate(R.layout.listview_subtopic_formatted, null);
                    convertView.setTag(childType);
                    break;
                case CHILD_TYPE_2:
                    convertView = inflater.inflate(R.layout.listview_subtopic_paired, null);
                    convertView.setTag(childType);
                    break;
                case CHILD_TYPE_3:
                    convertView = inflater.inflate(R.layout.listview_subtopic_connected, null);
                    convertView.setTag(childType);
                    break;
                default:
                    break;
            }
        }

        switch (childType){
            case CHILD_TYPE_1:
                TextView textView = convertView.findViewById(R.id.subtopic_formatted_textview);
                if(title != null){
                    textView.setText(title);
                }
                else{
                    textView.setText("Unknown, Pair to See Device Name");
                }
                ImageView imageView = convertView.findViewById(R.id.subtopic_formatted_imageview);
                setBluetoothIcon(imageView, device);

                CheckBox checkBox = convertView.findViewById(R.id.checkBox);
                checkBox.setChecked(checkBoxStates.get(childPosition));

                break;
            case CHILD_TYPE_2:
                TextView textView1 = convertView.findViewById(R.id.subtopic_paired_textview);
                if(title != null){
                    textView1.setText(title);
                }
                else{
                    textView1.setText("Unknown, Pair to See Device Name");
                }
                ImageView imageView1 = convertView.findViewById(R.id.subtopic_paired_imageview);
                setBluetoothIcon(imageView1, device);
                break;
            case CHILD_TYPE_3:
                TextView textView2 = convertView.findViewById(R.id.subtopic_connected_textview);
                if(title != null){
                    textView2.setText(title);
                }
                else{
                    textView2.setText("Unknown, Pair to See Device Name");
                }
                ImageView imageView2 = convertView.findViewById(R.id.subtopic_connected_imageviewdevice);
                setBluetoothIcon(imageView2, device);

                ImageView imageView3 = convertView.findViewById(R.id.subtopic_connected_imageview_shimmer);


                boolean connectionStatus = bluetoothHandlers.get(childPosition).getConnectionStatus();

                if(connectionStatus){
                    imageView3.setColorFilter(context.getResources().getColor(R.color.green_indicator));
                }
                else{
                    imageView3.setColorFilter(context.getResources().getColor(R.color.red_indicator));
                }
                break;
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }



    @Override
    public int getChildType(int groupPosition, int childPosition) {
        if(groupPosition == 0){
            return CHILD_TYPE_1;
        }
        else if(groupPosition == 1){
            return CHILD_TYPE_2;
        }
        else{
            return CHILD_TYPE_3;
        }
    }

    @Override
    public int getGroupType(int groupPosition) {
        return GROUP_TYPE_1;
    }


    @Override
    public int getChildTypeCount() {
        return 3; // three child types are defined, one with checkbox,
        // one without, and one with connection light
    }


    @Override
    public int getGroupTypeCount() {
        return 1; //only one group type is defined
    }


    public void setBluetoothIcon(ImageView imageView, BluetoothDevice device){
        switch(device.getBluetoothClass().getDeviceClass()){
            case PHONE_SMART:
                imageView.setBackgroundResource(R.drawable.ic_smartphone_black_24dp);
                break;
            case COMPUTER_LAPTOP:
            case COMPUTER_DESKTOP:
                imageView.setBackgroundResource(R.drawable.ic_computer_black_24dp);
                break;
            case AUDIO_VIDEO_HEADPHONES:
            case AUDIO_VIDEO_WEARABLE_HEADSET:
                imageView.setBackgroundResource(R.drawable.ic_headset_black_24dp);
                break;
            case WEARABLE_WRIST_WATCH:
                imageView.setBackgroundResource(R.drawable.ic_watch_black_24dp);
                break;
            default:
                imageView.setBackgroundResource(R.drawable.ic_bluetooth_black);
        }
    }

}
