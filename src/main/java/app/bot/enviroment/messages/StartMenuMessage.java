package app.bot.enviroment.messages;

import app.bot.enviroment.keyboards.StartKeybords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Service
public class StartMenuMessage {
    @Autowired
    StartKeybords startKeybords;
   private StringBuilder builder = new StringBuilder();
    public SendMessage getSendMessage(Long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        msg.setReplyMarkup(markup);
        msg.enableHtml(true);
        msg.setParseMode(ParseMode.HTML);
        return msg;
    }

    public SendMessage getMainOwnerMenu(Long chatId, boolean check) {
        builder.setLength(0);
        builder.append("Это АнтиСпам бот. Чем займемся?");
        return getSendMessage(chatId, builder.toString(), startKeybords.mainOwnerKeyboard(check));
    }

    public SendMessage getMainAdminMenu(Long chatId) {
        builder.setLength(0);
        builder.append("Это АнтиСпам бот. Чем займемся?");
        return getSendMessage(chatId, builder.toString(), startKeybords.mainAdminKeyboard());
    }

    public SendMessage getStopWordMainMenu(Long chatId) {
        builder.setLength(0);
        builder.append("Создай свой список стоп-слов");
        return getSendMessage(chatId, builder.toString(), startKeybords.getStopWordMenu());
    }

    public SendMessage getAdminsMainMenu(Long chatId) {
        builder.setLength(0);
        builder.append("Добавь или удали админа. Эта привелегия есть только у Владельца.");
        return getSendMessage(chatId,builder.toString(), startKeybords.getAdmins());
    }

    public SendMessage getAdvertisersMainOption(Long chatId) {
        builder.setLength(0);
        builder.append("Настройки групп и партнеров");
        return getSendMessage(chatId, builder.toString(), startKeybords.getGroupsMainKeyboards());
    }
}
