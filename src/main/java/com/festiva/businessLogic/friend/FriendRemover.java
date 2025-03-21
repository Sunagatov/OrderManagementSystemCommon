package com.festiva.businessLogic.friend;

import com.festiva.datastorage.CustomDAO;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class FriendRemover {

    private final CustomDAO dao;
    public FriendRemover(CustomDAO dao) {
        this.dao = dao;
    }

    public SendMessage handleRemoveFriend(long chatId, String messageText) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        try {
            String[] parts = messageText.split(" ", 2);

            if (parts.length < 2 || parts[1].trim().isEmpty()) {
                message.setText("Неверный формат. Используйте: /remove Имя");
                return message;
            }

            String name = parts[1].trim();

            if (!dao.friendExists(chatId, name)) {
                message.setText("Пользователь с именем " + name + " не найден.");
                return message;
            }

            dao.deleteFriend(chatId, name);
            message.setText("Пользователь " + name + " успешно удален!");
            return message;

        } catch (Exception e) {
            message.setText("Ошибка при удалении пользователя. Попробуйте еще раз.");
            return message;
        }
    }
}