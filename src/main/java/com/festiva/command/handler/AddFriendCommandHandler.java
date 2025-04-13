package com.festiva.command.handler;

import com.festiva.command.CommandHandler;
import com.festiva.datastorage.CustomDAO;
import com.festiva.datastorage.entity.Friend;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class AddFriendCommandHandler implements CommandHandler {

    private final CustomDAO dao;

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        Long telegramUserId = update.getMessage().getFrom().getId();
        String text = update.getMessage().getText();
        SendMessage response;

        if (text.equals("/add")) {
            response = new SendMessage();
            response.setChatId(String.valueOf(chatId));
            response.setText("Введите имя и дату рождения следующим образом: /add Имя гггг-мм-дд");
        } else {
            // Delegate to FriendCreator for processing.
            response = handleAddFriend(chatId, telegramUserId, text);
        }
        return response;
    }

    private SendMessage handleAddFriend(long chatId, Long telegramUserId, String messageText) {
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
