package eliteapps.SOSBattery.json;

/**
 * Created by Rodrigo on 26/02/2016.
 */
public class Result {
    private String icon;

    private Geometry geometry;

    private Opening_hours opening_hours;

    private Photos[] photos;

    private Address_components[] address_components;

    private String name;

    private String formatted_phone_number;

    private String[] types;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }


    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Opening_hours getOpening_hours() {
        return opening_hours;
    }

    public void setOpening_hours(Opening_hours opening_hours) {
        this.opening_hours = opening_hours;
    }

    public Photos[] getPhotos() {
        return photos;
    }

    public void setPhotos(Photos[] photos) {
        this.photos = photos;
    }

    public Address_components[] getAddress_components() {
        return address_components;
    }

    public void setAddress_components(Address_components[] address_components) {
        this.address_components = address_components;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormatted_phone_number() {
        return formatted_phone_number;
    }

    public void setFormatted_phone_number(String formatted_phone_number) {
        this.formatted_phone_number = formatted_phone_number;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    @Override
    public String toString() {
        return "ClassPojo [icon = " + icon + ", geometry = " + geometry + ", opening_hours = " + opening_hours + ", address_components = " + address_components + ", name = " + name + ", formatted_phone_number = " + formatted_phone_number + ", types = " + types + "]";
    }
}