package nl.imanidap.meet;

import java.io.Serializable;

/**
 * Created by maniflames on 01/04/2018.
 */

//implementing serializable so I can pass the entire object through an intent
public class MeetEvent implements Serializable {
    private String groupName;
    private String name;
    private Double latitude;
    private Double longitude;
    private Integer time;
    private Integer rsvpCount;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getRsvpCount() {
        return rsvpCount;
    }

    public void setRsvpCount(Integer rsvpCount) {
        this.rsvpCount = rsvpCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
