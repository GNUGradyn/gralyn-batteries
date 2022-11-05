package com.gradyn.gralynbatteries.Configuration;

import android.content.SharedPreferences;

public class ConfigurationHelper {
    /**
     * Generates a Configuration object from a SharedPreferences
     * @param preferences SharedPreferences containing config
     * @return Configuration
     */
    public static Configuration LoadConfiguration(SharedPreferences preferences) {
        return new Configuration(
                preferences.getBoolean(ConfigKeys.BatteryReporting.name(), false),
                preferences.getString(ConfigKeys.ApiRoot.name(), "https://gralyn.app/api"),
                preferences.getString(ConfigKeys.AccessCode.name(), "")
        );
    }

    /**
     * Writes a Configuration to a SharedPreferences
     * @param configuration The configuration to save
     * @param preferences The SharedPreferences to save the configuration to
     */
    public static void SaveConfiguration(Configuration configuration, SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(ConfigKeys.BatteryReporting.name(), configuration.BatteryReporting);
        editor.putString(ConfigKeys.ApiRoot.name(), configuration.ApiRoot);
        editor.putString(ConfigKeys.AccessCode.name(), configuration.AccessCode);
        editor.apply();
    }
}


