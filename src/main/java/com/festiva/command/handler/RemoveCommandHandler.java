package com.festiva.command.handler;

import com.festiva.command.CommandHandler;
import com.festiva.datastorage.CustomDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class RemoveCommandHandler implements CommandHandler {

    private final CustomDAO dao;

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

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
