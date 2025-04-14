package com.festiva.command.handler;

import com.festiva.command.CommandHandler;
import com.festiva.datastorage.CustomDAO;
import com.festiva.datastorage.entity.Friend;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
            responseText = "<b>Список пользователей пуст.</b>";
        } else {
            responseText = birthDateJubilee(friends);
        }

        SendMessage response = new SendMessage();
        response.setChatId(String.valueOf(chatId));
        response.setParseMode("HTML");
        response.setText(responseText);

        return response;
    }

    public String birthDateJubilee(List<Friend> friends) {
        List<Friend> jubileeFriends = new ArrayList<>();
        for (Friend friend : friends) {
            int nextAge = friend.getNextAge();
            if (nextAge % 5 == 0) {
                jubileeFriends.add(friend);
            }
        }
        if (jubileeFriends.isEmpty()) {
            return "<b>В ближайшее время нет юбилейных дней рождения.</b>";
        }
        StringBuilder response = new StringBuilder("<b>Юбилейные дни рождения</b>\n\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        for (Friend friend : jubileeFriends) {
            response.append("– ")
                    .append("<b>").append(friend.getBirthDate().format(formatter)).append("</b> ")
                    .append("<i>").append(friend.getName()).append("</i>")
                    .append(" (исполнится <b>").append(friend.getNextAge()).append("</b> лет)")
                    .append("\n");
        }
        return response.toString();
    }
}
