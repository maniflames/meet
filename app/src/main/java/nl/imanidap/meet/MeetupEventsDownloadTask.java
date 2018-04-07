package nl.imanidap.meet;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by maniflames on 01/04/2018.
 */


public class MeetupEventsDownloadTask extends AsyncTask<String, Void, ArrayList<MeetEvent>>{
    private WeakReference<MapsActivity> meetParent;
    private ArrayList<MeetEvent> meetEvents = new ArrayList<MeetEvent>();

    MeetupEventsDownloadTask(MapsActivity parent){
        super();
        meetParent = new WeakReference<MapsActivity>(parent);
    }

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
                    meetEvent.setGroupName(event.getJSONObject("group").getString("name"));
                    meetEvent.setLatitude(event.getJSONObject("venue").getDouble("lat"));
                    meetEvent.setLongitude(event.getJSONObject("venue").getDouble("lon"));
                    meetEvent.setDescription(event.getString("description"));
                    meetEvent.setTime(event.getLong("time"));
                    meetEvent.setRsvpCount(event.getInt("yes_rsvp_count"));

                    meetEvents.add(meetEvent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return meetEvents;
    }


    @Override
    //Void... voids < LOL
    protected ArrayList<MeetEvent> doInBackground(String... voids) {
        URL url = null;
        try {
            url = new URL(DowloadUtils.TEST_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String reqResult = DowloadUtils.getRequest(url);
        meetEvents = meetEventFromJSON(reqResult);

        return meetEventFromJSON(reqResult);
    }

    @Override
    protected void onPostExecute(ArrayList<MeetEvent> meetEventsResult) {
        if(meetParent.get() != null){
            MapsActivity mainActivity = meetParent.get();
            mainActivity.addEventsToMap(meetEventsResult);
        }
    }
}
