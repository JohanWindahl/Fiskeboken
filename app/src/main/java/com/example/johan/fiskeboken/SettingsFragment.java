package com.example.johan.fiskeboken;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class SettingsFragment extends Fragment {
    private String weightSelection;
    private String lengthSelection;
    private String languageSelection;

    //For saving states
    static final String STATE_OF_LIST = "listState";
    static final String WEIGHT_STATE = "weightState";
    static final String LENGTH_STATE = "heightState";
    static final String LANG_STATE = "langState";

    //Constructors
    public SettingsFragment() {}

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(STATE_OF_LIST, AppSettings.getCatchList());
        savedInstanceState.putSerializable(WEIGHT_STATE, AppSettings.isKg());
        savedInstanceState.putSerializable(LENGTH_STATE, AppSettings.isM());
        savedInstanceState.putSerializable(LANG_STATE, AppSettings.isSwedish());
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

            SettingsFragment settingsFragment = new SettingsFragment();
            getFragmentManager().beginTransaction().replace(R.id.frag, settingsFragment).commit();

            FloatingActionButton floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.floating_action_button);
            floatingActionButton.show();

            TextView mTitle = (TextView) getActivity().findViewById(R.id.title);
            mTitle.setText("Inställningar");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        initWeightSpinner(v);
        initLengthSpinner(v);
        initLangSpinner(v);
        return v;
    }

    //Init Weight-settings-spinner
    private void initWeightSpinner(View v) {
        String [] weight_units = {"Kg", "Pounds"};
        Spinner weight_spinner = (Spinner) v.findViewById(R.id.sett_we_spin);

        ArrayAdapter<String> weAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, weight_units);
        weAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        weight_spinner.setAdapter(weAdapter);

        if (AppSettings.isKg()) {
            weight_spinner.setSelection(weAdapter.getPosition("Kg"));
        }
        else {
            weight_spinner.setSelection(weAdapter.getPosition("Pounds"));
        }
        weight_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equals("Kg"))
                {
                    AppSettings.setKg(true);
                }
                else if(selectedItem.equals("Pounds"))
                {
                    AppSettings.setKg(false);
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }

    //Init Length-settings-spinner
    private void initLengthSpinner(View v) {

        String [] length_units = {"Cm", "Inches"};
        Spinner length_spinner = (Spinner) v.findViewById(R.id.sett_le_spin);
        ArrayAdapter<String> leAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, length_units);
        leAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        length_spinner.setAdapter(leAdapter);
        length_spinner.setSelection(leAdapter.getPosition(lengthSelection));

        if (AppSettings.isM()) {
            length_spinner.setSelection(leAdapter.getPosition("Cm"));
        }
        else {
            length_spinner.setSelection(leAdapter.getPosition("Inches"));
        }

        length_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equals("Cm"))
                {
                    AppSettings.setM(true);
                }
                else if(selectedItem.equals("Inches"))
                {
                    AppSettings.setM(false);
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    //Init Language-settings-spinner
    private void initLangSpinner(View v) {
        String [] language_units = {"Svenska"};
        Spinner language_spinner = (Spinner) v.findViewById(R.id.sett_la_spin);
        ArrayAdapter<String> laAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, language_units);
        laAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        language_spinner.setAdapter(laAdapter);
        language_spinner.setSelection(laAdapter.getPosition(languageSelection));

        if (AppSettings.isSwedish()) {
            language_spinner.setSelection(laAdapter.getPosition("Svenska"));
        }


        language_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equals("Svenska"))
                {
                    AppSettings.setSwedish(true);
                }
                else if(selectedItem.equals("English"))
                {
                    AppSettings.setSwedish(false);
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}
