package com.festiva.handler;

import com.festiva.command.CommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
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
        message.setText("Выберите месяц, чтобы посмотреть дни рождения:");
        message.setReplyMarkup(markup);

        return message;
    }

    private List<List<InlineKeyboardButton>> buildKeyboardRows() {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // First row: button for current month selection
        List<InlineKeyboardButton> currentMonthRow = new ArrayList<>();
        currentMonthRow.add(createButton("Текущий месяц", "MONTH_CURRENT"));
        rows.add(currentMonthRow);

        // Remaining rows: buttons for each month (numeric buttons)
        final int columns = 4;
        List<InlineKeyboardButton> row = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {

            InlineKeyboardButton button = createButton(String.valueOf(month), "MONTH_" + month);
            row.add(button);

            if (month % columns == 0) {
                // Add a copy of the row and clear for the next set
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
