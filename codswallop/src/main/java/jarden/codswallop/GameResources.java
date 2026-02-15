package jarden.codswallop;

import android.content.SharedPreferences;
import android.net.wifi.WifiManager;

/**
 * Created by john.denny@gmail.com on 15/02/2026.
 */
public class GameResources {
    private WifiManager wifiManager;
    private String waitingForAnswers;
    private String getWaitingForVotes;
    private SharedPreferences sharedPreferences;

    public GameResources(WifiManager wifiManager, String waitingForAnswers,
                         String getWaitingForVotes, SharedPreferences sharedPreferences) {
        this.wifiManager = wifiManager;
        this.waitingForAnswers = waitingForAnswers;
        this.getWaitingForVotes = getWaitingForVotes;
        this.sharedPreferences = sharedPreferences;
    }

    public String getWaitingForAnswers() {
        return waitingForAnswers;
    }
    public void setWaitingForAnswers(String waitingForAnswers) {
        this.waitingForAnswers = waitingForAnswers;
    }
    public String getGetWaitingForVotes() {
        return getWaitingForVotes;
    }
    public void setGetWaitingForVotes(String getWaitingForVotes) {
        this.getWaitingForVotes = getWaitingForVotes;
    }
    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }
    public WifiManager getWifiManager() {
        return wifiManager;
    }
    public void setWifiManager(WifiManager wifiManager) {
        this.wifiManager = wifiManager;
    }
}
