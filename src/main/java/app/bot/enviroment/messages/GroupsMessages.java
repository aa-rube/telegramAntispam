package app.bot.enviroment.messages;

import app.bot.enviroment.keyboards.AdminKeyboard;
import app.bot.enviroment.keyboards.GroupKeyboards;
import app.model.Group;
import app.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Service
public class GroupsMessages {
    @Autowired
    private GroupService groupService;
    @Autowired
    private AdminKeyboard adminKeyboard;

    @Autowired
    private GroupKeyboards groupKeyboards;
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

    public SendMessage getEnterNewGroupUserName(Long chatId) {
        builder.setLength(0);
        builder.append("Введите @username группы. Обязательно с \"@\"");
        return getSendMessage(chatId, builder.toString(), groupKeyboards.getBackToMainGroupMenu());
    }

    public SendMessage getListGroup(String userName, Long chatId) {
        builder.setLength(0);
        builder.append("Список групп:\n");

        for (Group g : groupService.findAllGroupsByOwnerUserName(userName)) {
            builder.append(g.getGroupUserName()).append("\n")
                    .append("/deleteGroup_").append(g.getId()).append("\n\n");
        }
        return getSendMessage(chatId, builder.toString(), groupKeyboards.getAddGroupKeyboard());
    }
}
