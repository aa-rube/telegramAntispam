package app.bot.enviroment.keyboards;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupKeyboards {
    public InlineKeyboardMarkup getAddGroupKeyboard() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton add = new InlineKeyboardButton();
        add.setText("Добавить группу");
        add.setCallbackData("11");

        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("◀️ Назад");
        back.setCallbackData("1");

        row.add(add);
        row.add(back);


        keyboardMatrix.add(row);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getBackToMainGroupMenu() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("◀️ Назад");
        back.setCallbackData("1");
        row.add(back);

        keyboardMatrix.add(row);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }



    private List<InlineKeyboardButton> getBackToStartButton() {
        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton admins = new InlineKeyboardButton();
        admins.setText("◀️ Назад");
        admins.setCallbackData("backToStart");
        backRow.add(admins);
        return backRow;
    }
}
