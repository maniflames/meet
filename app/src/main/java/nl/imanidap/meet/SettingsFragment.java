package nl.imanidap.meet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Created by maniflames on 08/04/2018.
 */

//suppressing linter to pass a reference to an activity to get a preference manager
@SuppressLint("ValidFragment")
public class SettingsFragment extends PreferenceFragment {
    private WeakReference<Activity> activity;
    SettingsFragment(Activity a){
        activity = new WeakReference<Activity>(a);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
