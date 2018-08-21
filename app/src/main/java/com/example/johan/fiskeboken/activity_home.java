package com.example.johan.fiskeboken;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import org.joda.time.DateTime;
import java.io.IOException;
import java.util.ArrayList;

public class activity_home extends AppCompatActivity {
    private LogFragment logFragment;
    private MapFragment mapFragment;
    private SettingsFragment settingsFragment;
    private AddNewFragment addNewFragment;
    private static TextView mTitle;
    private static FloatingActionButton floatingActionButton;

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            displaySelectedScreen(item.getItemId());
            return true;
        }
    };


    //Select which icon selected in bottom navigation
    private void displaySelectedScreen(int id) {
        mTitle = (TextView) findViewById(R.id.title);
        switch (id) {
            case R.id.log:
                loadFragLog();
                break;
            case R.id.map:

                loadFragMap();
                break;
            case R.id.settings:
                loadFragSettings();
                break;
        }
    }

    //Load settings fragment
    void loadFragSettings() {
        mTitle.setText("Inställningar");
        getSupportFragmentManager().beginTransaction().replace(R.id.frag, settingsFragment).commit();
        floatingActionButton.show();
        AppSettings.setMapPickerMode(false);

    }

    //Load map fragment
    void loadFragMap() {
        mTitle.setText("Karta");
        getSupportFragmentManager().beginTransaction().replace(R.id.frag, mapFragment).commit();
        floatingActionButton.show();
        AppSettings.setMapPickerMode(false);

    }

    //Load main fragment
    void loadFragLog() {
        mTitle.setText("Fiskeloggen");
        getSupportFragmentManager().beginTransaction().replace(R.id.frag, logFragment).commit();
        floatingActionButton.show();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Set App settings
        AppSettings.setAllCatchesList(loadData());
        AppSettings.setKg(true);
        AppSettings.setSwedish(true);
        AppSettings.setM(true);
        AppSettings.setMapPickerMode(false);
        AppSettings.setTempCatch(new Catch());

        //Make fragments
        settingsFragment = new SettingsFragment();
        logFragment = new LogFragment();
        mapFragment = new MapFragment();
        addNewFragment = new AddNewFragment();

        //Floating action button click listener
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
                navigation.setSelectedItemId(R.id.log);

                getSupportFragmentManager().beginTransaction().replace(R.id.frag, addNewFragment).commit();
                mTitle.setText("Lägg till fisk");
                floatingActionButton.hide();
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.log);
    }

    //Loads catchlist data from phones shared preferences
    private ArrayList<Catch> loadData() {
        ArrayList<Catch> returnMe = new ArrayList<Catch>();
        SharedPreferences prefs = getSharedPreferences("asdf", Context.MODE_PRIVATE);

        try {
            returnMe = (ArrayList<Catch>) ObjectSerializer.deserialize(prefs.getString("clist", ObjectSerializer.serialize(new ArrayList<Catch>())));

            AppSettings.setAllCatchesList(returnMe);;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (returnMe==null | returnMe.size()==0) { //If empty list, add 2 mock catches
            DateTime mockDate1 = new DateTime(2011, 07, 11, 0, 0, 0, 0);
            DateTime mockDate2 = new DateTime(2018, 04, 21, 0, 0, 0, 0);
            returnMe.add(new Catch("TestGädda", "kall sommardag", mockDate1, 10.0, 20.1, 59.756678, 17.614452));
            returnMe.add(new Catch("TestAbborre", "varm sommardag", mockDate2, 2.0, 20.2, 59.744618, 17.614412));
        }
        return returnMe;
    }

    //Handles back button
    @Override
    public void onBackPressed() {
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        int stackCount = getFragmentManager().getBackStackEntryCount();

        if (stackCount == 1) {
            super.onBackPressed(); // if you don't have any fragments in your backstack yet.
        }
        else if (navigation.getSelectedItemId() == R.id.log) {
            activity_home.this.moveTaskToBack(true);
        }
        else {
            loadFragLog();
            navigation.setSelectedItemId(R.id.log);
        }
    }
}



