package com.festiva.businessLogic;

import com.festiva.datastorage.CustomDAO;
import com.festiva.datastorage.Friend;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class BirthdayReminder {

    private final CustomDAO dao;
    private final BirthdayBot bot;
    private static final Logger LOGGER = Logger.getLogger(BirthdayReminder.class.getName());

    public BirthdayReminder(CustomDAO dao, BirthdayBot bot) {
        this.dao = dao;
        this.bot = bot;
        startScheduler();
    }

    private void startScheduler() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(this::checkBirthdays, 0, 1, TimeUnit.DAYS);

        LOGGER.info("Планировщик дней рождения запущен.");
    }

    private void checkBirthdays() {
        LOGGER.info("Начало проверки дней рождения...");

        List<Long> allUserIds = getAllUserIds();

        for (Long userId : allUserIds) {
            List<Friend> friends = dao.getFriends(userId);
            for (Friend friend : friends) {
                checkAndNotify(userId, friend);
            }
        }

        LOGGER.info("Проверка дней рождения завершена.");
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

    private void sendNotification(Long userId, Friend friend, String message) {
        SendMessage notification = new SendMessage();
        notification.setChatId(String.valueOf(userId));
        notification.setText(message);

        try {
            bot.execute(notification);
            LOGGER.info("Уведомление отправлено пользователю " + userId + " о дне рождения " + friend.getName());
        } catch (Exception e) {
            LOGGER.severe("Ошибка при отправке уведомления: " + e.getMessage());
        }
    }

    private List<Long> getAllUserIds() {
        return List.of(123456789L, 987654321L);
    }
}