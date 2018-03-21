package com.wastetracking.wastetracking.Model;

/**
 * Created by Elijah on 3/8/2018.
 */

// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class Address extends RealmObject {
    @Required
    private String RFIDValue;
    @Required
    private String Lat;
    @Required
    private String Lon;
    @Required
    private String Address;

    public String getRFIDValue() { return RFIDValue; }

    public void setRFIDValue(String RFIDValue) { this.RFIDValue = RFIDValue; }

    public String getLat() { return Lat; }

    public void setLat(String Lat) { this.Lat = Lat; }

    public String getLon() { return Lon; }

    public void setLon(String Lon) { this.Lon = Lon; }

    public String getAddress() { return Address; }

    public void setAddress(String Address) { this.Address = Address; }
}
