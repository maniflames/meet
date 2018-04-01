package nl.imanidap.meet;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by maniflames on 01/04/2018.
 */

public class DowloadUtils {
    public static final String TEST_URL = "https://api.meetup.com/2/concierge?key=" + Secret.MEETUP_API_KEY + "&sign=true&category_id=1,18&text_format=plain";
    public static String getRequest(URL url){

        HttpURLConnection cURL = null;
        try {
            cURL = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream in = null;
        try {
            in = cURL.getInputStream();


            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            StringBuilder json = new StringBuilder();

            while (scanner.hasNextLine()) {
                Log.d(MapsActivity.LOG, "running tha while loop");
                json.append(scanner.nextLine());
            }

            return json.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cURL.disconnect();
        }

        return null;
    }

}
