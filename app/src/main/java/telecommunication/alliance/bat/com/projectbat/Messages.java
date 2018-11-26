package telecommunication.alliance.bat.com.projectbat;

public class Messages {
    String sender;
    String msg;

    public Messages(String sender, String msg) {
        this.sender = sender;
        this.msg = msg;
    }

    public Messages(){}

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}