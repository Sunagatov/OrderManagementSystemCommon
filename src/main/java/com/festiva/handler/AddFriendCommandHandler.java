package com.festiva.handler;

import com.festiva.command.CommandHandler;
import com.festiva.friend.FriendCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class AddFriendCommandHandler implements CommandHandler {

    private final FriendCreator friendCreator;

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
            response = friendCreator.handleAddFriend(chatId, telegramUserId, text);
        }
        return response;
    }
}
