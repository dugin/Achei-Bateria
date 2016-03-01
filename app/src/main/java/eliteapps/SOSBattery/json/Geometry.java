package eliteapps.SOSBattery.json;

/**
 * Created by Rodrigo on 27/02/2016.
 */
public class Geometry {
    private Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "ClassPojo [location = " + location + "]";
    }
}