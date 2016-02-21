package app.nomad.projects.yashasvi.hotspots.models;

public class AppFeedback {

    private String name;
    private String email;
    private String message;
    private String emotion;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    public String getEmotion() {
	return emotion;
    }

    public void setEmotion(String emotion) {
	this.emotion = emotion;
    }

    public AppFeedback(String name, String email, String message, String emotion) {
	super();
	this.name = name;
	this.email = email;
	this.message = message;
	this.emotion = emotion;
    }

}
