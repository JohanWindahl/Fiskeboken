package com.example.johan.fiskeboken;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import org.joda.time.DateTime;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;
import static java.lang.Double.parseDouble;

public class AddNewFragment extends Fragment {

    //UI elements
    private TextInputLayout mTILfishType;
    private TextInputLayout mTILdesc;
    private DatePicker mTILdate;
    private TextInputLayout mTILweight;
    private TextInputLayout mTILheight;
    private TextView latLngTxt;
    private Button addFish;

    //For saving states
    static final String STATE_OF_LIST = "listState";
    static final String WEIGHT_STATE = "weightState";
    static final String LENGTH_STATE = "heightState";
    static final String LANG_STATE = "langState";
    static final String TEMP_CATCH_STATE = "tempCState";

    //Constructors
    public AddNewFragment() {}

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        saveVariablesToTempCatch();
        savedInstanceState.putSerializable(STATE_OF_LIST, AppSettings.getCatchList());
        savedInstanceState.putSerializable(WEIGHT_STATE, AppSettings.isKg());
        savedInstanceState.putSerializable(LENGTH_STATE, AppSettings.isM());
        savedInstanceState.putSerializable(LANG_STATE, AppSettings.isSwedish());
        savedInstanceState.putSerializable(TEMP_CATCH_STATE, AppSettings.getTempCatch());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            AppSettings.setAllCatchesList((ArrayList<Catch>) savedInstanceState.getSerializable(STATE_OF_LIST));
            AppSettings.setKg((Boolean) savedInstanceState.getSerializable(WEIGHT_STATE));
            AppSettings.setM((Boolean) savedInstanceState.getSerializable(LENGTH_STATE));
            AppSettings.setSwedish((Boolean) savedInstanceState.getSerializable(LANG_STATE));
            AppSettings.setTempCatch((Catch) savedInstanceState.getSerializable(TEMP_CATCH_STATE));


            AddNewFragment addNewFragment = new AddNewFragment();
            getFragmentManager().beginTransaction().replace(R.id.frag, addNewFragment).commit();

