package com.festiva.command.handler;

import com.festiva.command.CommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class BirthdaysCommandHandler implements CommandHandler {

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        return sendMonthSelection(chatId);
    }

    public SendMessage sendMonthSelection(long chatId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(buildKeyboardRows());

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setParseMode("HTML");
        message.setText("<b>Просмотр дней рождения</b>\n\nВыберите месяц, чтобы увидеть список дней рождения:");
        message.setReplyMarkup(markup);

        return message;
    }

    private List<List<InlineKeyboardButton>> buildKeyboardRows() {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Первая строка: кнопка для текущего месяца
        List<InlineKeyboardButton> currentMonthRow = new ArrayList<>();
        currentMonthRow.add(createButton("Текущий месяц", "MONTH_CURRENT"));
        rows.add(currentMonthRow);

        // Остальные строки: кнопки для каждого месяца (числовые кнопки)
        final int columns = 4;
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            InlineKeyboardButton button = createButton(String.valueOf(month), "MONTH_" + month);
            row.add(button);
            if (month % columns == 0) {
                rows.add(new ArrayList<>(row));
                row.clear();
            }
        }
        if (!row.isEmpty()) {
            rows.add(new ArrayList<>(row));
        }
        return rows;
    }

    private InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }
}
