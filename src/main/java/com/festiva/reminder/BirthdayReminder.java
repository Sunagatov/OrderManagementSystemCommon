package com.festiva.reminder;

import com.festiva.bot.BirthdayBot;
import com.festiva.datastorage.CustomDAO;
import com.festiva.datastorage.entity.Friend;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BirthdayReminder {

    private final CustomDAO dao;
    private final BirthdayBot birthdayBot;

    @Scheduled(cron = "0 0 0 * * *") // каждый день в полночь
    public void checkBirthdays() {
        List<Long> allUserIds = dao.getAllUserIds();
        for (Long userId : allUserIds) {
            List<Friend> friends = dao.getFriends(userId);
            for (Friend friend : friends) {
                checkAndNotify(userId, friend);
            }
        }
    }

    private void checkAndNotify(Long userId, Friend friend) {
        LocalDate today = LocalDate.now();
        LocalDate nextBirthday = friend.getBirthDate().withYear(today.getYear());
        if (nextBirthday.isBefore(today)) {
            nextBirthday = nextBirthday.plusYears(1);
        }
        long daysUntilBirthday = ChronoUnit.DAYS.between(today, nextBirthday);
        if (daysUntilBirthday == 0) {
            sendNotification(userId, friend, "Сегодня день рождения у вашего друга " + friend.getName() + "!");
        } else if (daysUntilBirthday == 1) {
            sendNotification(userId, friend, "Завтра день рождения у вашего друга " + friend.getName() + "!");
        } else if (daysUntilBirthday == 7) {
            sendNotification(userId, friend, "Через неделю день рождения у вашего друга " + friend.getName() + "!");
        }
    }

    private void sendNotification(Long userId, Friend friend, String text) {
        SendMessage notification = new SendMessage();
        notification.setChatId(String.valueOf(userId));
        notification.setText(text);
        try {
            // Send the message using the method that accepts a SendMessage object.
            birthdayBot.sendMessage(notification);
            log.info("Уведомление отправлено пользователю {} о дне рождения {}", userId, friend.getName());
        } catch (Exception e) {
            log.error("Ошибка при отправке уведомления", e);
        }
    }
}