            FloatingActionButton floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.floating_action_button);
            floatingActionButton.hide();

            TextView mTitle = (TextView) getActivity().findViewById(R.id.title);
            mTitle.setText("Lägg till fisk");
        }

        mTILfishType = (TextInputLayout) Objects.requireNonNull(getActivity()).findViewById(R.id.fishType_inpt);
        mTILdesc = (TextInputLayout) Objects.requireNonNull(getActivity()).findViewById(R.id.description_inpt);
        mTILdate = (DatePicker) getActivity().findViewById(R.id.date_inpt);
        mTILweight = (TextInputLayout) Objects.requireNonNull(getActivity()).findViewById(R.id.weight_inpt);
        mTILheight = (TextInputLayout) Objects.requireNonNull(getActivity()).findViewById(R.id.height_inpt);
        latLngTxt = (TextView) getActivity().findViewById(R.id.latTxt);



        try {mTILfishType.getEditText().setText(AppSettings.getTempCatch().getFishType());}
        catch (NullPointerException n) {}

        try {mTILdesc.getEditText().setText(AppSettings.getTempCatch().getDescription());}
        catch (NullPointerException n) {}

        try {mTILweight.getEditText().setText(AppSettings.getTempCatch().getWeight().toString());}
        catch (NullPointerException n) {}

        try {mTILheight.getEditText().setText(AppSettings.getTempCatch().getHeight().toString());}
        catch (NullPointerException n) {}

        try {mTILdate.updateDate(AppSettings.getTempCatch().getDate().getYear(),AppSettings.getTempCatch().getDate().getMonthOfYear(),AppSettings.getTempCatch().getDate().getDayOfMonth()); }
        catch (NullPointerException n) {}

        try {
            String lat = new DecimalFormat("#.##").format(AppSettings.getTempCatch().getGpsLat());
            String lng = new DecimalFormat("#.##").format(AppSettings.getTempCatch().getGpsLong());
            latLngTxt.setText("lat/lng: (" + lat + " / " + lng + ")");

        }
        catch (NullPointerException e) {
            latLngTxt.setText("Inga koordinater hittade");
        }
        catch (IllegalArgumentException e) {
            latLngTxt.setText("Inga koordinater hittade");
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_new, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        AppSettings.setMapPickerMode(true);
        addFish = (Button) getActivity().findViewById(R.id.addBtn);
        Button gpsBtn = getActivity().findViewById(R.id.gpsBtn);
        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveVariablesToTempCatch();
                loadLatLngPicker();
            }
        });

        addFish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTILfishType.setErrorEnabled(true);
                saveVariablesToTempCatch();

                if (AppSettings.getTempCatch().getFishType().matches("")) {
                    Toast.makeText(getContext(), "Du måste skriva in vilken sorts fisk", 2).show();
                }
                else {
                    AppSettings.addItem(new Catch(AppSettings.getTempCatch().getFishType(),AppSettings.getTempCatch().getDescription(), AppSettings.getTempCatch().getDate(), AppSettings.getTempCatch().getWeight(),AppSettings.getTempCatch().getHeight(),AppSettings.getTempCatch().getGpsLat(),AppSettings.getTempCatch().getGpsLong()));
                    AppSettings.setTempCatch(new Catch());
                    AppSettings.setMapPickerMode(false);
                    loadLogFrag();
                    saveData();
                }
            }
        });
    }

    //Saves data to shared preferences
    private void saveData() {
        SharedPreferences prefs = this.getActivity().getSharedPreferences("asdf", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        try {
            editor.putString("clist", ObjectSerializer.serialize(AppSettings.getCatchList()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
    }

    //Saves all UI-inputs to temporary catch, for state restoring purposes
    private void saveVariablesToTempCatch() {
        try {
            AppSettings.getTempCatch().setFishType(mTILfishType.getEditText().getText().toString());
        }
        catch(NullPointerException e)
        {AppSettings.getTempCatch().setFishType("");}

        try {
            AppSettings.getTempCatch().setDescription(mTILdesc.getEditText().getText().toString());
        }
        catch(NullPointerException e) {
            AppSettings.getTempCatch().setDescription("");
        }

        try {
            AppSettings.getTempCatch().setWeight(parseDouble(mTILweight.getEditText().getText().toString()));
        }
        catch (NumberFormatException e) {
            AppSettings.getTempCatch().setWeight(0.0);
        }

        try {
            AppSettings.getTempCatch().setHeight(parseDouble(mTILheight.getEditText().getText().toString()));
        }
        catch (NumberFormatException e) {
            AppSettings.getTempCatch().setHeight(0.0);
        }

        DateTime currentDate = new DateTime(mTILdate.getYear(),mTILdate.getMonth(),mTILdate.getDayOfMonth(),0,0,0,0);
        AppSettings.getTempCatch().setDate(currentDate);

        System.out.println(AppSettings.getTempCatch());
    }

    //Loads the main fragment
    private void loadLogFrag() {
        LogFragment logFragment;
        logFragment = new LogFragment();
        getFragmentManager().beginTransaction().replace(R.id.frag, logFragment).commit();
        FloatingActionButton floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.floating_action_button);
        floatingActionButton.show();
        TextView mTitle = (TextView) getActivity().findViewById(R.id.title);
        mTitle.setText("Fiskeloggen");
    }

    //Loads the map fragment in picker mode
    private void loadLatLngPicker() {
        AppSettings.setMapPickerMode(true);
        MapFragment mapFragment = new MapFragment();

        getFragmentManager().beginTransaction().replace(R.id.frag, mapFragment).commit();

        FloatingActionButton floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.floating_action_button);
        floatingActionButton.hide();

        TextView mTitle = (TextView) getActivity().findViewById(R.id.title);
        mTitle.setText("Välj Position");

    }
}
