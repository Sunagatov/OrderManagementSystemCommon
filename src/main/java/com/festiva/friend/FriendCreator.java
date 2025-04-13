package com.festiva.friend;

import com.festiva.datastorage.CustomDAO;
import com.festiva.datastorage.Friend;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class FriendCreator {

    private final CustomDAO dao;

    public SendMessage handleAddFriend(long chatId, Long telegramUserId, String messageText) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        int lastSpaceIndex = messageText.lastIndexOf(" ");
        if (lastSpaceIndex <= 4) {
            message.setText("Неверный формат. Используйте: /add Имя гггг-мм-дд");
            return message;
        }

        String name = messageText.substring(5, lastSpaceIndex).trim();
        String birthDateStr = messageText.substring(lastSpaceIndex + 1).trim();

        if (name.isEmpty()) {
            message.setText("Имя не может быть пустым.");
            return message;
        }

        LocalDate birthDate;
        try {
            birthDate = LocalDate.parse(birthDateStr);
        } catch (Exception e) {
            message.setText("Неверный формат даты. Используйте: гггг-мм-дд");
            return message;
        }

        if (birthDate.isAfter(LocalDate.now())) {
            message.setText("Дата рождения не может быть в будущем.");
            return message;
        }

        Friend friend = new Friend(name, birthDate);
        dao.addFriend(telegramUserId, friend);

        message.setText("Пользователь " + name + " успешно добавлен!");
        return message;
    }
}
