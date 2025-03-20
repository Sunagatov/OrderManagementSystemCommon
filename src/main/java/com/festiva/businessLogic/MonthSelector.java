package com.festiva.businessLogic;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class MonthSelector {

    public SendMessage sendMonthSelection(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выберите месяц, чтобы посмотреть дни рождения:");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> currentMonthRow = new ArrayList<>();
        InlineKeyboardButton currentMonthButton = new InlineKeyboardButton();
        currentMonthButton.setText("Текущий месяц");
        currentMonthButton.setCallbackData("MONTH_CURRENT");
        currentMonthRow.add(currentMonthButton);
        rows.add(currentMonthRow);
        int columns = 4;
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(String.valueOf(m));
            button.setCallbackData("MONTH_" + m);
            row.add(button);
            if (m % columns == 0) {
                rows.add(row);
                row = new ArrayList<>();
            }
        }
        if (!row.isEmpty()) {
            rows.add(row);
        }
        inlineKeyboardMarkup.setKeyboard(rows);
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }
}