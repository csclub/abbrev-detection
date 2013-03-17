import java.lang.String;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        String s = "аура (Б.";
        Pattern p = Pattern.compile("\\W*");
        Matcher m = p.matcher("..,&");
        boolean b = m.matches();
        System.out.println("\\u" + Integer.toHexString(' ' | 0x10000).substring(1));
    }
}
