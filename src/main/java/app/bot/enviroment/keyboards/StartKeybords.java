package app.bot.enviroment.keyboards;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class StartKeybords {
    public InlineKeyboardMarkup mainOwnerKeyboard(boolean checkPhotoBoolean) {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton stpwrd = new InlineKeyboardButton();
        stpwrd.setText("Cтоп слова");
        stpwrd.setCallbackData("0");
        firstRow.add(stpwrd);

        InlineKeyboardButton adv = new InlineKeyboardButton();
        adv.setText("Реклама");
        adv.setCallbackData("1");
        firstRow.add(adv);

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton exception = new InlineKeyboardButton();
        exception.setText("Список исключений");
        exception.setCallbackData("88");
        secondRow.add(exception);

        List<InlineKeyboardButton> thirdRow = new ArrayList<>();
        InlineKeyboardButton admins = new InlineKeyboardButton();
        admins.setText("Админы");
        admins.setCallbackData("3");
        thirdRow.add(admins);

        List<InlineKeyboardButton> fourthRow = new ArrayList<>();
        InlineKeyboardButton checkPhoto = new InlineKeyboardButton();
        checkPhoto.setText("Настройки");
        checkPhoto.setCallbackData("settings");
        fourthRow.add(checkPhoto);

        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(secondRow);
        keyboardMatrix.add(thirdRow);
        keyboardMatrix.add(fourthRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup mainAdminKeyboard() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton stpwrd = new InlineKeyboardButton();
        stpwrd.setText("Cтоп слова");
        stpwrd.setCallbackData("0");
        firstRow.add(stpwrd);

        InlineKeyboardButton execept = new InlineKeyboardButton();
        execept.setText("Реклама");
        execept.setCallbackData("1");
        firstRow.add(execept);

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton exception = new InlineKeyboardButton();
        exception.setText("Список исключений");
        exception.setCallbackData("88");
        secondRow.add(exception);

        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(secondRow);

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getStopWordMenu() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton list = new InlineKeyboardButton();
        list.setText("Cписок");
        list.setCallbackData("4");
        firstRow.add(list);

        InlineKeyboardButton execept = new InlineKeyboardButton();
        execept.setText("Добавить");
        execept.setCallbackData("5");
        firstRow.add(execept);


        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(getBackButton());
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getGroupsMainKeyboards() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton list = new InlineKeyboardButton();
        list.setText("Группы");
        list.setCallbackData("12");
        firstRow.add(list);

        InlineKeyboardButton execept = new InlineKeyboardButton();
        execept.setText("Партнеры");
        execept.setCallbackData("13");
        firstRow.add(execept);


        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(getBackButton());
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getAdmins() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton list = new InlineKeyboardButton();
        list.setText("Cписок");
        list.setCallbackData("8");
        firstRow.add(list);

        InlineKeyboardButton execept = new InlineKeyboardButton();
        execept.setText("Добавить");
        execept.setCallbackData("9");
        firstRow.add(execept);


        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(getBackButton());
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }


    private List<InlineKeyboardButton> getBackButton() {
        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton admins = new InlineKeyboardButton();
        admins.setText("◀️ Назад");
        admins.setCallbackData("backToStart");
        backRow.add(admins);
        return backRow;
    }
}
