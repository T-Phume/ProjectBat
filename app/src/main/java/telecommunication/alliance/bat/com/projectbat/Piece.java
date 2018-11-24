package telecommunication.alliance.bat.com.projectbat;

public class Piece {
    String image;
    String title;
    String desc;

    public Piece(String image, String desc, String title) {
        this.image = image;
        this.desc = desc;
        this.title = title;
    }

    public Piece() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
