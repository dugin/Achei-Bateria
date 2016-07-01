package eliteapps.SOSBattery.domain;

/**
 * Created by Rodrigo on 10/06/2016.
 */
public class Cabo {

    private Boolean android, iphone;

    public Cabo(Boolean android, Boolean iphone) {
        this.android = android;
        this.iphone = iphone;
    }

    public Cabo() {

    }

    public Boolean getAndroid() {
        return android;
    }

    public void setAndroid(Boolean android) {
        this.android = android;
    }

    public Boolean getIphone() {
        return iphone;
    }

    public void setIphone(Boolean iphone) {
        this.iphone = iphone;
    }
}

