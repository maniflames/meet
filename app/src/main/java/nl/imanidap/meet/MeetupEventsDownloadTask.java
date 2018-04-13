package nl.imanidap.meet;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;


/**
 * MeetupEventsDownloadsTask
 *
 * An AsyncTask extention that takes care of downloading events from meetup
 */

public class MeetupEventsDownloadTask extends AsyncTask<URL, Void, ArrayList<MeetEvent>>{
    private WeakReference<MapsActivity> meetParent;
    private ArrayList<MeetEvent> meetEvents = new ArrayList<MeetEvent>();
    public static final String TEST_URL = "https://api.meetup.com/2/concierge?key=" + Secret.MEETUP_API_KEY + "&sign=true&category_id=1,18&text_format=plain";
    public static final String MEETUP_EVENTS_BASE_URL = "https://api.meetup.com/2/concierge";
    public static final String KEY_PARAM = "key";
    public static final String SIGN_PARAM = "sign";
    public static final String SIGN_VALUE = "true";
    public static final String CATEGORY_PARAM = "category_id";
    public static final String TEXT_FORMAT_PARAM = "text_format";
    public static final String TEXT_FORMAT_VALUE = "plain";
    public static final String LAT_PARAM = "lat";
    public static final String LONG_PARAM = "lon";

    /**
     * Constructor
     *
     * This constructor creates a weakreference to the MapsActivity
     *
     * @param parent
     *      A mapsActivity
     */
    MeetupEventsDownloadTask(MapsActivity parent){
        super();
        meetParent = new WeakReference<MapsActivity>(parent);
    }

    /**
     * doInBackground
     *
     * AsyncTask Hook Method, preformed on the background thread.
     * This methods uses a downloadUtil to do an actual request.
     *
     * @param urls
     *      The request that is being send
     *
     * @return meetEvents
     *      Events from MeetUp
     *
     * @see DownloadUtils
     */

    @Override
    protected ArrayList<MeetEvent> doInBackground(URL... urls) {
        if(urls[0] == null){
            return new ArrayList<MeetEvent>();
        }

        URL url = urls[0];

        String reqResult = DownloadUtils.getRequest(url);
        meetEvents = meetEventFromJSON(reqResult);

        return meetEventFromJSON(reqResult);
    }

    /**
     * meetEventFromJSON
     *
     * Parses json from a sting and turns it into MeetEvents
     *
     * @param s
     *      String representation of JSON
     *
     * @return meetEvents
     *      Events from MeetUp
     */

    private ArrayList<MeetEvent> meetEventFromJSON(String s){

        JSONObject json = null;
        try {
            json = new JSONObject(s);
            JSONArray events = json.getJSONArray("results");
            for(int i = 0; i < events.length(); i++ ){
                JSONObject event = events.getJSONObject(i);

                try{
                    JSONObject venue = event.getJSONObject("venue");

                    MeetEvent meetEvent = new MeetEvent();
                    meetEvent.setName(event.getString("name"));
                    meetEvent.setGroupName(venue.getString("name"));
                    meetEvent.setLatitude(venue.getDouble("lat"));
                    meetEvent.setLongitude(venue.getDouble("lon"));
                    meetEvent.setDescription(event.getString("description"));
                    meetEvent.setTime(event.getLong("time"));
                    meetEvent.setRsvpCount(event.getInt("yes_rsvp_count"));

                    try {
                        meetEvent.setImageUrl(event.getString("photo_url"));
                    } catch (Exception e){
                        Log.d(MapsActivity.LOG, "no image found");
                    } finally {
                        meetEvents.add(meetEvent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return meetEvents;
    }


    /**
     * onPostExecute
     *
     * AsyncTask hook method, this method is performed on the UI thread.
     * The method that is performed is part of the MapsActivity Class.
     * (addEventsToMap)
     *
     * @param meetEventsResult
     *      A list of MeetEents
     *
     * @see MapsActivity
     */
    @Override
    protected void onPostExecute(ArrayList<MeetEvent> meetEventsResult) {
        if(meetParent.get() != null){
            MapsActivity mainActivity = meetParent.get();
            mainActivity.addEventsToMap(meetEventsResult);
        }
    }

    /**
     * getMeetupCategoriesFromUserPreferences
     *
     * This reads the categories selected in the user preferences and returns the string with category id's,
     * separated by comma so it can immedeatly be put into a request url.
     *
     * @return string
     *      a string containing category id's separated by comma
     */

    public String getMeetupCategoriesFromUserPreferences(){
        StringBuilder categories = new StringBuilder();
        if(meetParent.get() != null){
            MapsActivity mainActivity = meetParent.get();

            ArrayList<String> activeCategories = new ArrayList<String>();
            SharedPreferences prefManager = PreferenceManager.getDefaultSharedPreferences(mainActivity);

            //there must be a pattern to fix this because this many if statements is never good
            if(prefManager.getBoolean(SettingsActivity.KEY_TYPE_THEATER, SettingsActivity.DEFAULT_TYPE_THEATER)){
                activeCategories.add(SettingsActivity.CATEGORY_IDS_THEATER);
            }

            if(prefManager.getBoolean(SettingsActivity.KEY_TYPE_LITERATURE, SettingsActivity.DEFAULT_TYPE_LITERATURE)){
                activeCategories.add(SettingsActivity.CATEGORY_IDS_LITERATURE);
            }

            if(prefManager.getBoolean(SettingsActivity.KEY_TYPE_TECH, SettingsActivity.DEFAULT_TYPE_TECH)){
                activeCategories.add(SettingsActivity.CATEGORY_IDS_TECH);
            }

            if(prefManager.getBoolean(SettingsActivity.KEY_TYPE_SPORTS, SettingsActivity.DEFAULT_TYPE_SPORTS)){
                activeCategories.add(SettingsActivity.CATEGORY_IDS_SPORTS);
            }

            if(prefManager.getBoolean(SettingsActivity.KEY_TYPE_SPIRITUALITY, SettingsActivity.DEFAULT_TYPE_SPIRITUALITY)){
                activeCategories.add(SettingsActivity.CATEGORY_IDS_SPIRITUALITY);
            }

            for ( Integer i = 0; i < activeCategories.size(); i++) {
                categories.append(activeCategories.get(i));

                if(i != activeCategories.size() - 1){
                    categories.append(",");
                }
            }

        }

        return categories.toString();
    }

}
