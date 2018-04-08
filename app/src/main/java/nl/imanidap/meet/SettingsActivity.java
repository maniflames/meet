package nl.imanidap.meet;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.Nullable;

/**
 * Created by maniflames on 08/04/2018.
 */

public class SettingsActivity extends Activity {

    public static final String KEY_TYPE_THEATER = "pref_event_type_theater";
    public static final Boolean DEFAULT_TYPE_THEATER = true;
    public static final String CATEGORY_IDS_THEATER = "1,15,20,27";
    public static final String KEY_TYPE_LITERATURE = "pref_event_type_literature";
    public static final Boolean DEFAULT_TYPE_LITERATURE = false;
    public static final String CATEGORY_IDS_LITERATURE = "18,36";
    public static final String KEY_TYPE_TECH = "pref_event_type_tech";
    public static final Boolean DEFAULT_TYPE_TECH = false;
    public static final String CATEGORY_IDS_TECH = "3,11,34";
    public static final String KEY_TYPE_SPORTS = "pref_event_type_sports";
    public static final Boolean DEFAULT_TYPE_SPORTS = false;
    public static final String CATEGORY_IDS_SPORTS = "5,23,32";
    public static final String KEY_TYPE_SPIRITUALITY = "pref_event_type_spirituality";
    public static final Boolean DEFAULT_TYPE_SPIRITUALITY= false;
    public static final String CATEGORY_IDS_SPIRITUALITY = "22,24";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();

    }
}
