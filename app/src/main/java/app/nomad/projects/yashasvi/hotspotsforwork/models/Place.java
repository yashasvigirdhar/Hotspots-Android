package app.nomad.projects.yashasvi.hotspotsforwork.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable {

    private static final long serialVersionUID = -1240385827276200982L;

    Integer id;

    String name;
    String city;
    String latitude;
    String longitude;
    String phone;
    String food;
    String wifiSpeed;
    String wifiPaid;
    String ambiance;
    String service;
    String chargingPoints;
    String description;
    String zomatoUrl;

    public Place(Integer id, String name, String city, String latitude, String longitude, String phoneNumber,
                 String wifispeed, String wifipaid, String service, String ambiance, String food, String charingpoints,
                 String description, String zomatoUrl) {
        this.id = id;
        this.city = city;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phoneNumber;
        this.food = food;
        this.wifiSpeed = wifispeed;
        this.wifiPaid = wifipaid;
        this.ambiance = ambiance;
        this.service = service;
        this.chargingPoints = charingpoints;
        this.description = description;
        this.zomatoUrl = zomatoUrl;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phoneNumber) {
        this.phone = phoneNumber;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWifiPaid() {
        return wifiPaid;
    }

    public void setWifiPaid(String wifipaid) {
        this.wifiPaid = wifipaid;
    }

    public String getChargingPoints() {
        return chargingPoints;
    }

    public void setChargingPoints(String chargingpoints) {
        this.chargingPoints = chargingpoints;
    }

    public String getZomatoUrl() {
        return zomatoUrl;
    }

    public void setZomatoUrl(String zomatoLink) {
        this.zomatoUrl = zomatoLink;
    }

    public String getAmbiance() {
        return ambiance;
    }

    public void setAmbiance(String ambiance) {
        this.ambiance = ambiance;
    }

    public String getWifiSpeed() {
        return wifiSpeed;
    }

    public void setWifiSpeed(String wifiSpeed) {
        this.wifiSpeed = wifiSpeed;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Place [name=" + name + ", city=" + city + ", latitude=" + latitude + ", longitude=" + longitude
                + ", phone=" + phone + ", food=" + food + ", wifiSpeed=" + wifiSpeed + ", wifiPaid=" + wifiPaid
                + ", ambiance=" + ambiance + ", service=" + service + ", chargingPoints=" + chargingPoints
                + ", description=" + description + ", zomatoUrl=" + zomatoUrl + "]";
    }

    protected Place(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        name = in.readString();
        city = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        phone = in.readString();
        food = in.readString();
        wifiSpeed = in.readString();
        wifiPaid = in.readString();
        ambiance = in.readString();
        service = in.readString();
        chargingPoints = in.readString();
        description = in.readString();
        zomatoUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(id);
        }
        dest.writeString(name);
        dest.writeString(city);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(phone);
        dest.writeString(food);
        dest.writeString(wifiSpeed);
        dest.writeString(wifiPaid);
        dest.writeString(ambiance);
        dest.writeString(service);
        dest.writeString(chargingPoints);
        dest.writeString(description);
        dest.writeString(zomatoUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };
}
