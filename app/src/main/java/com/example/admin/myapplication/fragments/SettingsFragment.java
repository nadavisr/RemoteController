/*
 * Created by admin on 16/10/2017
 * Last modified 08:07 16/10/17
 */

package com.example.admin.myapplication.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.admin.myapplication.R;
import com.example.admin.myapplication.common.Intents;

import businessLogic.common.interfaces.ILog;
import services.logging.LogManager;


/**
 *
 */
public class SettingsFragment extends PreferenceFragment {

    //region Static

    private final static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new OnPreferenceChangeListener();

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToString(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }


    private static void bindPreferenceSummaryToBoolean(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getBoolean(preference.getKey(), false));
    }

    //endregion

    //region Fields

    private FloatingActionButton m_floatingActionButton;

    private ILog m_logger;

    private BroadcastReceiver m_broadcastReceiver;

    //endregion

    //region Fragment LifeCycle Methods

    @Override
    public void onAttach(Context context) {
        m_logger = LogManager.getLogger();
        m_logger.verbose("in onAttach");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        m_logger.verbose("in onCreate");

        super.onCreate(savedInstanceState);
        initializeReferences();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        m_logger.verbose("in onCreateView");

        View view = super.onCreateView(inflater, container, savedInstanceState);

        m_floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.fab_main);

        m_floatingActionButton.setVisibility(View.GONE);

        m_broadcastReceiver = new SettingsBroadcastReceiver();

        initializeBroadcastReceiver();

        return view;
    }

    @Override
    public void onStart() {
        m_logger.verbose("in onStart");

        initializeOnClickListener();
        super.onStart();
    }

    @Override
    public void onStop() {
        m_logger.verbose("in onStop");


        removeOnClickListener();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        m_logger.verbose("in onDestroyView");

        Activity activity = getActivity();
        if (m_broadcastReceiver != null) {
            activity.unregisterReceiver(m_broadcastReceiver);
        }

        m_floatingActionButton = null;
        super.onDestroyView();
    }

    //endregion

    //region Methods

    private void initializeReferences() {
        addPreferencesFromResource(R.xml.preferences);


        final String robotId = getResources().getString(R.string.pref_key_robot_id);
        bindPreferenceSummaryToString(findPreference(robotId));

        final String displayName = getResources().getString(R.string.pref_key_display_name);
        bindPreferenceSummaryToString(findPreference(displayName));

        final String videoQuality = getResources().getString(R.string.pref_key_video_quality);
        bindPreferenceSummaryToString(findPreference(videoQuality));

        final String syncFrequency = getResources().getString(R.string.pref_key_sync_frequency);
        bindPreferenceSummaryToString(findPreference(syncFrequency));

        final String debugMode = getResources().getString(R.string.pref_key_debug_mode);
        bindPreferenceSummaryToBoolean(findPreference(debugMode));
    }


    private void initializeOnClickListener() {
        if (m_floatingActionButton == null) {
            m_logger.warning("The methods initializeOnClickListener called when FloatingActionButton is null!");
            return;
        }

        removeOnClickListener();

        m_floatingActionButton.setImageResource(R.drawable.ic_back);

        m_floatingActionButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.popBackStack();
        });
    }

    private void removeOnClickListener() {
        if (m_floatingActionButton == null) {
            m_logger.warning("The methods removeOnClickListener called when FloatingActionButton is null!");
            return;
        }
        if (m_floatingActionButton.hasOnClickListeners()) {
            m_floatingActionButton.setOnClickListener(null);
        }

        m_floatingActionButton.setImageResource(0);

    }


    private void initializeBroadcastReceiver() {
        Activity activity = getActivity();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intents.KEY_VIDEO_CHANNEL_STOPPED);
        activity.registerReceiver(m_broadcastReceiver, intentFilter);
    }
    //endregion

    //region Nested Classes

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static class OnPreferenceChangeListener implements Preference.OnPreferenceChangeListener {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;

        }
    }

    private class SettingsBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action == null || action.isEmpty()){
                m_logger.warning("SettingsBroadcastReceiver received null or empty broadcast.");
                return;
            }
            switch (action) {
                case Intents.KEY_VIDEO_CHANNEL_STOPPED:
                    if (m_floatingActionButton == null) {
                        break;
                    }
                    if (m_floatingActionButton.getVisibility() != View.VISIBLE) {
                        m_floatingActionButton.setVisibility(View.VISIBLE);
                    }

            }
        }
    }
    //endregion


}
