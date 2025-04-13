package com.festiva.handler.helper;

import com.festiva.datastorage.Friend;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Component
public class BirthdateInfoProvider {

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

    public int calculateDaysCountBeforeUpcomingBirthday(Friend friend) {
        LocalDate currentDate = LocalDate.now();
        LocalDate birthdate = friend.getBirthDate();

        int month = birthdate.getMonthValue();
        int date = birthdate.getDayOfMonth();
        int year = currentDate.getYear();
        LocalDate upcomingBirthdate = LocalDate.of(year, month, date);

        if (upcomingBirthdate.isBefore(currentDate)) {
            year = currentDate.getYear() + 1;
            upcomingBirthdate = LocalDate.of(year, month, date);
        }

        return (int) java.time.temporal.ChronoUnit.DAYS.between(currentDate, upcomingBirthdate);
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
            return "В ближайшие 30 дней нет дней рождения.";
        }
        StringBuilder sb = new StringBuilder("Ближайшие дни рождения:\n");
        for (Friend friend : upcomingFriends) {
            long daysUntil = calculateDaysCountBeforeUpcomingBirthday(friend);
            LocalDate friendNextBirthday = getNextBirthday(friend);
            sb.append("* ")
                    .append(friendNextBirthday.format(formatter))
                    .append(" ")
                    .append(friend.getName())
                    .append(" (исполнится ")
                    .append(friend.getNextAge())
                    .append(", дней до дня рождения - ")
                    .append(daysUntil)
                    .append(")\n");
        }
        return sb.toString();
    }

    private LocalDate getNextBirthday(Friend friend) {
        LocalDate currentDate = LocalDate.now();
        LocalDate nextBirthday = friend.getBirthDate().withYear(currentDate.getYear());
        if (!nextBirthday.isAfter(currentDate)) {
            nextBirthday = nextBirthday.plusYears(1);
        }
        return nextBirthday;
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
