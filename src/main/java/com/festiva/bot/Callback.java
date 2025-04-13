package com.festiva.bot;

import com.festiva.datastorage.CustomDAO;
import com.festiva.datastorage.entity.Friend;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Callback {

    private static final String MONTH_PREFIX = "MONTH_";
    private static final String CURRENT_MONTH = "CURRENT";

    private final CustomDAO dao;

    /**
     * Handles an incoming CallbackQuery and returns an EditMessageText response containing
     * birthday information based on the callback data.
     *
     * @param callbackQuery the Telegram CallbackQuery to handle
     * @return an EditMessageText object with the response or null if no action is required
     */
    public EditMessageText handleCallbackQuery(CallbackQuery callbackQuery) {
        if (callbackQuery == null) {
            log.warn("Received a null CallbackQuery.");
            return null;
        }

        String callbackData = callbackQuery.getData();
        if (callbackData == null || !callbackData.startsWith(MONTH_PREFIX)) {
            log.debug("Callback data '{}' does not start with expected prefix '{}'.", callbackData, MONTH_PREFIX);
            return null;
        }

        MaybeInaccessibleMessage message = callbackQuery.getMessage();
        if (message == null) {
            log.warn("The CallbackQuery's message is null.");
            return null;
        }

        // Create an EditMessageText instance with chatId and messageId preset
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(String.valueOf(message.getChatId()));
        editMessage.setMessageId(message.getMessageId());

        // Parse the selected month from callback data
        int month = parseMonth(callbackData);
        if (month < 0) {
            editMessage.setText("Ошибка при выборе месяца.");
            return editMessage;
        }

        // Fetch birthdays for the specified month and update the response text
        String response = getBirthdaysForMonth(message.getChatId(), month);
        editMessage.setText(response);
        return editMessage;
    }

    /**
     * Parses the month number from the callback data.
     * If the callback data indicates CURRENT, returns 0; otherwise, attempts to parse an integer.
     *
     */
    private int parseMonth(String callbackData) {
        String monthString = callbackData.substring(MONTH_PREFIX.length());
        if (CURRENT_MONTH.equalsIgnoreCase(monthString)) {
            return 0;
        }
        try {
            return Integer.parseInt(monthString);
        } catch (NumberFormatException e) {
            log.error("Failed to parse month from callback data '{}'.", callbackData, e);
            return -1;
        }
    }

    /**
     * Retrieves and formats birthday information for the given Telegram user and month.
     */
    private String getBirthdaysForMonth(long telegramUserId, int month) {
        List<Friend> friends = dao.getAllBySortedByDayMonth(telegramUserId);
        return birthDatesInMonth(friends, month);
    }

    public String birthDatesInMonth(List<Friend> friends, int month) {
        LocalDate currentDate = LocalDate.now();
        int monthToShow = (month == 0) ? currentDate.getMonthValue() : month;

        List<Friend> friendsWithBirthdayThisMonth = friends.stream()
                .filter(friend -> friend.getBirthDate().getMonthValue() == monthToShow)
                .toList();

        if (friendsWithBirthdayThisMonth.isEmpty()) {
            return "В выбранном месяце нет дней рождения.";
        }

        StringBuilder response = new StringBuilder("Дни рождения в " + monthToShow + "-м месяце:\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        for (Friend friend : friendsWithBirthdayThisMonth) {
            response.append("* ").append(friend.getBirthDate().format(formatter))
                    .append(" ").append(friend.getName())
                    .append(" (сейчас пользователю ").append(friend.getAge()).append(")")
                    .append("\n");
        }
        return response.toString();
    }
}
