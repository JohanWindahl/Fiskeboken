package com.example.johan.fiskeboken;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;


//This class represents all the different settings the app has
public class AppSettings {
    private static ArrayList<Catch> allCatchesList;
    private static boolean isKg;
    private static boolean isM;
    private static boolean isSwedish;
    private static boolean mapPickerMode;
    private static Catch tempCatch;
    private static double lbsMultipel = 0.45359237;
    private static double incMultipel = 0.393700787;

    public static Catch getTempCatch() {return tempCatch;}
    public static void setTempCatch(Catch tempCatch) {AppSettings.tempCatch = tempCatch;}

    public static boolean isMapPickerMode() {return mapPickerMode;}
    public static void setMapPickerMode(boolean mapPickerMode) {AppSettings.mapPickerMode = mapPickerMode;}

    public static ArrayList<Catch> getCatchList() {return allCatchesList;}
    public static void setAllCatchesList(ArrayList<Catch> allCatchesList) {AppSettings.allCatchesList = allCatchesList;}

    public static void removeItem(int layoutPosition) {allCatchesList.remove(layoutPosition);}

    public static boolean isKg() {return isKg;}
    public static void setKg(boolean kg) {isKg = kg;}

    public static boolean isM() {return isM;}
    public static void setM(boolean m) {isM = m;}

    public static boolean isSwedish() {return isSwedish;}
    public static void setSwedish(boolean swedish) {isSwedish = swedish;}

    public static double getLbsMultipel() {return lbsMultipel;}
    public static Double getInchesMultipel() {return incMultipel;}

    public static void addItem(Catch addMe) {allCatchesList.add(addMe);}
}
