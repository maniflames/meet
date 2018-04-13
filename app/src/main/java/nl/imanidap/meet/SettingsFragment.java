package nl.imanidap.meet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * SettingsFragment
 *
 * This class serves as SettingsFragment.
 * The Settings are loaded and there is a check that keeps track if changes have been made.
 *
 * @Note suppressing linter to pass a reference to an activity to get a preference manager
 */

@SuppressLint("ValidFragment")
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private WeakReference<Activity> activity;

    /**
     * Constructor
     *
     * A weakreference to the previously active activity is made
     *
     * @param a
     *      The previously active activity
     */

    SettingsFragment(Activity a){
        activity = new WeakReference<Activity>(a);
    }


    /**
    * onCreate
    *
    * Android Hook method, performed when the activity is created.
    * The preferences are loaded
    *
    * @param savedInstanceState
     *  Parameters are inserted by the framework
    */

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }

    /**
     * onResume
     *
     * Android Hook Method, preformed when user navigates (back) to the activity.
     * The preference change listener is activated
     */

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * onPause
     *
     * Android Hook method, performed when the activity is paused (user navigates away).
     * The preference change listener is dectivated
     */

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * onSharedPreferenceChanged
     *
     * Android Hook method, a preference has changed.
     * The location manager from the LocationHandler is stopped.
     *
     * @param sharedPreferences
     *      The sharedPreferences object that received a change
     * @param s
     *      The key of the preference that was changed
     */

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        MapsActivity.settingsChanged = true;
    }
}
