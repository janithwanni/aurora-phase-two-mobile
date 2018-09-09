package lk.ac.sjp.aurora.phasetwo;


import android.util.Log;

import java.util.ArrayList;

public class StateManager {
    private static String teamName;
    private static ArrayList<String> scannedList;
    private static String TAG = "AuroraStateManager";

    public static String getRegisteredTeam() {
        return teamName;
    }

    public static void setRegisteredTeam(String InteamName) {
        Log.i(TAG, "Setting registered team to " + teamName);
        teamName = InteamName;
    }

    public static boolean isTeamRegistered() {
        Log.i(TAG, "Checking if" + teamName + " team is registered and its " + (teamName != null));
        return teamName != null;
    }

    public static String getLastScannedPost() {
        return scannedList.get(scannedList.size());
    }

    public static void addScannedPost(String scannedPost) {
        scannedList.add(scannedPost);
    }

    public static boolean allScanned() {
        return true;
        /*Log.i(TAG,"Checking if all posts are scanned");
        if(scannedList == null){
            return false;
        }else{
            return scannedList.size() == 9;
        }*/

    }
}
