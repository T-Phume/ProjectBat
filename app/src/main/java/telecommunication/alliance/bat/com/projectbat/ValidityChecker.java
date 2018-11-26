package telecommunication.alliance.bat.com.projectbat;

import java.util.regex.Pattern;

public class ValidityChecker {
    public static boolean isValidUsername(String s){
        String usernameRegex = "^[a-zA-Z0-9._-]{4,16}$";
        Pattern pat = Pattern.compile(usernameRegex);
        return !(s == null) && pat.matcher(s).matches();
    }

    public static boolean isValidProfession(String s){
        String usernameRegex = "[a-zA-Z0-9. -]{4,30}$";
        Pattern pat = Pattern.compile(usernameRegex);
        return !(s == null) && pat.matcher(s).matches();
    }
}
