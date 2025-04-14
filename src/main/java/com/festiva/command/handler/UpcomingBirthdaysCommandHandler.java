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
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UpcomingBirthdaysCommandHandler implements CommandHandler {

    private final CustomDAO dao;

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        Long telegramUserId = update.getMessage().getFrom().getId();

        List<Friend> friends = dao.getAllBySortedByDayMonth(telegramUserId);

        String responseText = upcomingBirthdaysWithin30Days(friends);

        SendMessage response = new SendMessage();
        response.setChatId(String.valueOf(chatId));
        response.setParseMode("HTML");
        response.setText(responseText);

        return response;
    }

    public String upcomingBirthdaysWithin30Days(List<Friend> friends) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        List<Friend> upcomingFriends = friends.stream()
                .filter(friend -> {
                    long daysUntil = calculateDaysCountBeforeUpcomingBirthday(friend);
                    return daysUntil >= 0 && daysUntil <= 30;
                })
                .sorted(Comparator
                        .comparing(this::getNextBirthday)
                        .thenComparing(Friend::getName))
                .toList();
        if (upcomingFriends.isEmpty()) {
            return "<b>В ближайшие 30 дней нет дней рождения.</b>";
        }
        StringBuilder sb = new StringBuilder("<b>Ближайшие дни рождения:</b>\n\n");
        for (Friend friend : upcomingFriends) {
            long daysUntil = calculateDaysCountBeforeUpcomingBirthday(friend);
            LocalDate friendNextBirthday = getNextBirthday(friend);
            sb.append("– ")
                    .append("<b>").append(friendNextBirthday.format(formatter)).append("</b> ")
                    .append("<i>").append(friend.getName()).append("</i>")
                    .append(" (исполнится <b>").append(friend.getNextAge()).append("</b>, ")
                    .append("дней до дня рождения — <b>").append(daysUntil).append("</b>)\n");
        }
        return sb.toString();
    }

    public int calculateDaysCountBeforeUpcomingBirthday(Friend friend) {
        LocalDate currentDate = LocalDate.now();
        LocalDate birthDate = friend.getBirthDate();

        int month = birthDate.getMonthValue();
        int day = birthDate.getDayOfMonth();
        int year = currentDate.getYear();
        LocalDate upcomingBirthDate = LocalDate.of(year, month, day);

        if (upcomingBirthDate.isBefore(currentDate)) {
            upcomingBirthDate = LocalDate.of(year + 1, month, day);
        }

        return (int) java.time.temporal.ChronoUnit.DAYS.between(currentDate, upcomingBirthDate);
    }

    private LocalDate getNextBirthday(Friend friend) {
        LocalDate currentDate = LocalDate.now();
        LocalDate nextBirthday = friend.getBirthDate().withYear(currentDate.getYear());
        if (!nextBirthday.isAfter(currentDate)) {
            nextBirthday = nextBirthday.plusYears(1);
        }
        return nextBirthday;
    }
}
