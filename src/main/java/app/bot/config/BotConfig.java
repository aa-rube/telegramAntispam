package app.bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {
    @Value("${bot.username}")
    private String botName;
    @Value("${bot.token}")
    private String token;
    @Value("${owner.user.name}")
    private String ownerUserName;
    @Value("${oauth.token}")
    private String yandexToken;
    @Value("${folder.id}")
    private String folderYandexId;
    public String getFolderYandexId() {
        return folderYandexId;
    }
    public String getYandexToken() {
        return yandexToken;
    }
    public String getBotName() {
        return botName;
    }
    public String getToken() {
        return token;
    }
    public String getOwnerUserName() {
        return ownerUserName;
    }
}