package com.festiva.businessLogic;

import com.festiva.datastorage.CustomDAO;
import com.festiva.datastorage.Friend;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

public class Callback {

    private final CustomDAO dao;
    BirthdateInfoProvider birthdateInfoProvider;

    public Callback(CustomDAO dao) {
        this.dao = dao;
        this.birthdateInfoProvider = new BirthdateInfoProvider();
    }

    public EditMessageText handleCallbackQuery(org.telegram.telegrambots.meta.api.objects.CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
        Message message = callbackQuery.getMessage();
        EditMessageText editMessageText = new EditMessageText();
        long chatId = message.getChatId();
        Long telegramUserId = callbackQuery.getFrom().getId();
        int messageId = message.getMessageId();

        if (callbackData.startsWith("MONTH_")) {
            String monthString = callbackData.substring("MONTH_".length());
            if (monthString.equals("CURRENT")) {
                int month = 0;
                String response = getBirthdaysForMonth(telegramUserId, month);
                editMessageText.setChatId(String.valueOf(chatId));
                editMessageText.setMessageId(messageId);
                editMessageText.setText(response);
                return editMessageText;
            } else {
                try {
                    int month = Integer.parseInt(monthString);
                    String response = getBirthdaysForMonth(telegramUserId, month);
                    editMessageText.setChatId(String.valueOf(chatId));
                    editMessageText.setMessageId(messageId);
                    editMessageText.setText(response);
                    return editMessageText;
                } catch (NumberFormatException e) {
                    editMessageText.setChatId(String.valueOf(chatId));
                    editMessageText.setMessageId(messageId);
                    editMessageText.setText("Ошибка при выборе месяца.");
                    return editMessageText;
                }
            }
        }
        return null;
    }

    private String getBirthdaysForMonth(long telegramUserId, int month) {
        List<Friend> friends = this.dao.getAllBySortedByDayMonth(telegramUserId);
        return birthdateInfoProvider.birthDatesInMonth(friends, month);
    }
}