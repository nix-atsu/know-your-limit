package com.gamesofni.knowyourlimit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;


public class SettingsFragment extends PreferenceFragment {

    // save listener instance so it won't be garbage collected
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    Constants constants;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.pref_vizualizer);

        constants = new Constants(getActivity().getApplicationContext());

        listener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                    Preference pref = findPreference(key);

                    if (pref instanceof EditTextPreference) {
                        if (key.equals(constants.DAILY_LIMIT_KEY) ||
                                key.equals(constants.FIRST_USER_NAME) ||
                                key.equals(constants.SECOND_USER_NAME)) {
                            EditTextPreference currentSetting = (EditTextPreference) pref;
                            pref.setSummary(currentSetting.getText());

                        } else if (key.equals(constants.CURRENT_LIMIT_RESET)) {
                            final float currentLimit = (float) Double.parseDouble(((EditTextPreference) pref).getText());

                            SharedPreferences.Editor prefEditor = getPreferenceScreen().getSharedPreferences().edit();
                            prefEditor.putFloat(constants.CURRENT_LIMIT, currentLimit);

                            final float userLimit = currentLimit/2;
                            prefEditor.putFloat(constants.CURRENT_LIMIT_USER1, userLimit);
                            prefEditor.putFloat(constants.CURRENT_LIMIT_USER2, userLimit);

                            prefEditor.apply();
                        }
                    }
                }
            };
    }

    public void onResume() {
        super.onResume();

        // update summary of preferences
        setSummaryOfEditTextPreference(constants.DAILY_LIMIT_KEY);
        setSummaryOfEditTextPreference(constants.FIRST_USER_NAME);
        setSummaryOfEditTextPreference(constants.SECOND_USER_NAME);


        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(listener);
    }

    private void setSummaryOfEditTextPreference(String preferenceKey) {
        Preference pref = findPreference(preferenceKey);
        EditTextPreference currentSetting = (EditTextPreference) pref;
        pref.setSummary(currentSetting.getText());
    }

    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(listener);
    }

}
