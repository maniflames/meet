package nl.imanidap.meet;

import android.os.AsyncTask;

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


public class MeetupEventsDownloadTask extends AsyncTask<URL, Void, ArrayList<MeetEvent>>{
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
                    meetEvent.setGroupName(venue.getString("name"));
                    meetEvent.setLatitude(venue.getDouble("lat"));
                    meetEvent.setLongitude(venue.getDouble("lon"));
                    meetEvent.setDescription(event.getString("description"));
                    meetEvent.setTime(event.getLong("time"));
                    meetEvent.setRsvpCount(event.getInt("yes_rsvp_count"));

                    //try catch img if no img, no worries it'll use a placeholder
                    //so put the addition of the meetEvent in a finally block

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
    protected ArrayList<MeetEvent> doInBackground(URL... urls) {
        if(urls[0] == null){
            return new ArrayList<MeetEvent>();
        }

        URL url = urls[0];

        String reqResult = DownloadUtils.getRequest(url);
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
