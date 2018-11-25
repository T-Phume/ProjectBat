package telecommunication.alliance.bat.com.projectbat;

public class Post {

    private String uri;
    private String title;
    private String state;
    private String location;

    public Post() {
    }

    public Post(String uri, String title, String state, String location) {
        this.uri = uri;
        this.title = title;
        this.state = state;
        this.location = location;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}


