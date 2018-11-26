package telecommunication.alliance.bat.com.projectbat;

public class Friend {
    String name;
    String uri;
    String ref;

    public Friend() {
    }

    public Friend(String name, String uri, String ref) {
        this.name = name;
        this.uri = uri;
        this.ref = ref;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }
}
