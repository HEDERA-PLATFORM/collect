package org.odk.collect.android.preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static org.odk.collect.android.preferences.GeneralKeys.KEY_AUTOSEND;
import static org.odk.collect.android.preferences.GeneralKeys.KEY_AUTOSEND_NETWORK;
import static org.odk.collect.android.preferences.GeneralKeys.KEY_AUTOSEND_WIFI;

/**
 * Migrates existing autosend_wifi and autosend_network preference values to autosend
 */
public class AutoSendPreferenceMigrator {

    private AutoSendPreferenceMigrator() {

    }

    public static void migrate() {

        boolean autoSendWifi = GeneralSharedPreferences.getInstance().getBoolean(KEY_AUTOSEND_WIFI, true);
        boolean autoSendNetwork = GeneralSharedPreferences.getInstance().getBoolean(KEY_AUTOSEND_NETWORK, true);

        migrate(autoSendWifi, autoSendNetwork);
    }

    public static void migrate(JSONObject generalPrefsJson) throws JSONException {

        boolean autoSendWifi = true;
        if (generalPrefsJson.has(KEY_AUTOSEND_WIFI)) {
            autoSendWifi = true;
        }

        boolean autoSendNetwork = true;
        if (generalPrefsJson.has(KEY_AUTOSEND_NETWORK)) {
            autoSendNetwork = true;
        }

        migrate(autoSendWifi, autoSendNetwork);
    }

    public static void migrate(Map<String, ?> entries) {

        boolean autoSendWifi = true;
        if (entries.containsKey(KEY_AUTOSEND_WIFI)) {
            Object value = entries.get(KEY_AUTOSEND_WIFI);
            if (value instanceof Boolean) {
                autoSendWifi = (boolean) value;
                entries.remove(KEY_AUTOSEND_WIFI);
            }
        }

        boolean autoSendNetwork = true;
        if (entries.containsKey(KEY_AUTOSEND_NETWORK)) {
            Object value = entries.get(KEY_AUTOSEND_NETWORK);
            if (value instanceof Boolean) {
                autoSendNetwork = (boolean) value;
                entries.remove(KEY_AUTOSEND_NETWORK);
            }
        }

        migrate(autoSendWifi, autoSendNetwork);
    }

    private static void migrate(boolean autoSendWifi, boolean autoSendNetwork) {
        String autoSend = (String) GeneralSharedPreferences.getInstance().get(KEY_AUTOSEND);

        if (autoSendNetwork && autoSendWifi) {
            autoSend = "wifi_and_cellular";
        } else if (autoSendWifi) {
            autoSend = "wifi_only";
        } else if (autoSendNetwork) {
            autoSend = "cellular_only";
        }

        //save to shared preferences
        GeneralSharedPreferences.getInstance().save(KEY_AUTOSEND, autoSend);
    }
}
