package com.festiva.handler;

import com.festiva.command.CommandHandler;
import com.festiva.handler.helper.BirthdateInfoProvider;
import com.festiva.datastorage.CustomDAO;
import com.festiva.datastorage.Friend;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListCommandHandler implements CommandHandler {

    private final CustomDAO dao;
    private final BirthdateInfoProvider birthdateInfoProvider;

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        Long telegramUserId = update.getMessage().getFrom().getId();
        List<Friend> friends = dao.getAllBySortedByDayMonth(telegramUserId);

        String response;
        if (friends.isEmpty()) {
            response = "Список пользователей пуст.";
        } else {
            response = birthdateInfoProvider.allFriendsInfo(friends);
        }

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(response);

        return message;
    }
}
