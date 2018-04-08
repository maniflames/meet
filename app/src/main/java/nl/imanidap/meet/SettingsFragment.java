package nl.imanidap.meet;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * Created by maniflames on 08/04/2018.
 */

public class SettingsFragment extends PreferenceFragment {
    private WeakReference<MapsActivity> mapsActivity;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }

}
