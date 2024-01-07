package app.bot.enviroment.messages;

import app.bot.enviroment.keyboards.AdminKeyboard;
import app.model.BotAdmin;
import app.model.VipUser;
import app.service.BotAdminService;
import app.service.VipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Service
public class AdminMessage {
    @Autowired
    private AdminKeyboard adminKeyboard;
    @Autowired
    private BotAdminService service;
    @Autowired
    private VipService vipService;
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
    public SendMessage getAdminsList(Long chatId) {
        builder.setLength(0);

        for (BotAdmin admin : service.getAllAdmins()) {
            builder.append(admin.getName()).append("\n")
                    .append("/deleteAdmin_").append(admin.getId()).append("\n\n");
        }

        if (builder.isEmpty() || builder.toString().equals("null")) {
            return getSendMessage(chatId, "Список пуст", adminKeyboard.getBackToAdminsOptions());
        }

        return getSendMessage(chatId, builder.toString(), adminKeyboard.getBackToAdminsOptions());
    }

    public SendMessage getAddNewAdmin(Long chatId) {
        builder.setLength(0);
        builder.append("Введи @username нового админа. Добавлять админов может только владелец");
        return getSendMessage(chatId, builder.toString(), adminKeyboard.getBackToAdminsOptions());
    }

    public SendMessage vipUsersMainMenu(Long chatId) {
        builder.setLength(0);
        builder.append("Меню для управления списком юзеров исключений");
        return getSendMessage(chatId, builder.toString(), adminKeyboard.getOptionsForVipUsers());
    }

    public SendMessage addVipUsers(String adding, Long chatId) {
        builder.setLength(0);
        builder.append(adding)
                .append("Введи @username нового пользователя для списка исключений. Можно списком через пробел или с переносом на новую строку");
        return getSendMessage(chatId, builder.toString(), adminKeyboard.getBackToVipOption());
    }

    public SendMessage getVipListUser(Long chatId) {
        builder.setLength(0);
        builder.append("Список юзеров исключений:\n");

        for (VipUser user : vipService.findAllVipUsers()) {
            builder.append(user.getUserName()).append("\n")
                    .append("/deleteVip").append(user.getUserName()).append("\n\n");
        }

        return getSendMessage(chatId, builder.toString(), adminKeyboard.getBackToVipOption());
    }

    public SendMessage getSettingsMsg(Long chatId, int count, int length, boolean check) {
        builder.setLength(0);
        builder.append("-включить\\выключить проверку по фото\n-настроить колличество символов под фото, после которого проверка не производится\n\n")
                .append("Проверили фотo: ").append(count);
        return getSendMessage(chatId, builder.toString(), adminKeyboard.getSettingsOption(check, length));
    }

    public SendMessage enterNewLength(Long chatId) {
        builder.setLength(0);
        builder.append("Введите новое число символов, после которого проверка картинок производиться не будет.");
        return getSendMessage(chatId, builder.toString(), adminKeyboard.getBackToSettings());
    }
}
