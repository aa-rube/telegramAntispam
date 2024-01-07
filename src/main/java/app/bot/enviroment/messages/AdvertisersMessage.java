package app.bot.enviroment.messages;

import app.bot.enviroment.keyboards.AdvertisersKeyboard;
import app.model.AdvertisersUser;
import app.model.Group;
import app.service.AdvertisersService;
import app.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

@Service
public class AdvertisersMessage {
    @Autowired
    private AdvertisersService advertisersService;
    @Autowired
    private AdvertisersKeyboard keyboard;
    @Autowired
    private GroupService groupService;
    private final StringBuilder builder = new StringBuilder();

    private SendMessage getSendMessage(Long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        msg.setReplyMarkup(markup);
        msg.enableHtml(true);
        msg.setParseMode(ParseMode.HTML);
        return msg;
    }

    public SendMessage getAdvertisersStart(Long chatId) {
        builder.setLength(0);
        builder.append("Тут можно настроить кто, в какую группу и сколько может разместить постов.");
        return getSendMessage(chatId, builder.toString(), keyboard.getMainMenu());
    }

    public SendMessage addAdvertiser(Long chatId) {
        builder.setLength(0);
        builder.append("Введите @username рекламодателя");
        return getSendMessage(chatId, builder.toString(), keyboard.getBackToMain());
    }

    public SendMessage getGroupsKeyList(Long chatId, String userName, int i) {
        builder.setLength(0);
        builder.append("Выберите группу, где пользователь: ").append(userName).append(" может размещать посты");
        List<Group> groupsList = groupService.findAllGroupsByOwnerChatId(chatId);
        return getSendMessage(chatId, builder.toString(), keyboard.getLookAfterToAllUsers(groupsList, i));
    }

    public SendMessage addCountPostKeys(Long chatId, AdvertisersUser advertisersUser) {
        builder.setLength(0);

        builder.append("Выберите колличество постов:\n")
                .append("Пользователь ").append(advertisersUser.getUserName()).append("\n")
                .append("Группа:").append(advertisersUser.getPermissionToGroup());

        return getSendMessage(chatId, builder.toString(), keyboard.getPostCountKeyboard());
    }

    public SendMessage dataSaved(Long chatId, AdvertisersUser user) {
        builder.setLength(0);

        builder.append("Пользователь: ").append(user.getUserName()).append("\n")
                .append("Может отправить ").append(user.getPostCount()).append(" постов ")
                .append("в группу: ").append(user.getPermissionToGroup()).append("\n")

                .append("до: ").append(user.getEndPermission() == null ?
                        "период использования не начался" :
                        user.getEndPermission().toString().substring(0, 16)).append("\n")

                .append(" (").append(user.getDaysOfPermission()).append(" дней)\n\n")
                .append("Можно ввести @username еще одного пользователя.");

        return getSendMessage(chatId, builder.toString(), keyboard.getBackToMain());
    }

    public SendMessage getListUsersPermission(Long chatId) {
        builder.setLength(0);
        builder.append("СПИСОК РАЗРЕШЕНИЙ ПАРТНЕРАМ - КАЖДЫЙ ПАРТНЕР БУДЕТ В СПИСКЕ СТОЛЬКО РАЗ, ")
                .append("ВО СКОЛЬКО ГРУПП ЕСТЬ РАЗРЕШЕНИЕ ДЛЯ ПУБЛИКАЦИЙ\n\n");

        for (AdvertisersUser user : advertisersService.getAllUsersByOwnerChatId(chatId)) {
            builder.append("Партнер: ").append(user.getUserName()).append("\n")
                    .append("Группа: ").append(user.getPermissionToGroup()).append("\n")
                    .append("Постов осталось: ").append(user.getPostCount()).append("\n")

                    .append("до: ").append(user.getEndPermission() == null ?
                            "период использования не начался\n" :
                            user.getEndPermission().toString().substring(0, 16)).append("\n")

                    .append("Дней осталось: ").append(user.getDaysOfPermission()).append("\n")
                    .append("/deletePartner_").append(user.getId()).append("\n")
                    .append("/addOnePost_").append(user.getId()).append("\n")
                    .append("/removeOnePost_").append(user.getId()).append("\n\n");
        }

        return getSendMessage(chatId, builder.toString(), keyboard.getBackToMain());
    }
}
