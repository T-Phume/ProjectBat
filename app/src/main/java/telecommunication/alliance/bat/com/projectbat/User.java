package telecommunication.alliance.bat.com.projectbat;


import java.util.ArrayList;

public class User {
    private String username;
    private String email;
    private String country;
    private String profession;
    private String uri;
    private String displayname;

    public User() {
    }

    public User(String username, String email, String uri, String country){
        this.username = username;
        this.email = email;
        this.country = country;
        profession = "Profession";
        this.uri = uri;
        this.displayname = username;
    }

    public String getDisplayName() {
        return displayname;
    }

    public void setDisplayName(String displayName) {
        this.displayname = displayName;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {this.email = email; }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }
}
