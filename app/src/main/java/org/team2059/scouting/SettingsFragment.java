package org.team2059.scouting;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.BuildCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;


public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        ((NavigationDrawer) getActivity()).showBackButton(true);

        findPreference("theme").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.e("yes", newValue.toString());

                if(newValue.toString().equals("Dark")){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                else if(newValue.toString().equals("Light")){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                else if(BuildCompat.isAtLeastQ()){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                }
                //((NavigationDrawer) getActivity()).showBackButton(true);
                return true;
            }
        });

        final EditTextPreference editTextPreference = findPreference("my_bluetooth_device_nickname");
        editTextPreference.setSummaryProvider(new Preference.SummaryProvider() {
            @Override
            public CharSequence provideSummary(Preference preference) {
                if(BluetoothAdapter.getDefaultAdapter() != null){
                    return BluetoothAdapter.getDefaultAdapter().getName();
                }
                return "Not Available";
            }
        });
        editTextPreference.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                if(BluetoothAdapter.getDefaultAdapter() != null){
                    editText.setText(BluetoothAdapter.getDefaultAdapter().getName());
                }

            }
        });

        if(BluetoothAdapter.getDefaultAdapter() != null){
            editTextPreference.setDefaultValue(BluetoothAdapter.getDefaultAdapter().getName());

            editTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                    Log.e("Settings", bluetoothAdapter.getName());

                    if(bluetoothAdapter.isEnabled()){
                        Log.e("Settings", "enabled");
                        bluetoothAdapter.setName(newValue.toString());
                    }
                    return true;
                }
            });
        }




    }

}
