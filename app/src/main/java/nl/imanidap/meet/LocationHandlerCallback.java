package nl.imanidap.meet;

import android.location.Location;

/**
 * LocationHandlerCallback
 *
 * An interface to ensure the onUserLocationSuccess method can be executed
 * @see LocationHandler
 */

public interface LocationHandlerCallback {
    public void onUserLocationSuccess(Location location);
}
