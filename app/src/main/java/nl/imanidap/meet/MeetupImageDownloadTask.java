package nl.imanidap.meet;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by maniflames on 13/04/2018.
 */

public class MeetupImageDownloadTask extends AsyncTask<MeetEvent, Void, Bitmap>{
    public static final String TEST_URL = "https://secure.meetupstatic.com/photos/event/1/6/9/0/global_466985776.jpeg";
    private MeetupImageDownloadCallback callback;

    MeetupImageDownloadTask(MeetupImageDownloadCallback cb){
        super();
        callback = cb;
    }

    @Override
    protected Bitmap doInBackground(MeetEvent... meetEvents) {
        try {
            MeetEvent meetEvent = meetEvents[0];
            URL url = new URL(meetEvent.getImageUrl());
            return DownloadUtils.getRequestImage(url);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap b) {

        callback.loadImagePreview(b);
    }
}
