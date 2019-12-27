package com.cybavo.example.auth.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.cybavo.auth.service.auth.Authenticator;
import com.cybavo.example.auth.MainViewModel;
import com.cybavo.example.auth.R;
import com.cybavo.example.auth.config.Config;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final static String KEY_SDK_VER = "pref_sdk_version";
    private final static String KEY_API_CODE = "pref_api_code";
    private final static String KEY_PAIRINGS = "pref_parings";

    private MainViewModel mViewModel;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences_settings);
        addPreferencesFromResource(R.xml.preferences_dev);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        mViewModel.getPairings().observe(this, pairings -> {
            final Preference pairingsPref = findPreference(KEY_PAIRINGS);
            if (pairingsPref != null) {
                pairingsPref.setSummary(getString(R.string.template_pairing_count, pairings.length));
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Preference sdkVerPref = findPreference(KEY_SDK_VER);
        if (sdkVerPref != null) {
            Map<String, String> info = Authenticator.getSDKInfo();
            sdkVerPref.setSummary(
                    String.format("%s (%s) - %s",
                            info.get("VERSION_NAME"),
                            info.get("VERSION_CODE"),
                            info.get("BUILD_TYPE"))
            );
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    final String[] RESTART = new String[]{ Config.PREFKEY_ENDPOINT, Config.PREFKEY_API_CODE };
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Arrays.asList(RESTART).contains(key)) {
            Snackbar.make(getView(), getString(R.string.message_change_restart), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        switch (preference.getKey()) {
            case KEY_PAIRINGS:
                goManageParings();
                return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    private void goManageParings() {
        Navigation.findNavController(getView()).
                navigate(SettingsFragmentDirections.actionManagePairing());
    }
}
