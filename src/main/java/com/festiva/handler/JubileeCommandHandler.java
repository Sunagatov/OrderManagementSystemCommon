package com.festiva.handler;

import com.festiva.command.CommandHandler;
import com.festiva.datastorage.CustomDAO;
import com.festiva.datastorage.Friend;
import com.festiva.handler.helper.BirthdateInfoProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JubileeCommandHandler implements CommandHandler {

    private final CustomDAO dao;
    private final BirthdateInfoProvider birthdateInfoProvider;

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        Long telegramUserId = update.getMessage().getFrom().getId();
        List<Friend> friends = dao.getAllSortedByUpcomingBirthday(telegramUserId);

        String responseText;
        if (friends.isEmpty()) {
            responseText = "Список пользователей пуст.";
        } else {
            responseText = birthdateInfoProvider.birthDateJubilee(friends);
        }

        SendMessage response = new SendMessage();
        response.setChatId(String.valueOf(chatId));
        response.setText(responseText);

        return response;
    }
}
