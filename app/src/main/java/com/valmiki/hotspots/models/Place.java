package com.valmiki.hotspots.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable {

    private static final long serialVersionUID = -1240385827276200982L;

    private final Integer id;

    private String name;
    private String address;
    private String city;
    private String latitude;
    private String longitude;
    private String phone;
    private String rating;
    private String cost;
    private String food;
    private String wifiSpeed;
    private String wifiPaid;
    private String ambiance;
    private String service;
    private String chargingPoints;
    private String description;
    private String zomatoUrl;
    private String imagesPath;

    private int imagesCount;
    private int menuImagesCount;

    public Place(Integer id, String name, String address, String city, String latitude, String longitude, String phoneNumber,
                 String rating, String cost, String wifispeed, String wifipaid, String service, String ambiance, String food, String charingpoints,
                 String description, String zomatoUrl) {
        this.id = id;
        this.address = address;
        this.city = city;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phoneNumber;
        this.rating = rating;
        this.cost = cost;
        this.food = food;
        this.wifiSpeed = wifispeed;
        this.wifiPaid = wifipaid;
        this.ambiance = ambiance;
        this.service = service;
        this.chargingPoints = charingpoints;
        this.description = description;
        this.zomatoUrl = zomatoUrl;
    }

    public String getImagesPath() {
        return imagesPath;
    }

    public void setImagesPath(String imagesPath) {
        this.imagesPath = imagesPath;
    }

    public int getImagesCount() {
        return imagesCount;
    }

    public void setImagesCount(int imagesCount) {
        this.imagesCount = imagesCount;
    }

    public int getMenuImagesCount() {
        return menuImagesCount;
    }

    public void setMenuImagesCount(int menuImagesCount) {
        this.menuImagesCount = menuImagesCount;
    }

    public Integer getId() {
        return id;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
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
        return "Place [id=" + id + ", name=" + name + ", address=" + address + ", city=" + city + ", latitude="
                + latitude + ", longitude=" + longitude + ", phone=" + phone + ", rating=" + rating + ", cost=" + cost
                + ", food=" + food + ", wifiSpeed=" + wifiSpeed + ", wifiPaid=" + wifiPaid + ", ambiance=" + ambiance
                + ", service=" + service + ", chargingPoints=" + chargingPoints + ", description=" + description
                + ", zomatoUrl=" + zomatoUrl + ", imagesPath=" + imagesPath + ", imagesCount=" + imagesCount
                + ", menuImagesCount=" + menuImagesCount + "]";
    }


    private Place(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        name = in.readString();
        address = in.readString();
        city = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        phone = in.readString();
        rating = in.readString();
        cost = in.readString();
        food = in.readString();
        wifiSpeed = in.readString();
        wifiPaid = in.readString();
        ambiance = in.readString();
        service = in.readString();
        chargingPoints = in.readString();
        description = in.readString();
        zomatoUrl = in.readString();
        imagesCount = in.readInt();
        menuImagesCount = in.readInt();
        imagesPath = in.readString();
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
        dest.writeString(address);
        dest.writeString(city);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(phone);
        dest.writeString(rating);
        dest.writeString(cost);
        dest.writeString(food);
        dest.writeString(wifiSpeed);
        dest.writeString(wifiPaid);
        dest.writeString(ambiance);
        dest.writeString(service);
        dest.writeString(chargingPoints);
        dest.writeString(description);
        dest.writeString(zomatoUrl);
        dest.writeInt(imagesCount);
        dest.writeInt(menuImagesCount);
        dest.writeString(imagesPath);
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
