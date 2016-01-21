package app.nomad.projects.yashasvi.hotspotsforwork.models;

public class PlaceFeedback {

    int id;
    int place_id;
    int feeling;
    int wifi;
    int food;
    int ambiance;
    int service;
    String name;
    String message;
    
    
    public PlaceFeedback(int place_id, int feeling, int wifi, int food, int ambiance, int service, String name,
	    String message) {
	super();
	this.place_id = place_id;
	this.feeling = feeling;
	this.wifi = wifi;
	this.food = food;
	this.ambiance = ambiance;
	this.service = service;
	this.name = name;
	this.message = message;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public int getPlace_id() {
        return place_id;
    }


    public void setPlace_id(int place_id) {
        this.place_id = place_id;
    }


    public int getFeeling() {
        return feeling;
    }


    public void setFeeling(int feeling) {
        this.feeling = feeling;
    }


    public int getWifi() {
        return wifi;
    }


    public void setWifi(int wifi) {
        this.wifi = wifi;
    }


    public int getFood() {
        return food;
    }


    public void setFood(int food) {
        this.food = food;
    }


    public int getAmbiance() {
        return ambiance;
    }


    public void setAmbiance(int ambiance) {
        this.ambiance = ambiance;
    }


    public int getService() {
        return service;
    }


    public void setService(int service) {
        this.service = service;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    @Override
    public String toString() {
	return "PlaceFeedback [id=" + id + ", place_id=" + place_id + ", feeling=" + feeling + ", wifi=" + wifi
		+ ", food=" + food + ", ambiance=" + ambiance + ", service=" + service + ", name=" + name + ", message="
		+ message + "]";
    }
    
    
}
