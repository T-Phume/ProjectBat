package telecommunication.alliance.bat.com.projectbat;

public class User {
    String username;
    String email;
    String country;
    String profession;

    public User() {
    }

    public User(String username, String email){
        this.username = username;
        this.email = email;
        country = "Country";
        profession = "Profession";
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
