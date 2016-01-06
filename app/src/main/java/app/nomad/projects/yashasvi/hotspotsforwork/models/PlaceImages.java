package app.nomad.projects.yashasvi.hotspotsforwork.models;

/**
 * Created by ygirdha on 1/6/16.
 */
public class PlaceImages {

    int id;
    int place_id;
    String path;
    int count;

    public PlaceImages(int id, int place_id, String path, int count) {
        this.id = id;
        this.place_id = place_id;
        this.path = path;
        this.count = count;
    }

    public int getPlace_id() {
        return place_id;
    }

    public void setPlace_id(int place_id) {
        this.place_id = place_id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "PlaceImages{" +
                "id=" + id +
                ", place_id=" + place_id +
                ", path='" + path + '\'' +
                ", count=" + count +
                '}';
    }
}
