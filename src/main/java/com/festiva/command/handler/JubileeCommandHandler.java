package com.festiva.command.handler;

import com.festiva.command.CommandHandler;
import com.festiva.datastorage.CustomDAO;
import com.festiva.datastorage.entity.Friend;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JubileeCommandHandler implements CommandHandler {

    private final CustomDAO dao;

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        Long telegramUserId = update.getMessage().getFrom().getId();
        List<Friend> friends = dao.getAllSortedByUpcomingBirthday(telegramUserId);

        String responseText;
        if (friends.isEmpty()) {
            responseText = "Список пользователей пуст.";
        } else {
            responseText = birthDateJubilee(friends);
        }

        SendMessage response = new SendMessage();
        response.setChatId(String.valueOf(chatId));
        response.setText(responseText);

        return response;
    }

    public String birthDateJubilee (List<Friend> friends) {
        List<Friend> jubileeFriends = new java.util.ArrayList<>();
        for (Friend friend : friends) {
            int nextAge = friend.getNextAge();
            if (nextAge % 5 == 0) {
                jubileeFriends.add(friend);
            }
        }
        if (jubileeFriends.isEmpty()) {
            return "В ближайшее время нет юбилейных дней рождения.";
        }
        StringBuilder response = new StringBuilder("Пользователи, у которых следующий день рождения - юбилей:\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        for (Friend friend : jubileeFriends) {
            response.append("* ").append(friend.getBirthDate().format(formatter))
                    .append(" ").append(friend.getName())
                    .append(" (исполнится ").append(friend.getNextAge())
                    .append(" лет)\n");
        }
        return response.toString();
    }
}
