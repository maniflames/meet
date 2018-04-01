package nl.imanidap.meet;

import android.os.AsyncTask;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by maniflames on 01/04/2018.
 */


//for now it has no params but I might params that serve as options
public class MeetupEventsDownloadTask extends AsyncTask<String, Void, String>{
    private WeakReference<MapsActivity> meetParent;

    MeetupEventsDownloadTask(MapsActivity parent){
        super();
        meetParent = new WeakReference<MapsActivity>(parent);
    }

    public void getEvents(){
        //build an URL
        //Perform a network request
        //parse JSON and turn it into an Event Object < this should probably happen in the constructor of Event
    }


    @Override
    //Void... voids < LOL
    protected String doInBackground(String... voids) {
        URL url = null;
        try {
            url = new URL(DowloadUtils.TEST_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return DowloadUtils.getRequest(url);
    }

    @Override
    protected void onPostExecute(String s) {

        //Check link of parent & result
        if (null != s && null != meetParent.get()) {
            MapsActivity mainActivity = meetParent.get();

            mainActivity.addEventsToMap(s);
        }
    }
}
