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
            response = "Список пользователей пуст.";
        } else {
            response = allFriendsInfo(friends);
        }

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(response);

        return message;
    }

    public String allFriendsInfo(List<Friend> friends) {
        StringBuilder response = new StringBuilder("Список пользователей (текущий календарный год):\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate currentDate = LocalDate.now();

        for (Friend friend : friends) {
            LocalDate birthDate = friend.getBirthDate();
            LocalDate nextBirthday = birthDate.withYear(currentDate.getYear());
            if (nextBirthday.isBefore(currentDate) || nextBirthday.isEqual(currentDate)) {
                response.append("* ").append(friend.getBirthDate().format(formatter))
                        .append(" ").append(friend.getName())
                        .append(" (в этом году исполнилось ").append(friend.getAge()).append(")")
                        .append("\n");
            } else {
                response.append("* ").append(friend.getBirthDate().format(formatter))
                        .append(" ").append(friend.getName())
                        .append(" (сейчас пользователю ").append(friend.getAge())
                        .append(", в этом году исполнится ").append(friend.getNextAge()).append(")")
                        .append("\n");
            }
        }
        return response.toString();
    }
}
