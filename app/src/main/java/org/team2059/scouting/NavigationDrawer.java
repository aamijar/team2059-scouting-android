package org.team2059.scouting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

public class NavigationDrawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        MenuFragment.MenuFragmentListener, OpenFragment.OpenFragmentListener {


    private DrawerLayout drawer;
    private Fragment fragment;
    private Toolbar toolbar;


    private ActionBarDrawerToggle toggle;
    private boolean navIsRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_navigation_drawer);
        Log.e("RECREATED!", "NAVDRAWER");
        //Dark Theme Test

        //hides status bar
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);



        toolbar = findViewById(R.id.navigation_toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.navigation_drawer);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);


        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new MenuFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_new);
        }
        else{

            navIsRegistered = savedInstanceState.getBoolean("navIsRegistered");

            if(navIsRegistered){
                navIsRegistered = false;
                showBackButton(true);
            }

        }

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {}

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {}

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if(fragment != null){

                    if(fragment instanceof TabFragment){
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                        ft.setCustomAnimations(R.anim.fade_in_fast, R.anim.fade_out);
                        ft.addToBackStack(null);
                        ft.replace(R.id.fragment_container, fragment, "tab").commit();
                        fragment = null;
                    }
                    else if(fragment instanceof BluetoothFragment){
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                        ft.setCustomAnimations(R.anim.fade_in_fast, R.anim.fade_out);
                        ft.addToBackStack(null);
                        ft.replace(R.id.fragment_container, fragment, "BluetoothFragment").commit();
                        fragment = null;
                    }
                    else if(fragment instanceof SettingsFragment || fragment instanceof AboutFragment){
                        Log.e("", "Settings transition");
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                        ft.addToBackStack(null);
                        ft.replace(R.id.fragment_container, fragment).commit();
                        fragment = null;
                    }
                    else{
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                        ft.setCustomAnimations(R.anim.fade_in_fast, R.anim.fade_out);
                        ft.addToBackStack(null);
                        ft.replace(R.id.fragment_container, fragment).commit();
                        fragment = null;
                    }
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {}

        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){

            case R.id.nav_new:
                fragment = new MenuFragment();
                break;
            case R.id.nav_open:
                fragment = new OpenFragment();
                break;
            case R.id.nav_bluetooth:

                BluetoothFragment bluetoothFragment = (BluetoothFragment) getSupportFragmentManager().findFragmentByTag("BluetoothFragment");
                if(bluetoothFragment != null){
                    fragment = bluetoothFragment;
                }
                else{
                    fragment = new BluetoothFragment();
                }
                break;
            case R.id.nav_about:
                fragment = new AboutFragment();
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed(){
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
        if(navIsRegistered){
            showBackButton(false);
        }
    }

    @Override
    public void onInputSend(String dirName) {

        TabFragment tabFragment = TabFragment.newInstance(dirName);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.replace(R.id.fragment_container, tabFragment).commit();
    }

    @Override
    public void onInputOpenSend(String dirName) {

        TabFragment tabFragment = TabFragment.newInstance(dirName);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.replace(R.id.fragment_container, tabFragment).commit();
    }



    public void showBackButton(final boolean enable){
        if(drawer == null){
            return;
        }

        if(enable) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            if(!navIsRegistered) {
                toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
                navIsRegistered = true;
            }

        } else {

            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            toggle.setDrawerIndicatorEnabled(true);
            toggle.setToolbarNavigationClickListener(null);
            navIsRegistered = false;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("navIsRegistered", navIsRegistered);
    }
}
