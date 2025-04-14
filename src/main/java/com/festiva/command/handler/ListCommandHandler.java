package com.festiva.command.handler;

import com.festiva.command.CommandHandler;
import com.festiva.datastorage.CustomDAO;
import com.festiva.datastorage.entity.Friend;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ListCommandHandler implements CommandHandler {

    private final CustomDAO dao;

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        Long telegramUserId = update.getMessage().getFrom().getId();
        List<Friend> friends = dao.getAllBySortedByDayMonth(telegramUserId);

        String response;
        if (friends.isEmpty()) {
            response = "<b>Список пользователей пуст.</b>";
        } else {
            response = allFriendsInfo(friends);
        }

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setParseMode("HTML");
        message.setText(response);
        return message;
    }

    public String allFriendsInfo(List<Friend> friends) {
        StringBuilder response = new StringBuilder("<b>Список пользователей (текущий календарный год):</b>\n\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate currentDate = LocalDate.now();

        for (Friend friend : friends) {
            LocalDate birthDate = friend.getBirthDate();
            LocalDate nextBirthday = birthDate.withYear(currentDate.getYear());
            if (nextBirthday.isBefore(currentDate) || nextBirthday.isEqual(currentDate)) {
                response.append("– ")
                        .append("<b>").append(friend.getBirthDate().format(formatter)).append("</b> ")
                        .append("<i>").append(friend.getName()).append("</i>")
                        .append(" (в этом году исполнилось <b>").append(friend.getAge()).append("</b>)\n");
            } else {
                response.append("– ")
                        .append("<b>").append(friend.getBirthDate().format(formatter)).append("</b> ")
                        .append("<i>").append(friend.getName()).append("</i>")
                        .append(" (сейчас пользователю <b>").append(friend.getAge())
                        .append("</b>, в этом году исполнится <b>").append(friend.getNextAge()).append("</b>)\n");
            }
        }
        return response.toString();
    }
}
