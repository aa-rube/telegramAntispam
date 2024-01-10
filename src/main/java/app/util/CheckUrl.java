package app.util;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUrl {

    public static boolean textContainsUrl(Message message) {
        if (message.getText() == null) {
            return true;
        }

        String urlRegex = "(?i)\\b(?:https?://|www\\.)\\S+(?:/|\\b)";
        Pattern pattern = Pattern.compile(urlRegex);
        Matcher matcher = pattern.matcher(message.getText().toLowerCase());
        return matcher.find();
    }

    public static void main(String[] args) {
        String urlRegex = "(?i)\\b(?:https?://|www\\.)\\S+(?:/|\\b)";
        Pattern pattern = Pattern.compile(urlRegex);
        Matcher matcher = pattern.matcher("https://kwork.ru/offers");
        System.out.println(matcher.find());
    }
}
