package app.nomad.projects.yashasvi.hotspots.models;

public class Timings {

    private int id;
    private int place_id;
    private String monday;
    private String tuesday;
    private String wednesday;
    private String thursday;
    private String friday;
    private String saturday;
    private String sunday;

    public Timings(int id, int place_id, String monday, String tuesday, String wednesday, String thursday,
	    String friday, String saturday, String sunday) {
	super();
	this.id = id;
	this.place_id = place_id;
	this.monday = monday;
	this.tuesday = tuesday;
	this.wednesday = wednesday;
	this.thursday = thursday;
	this.friday = friday;
	this.saturday = saturday;
	this.sunday = sunday;
    }

    public int getPlace_id() {
	return place_id;
    }

    public void setPlace_id(int place_id) {
	this.place_id = place_id;
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getMonday() {
	return monday;
    }

    public void setMonday(String monday) {
	this.monday = monday;
    }

    public String getTuesday() {
	return tuesday;
    }

    public void setTuesday(String tuesday) {
	this.tuesday = tuesday;
    }

    public String getWednesday() {
	return wednesday;
    }

    public void setWednesday(String wednesday) {
	this.wednesday = wednesday;
    }

    public String getThursday() {
	return thursday;
    }

    public void setThursday(String thursday) {
	this.thursday = thursday;
    }

    public String getFriday() {
	return friday;
    }

    public void setFriday(String friday) {
	this.friday = friday;
    }

    public String getSaturday() {
	return saturday;
    }

    public void setSaturday(String saturday) {
	this.saturday = saturday;
    }

    public String getSunday() {
	return sunday;
    }

    public void setSunday(String sunday) {
	this.sunday = sunday;
    }

}
