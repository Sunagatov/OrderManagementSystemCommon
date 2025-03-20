package com.festiva.datastorage.inmemory;

import com.festiva.datastorage.Friend;
import com.festiva.datastorage.CustomDAO;
import com.festiva.datastorage.User;

import java.time.LocalDate;
import java.util.*;

public class InMemoryStorageDAO implements CustomDAO {

    private final Map<Long, User> friends;

    public InMemoryStorageDAO() {
        friends = new HashMap<>();
    }

    @Override
    public void addFriend(long telegramUserId, Friend friend) {
        User user = friends.computeIfAbsent(telegramUserId, User::new);
        user.addFriend(friend);
    }

    @Override
    public List<Friend> getFriends(long telegramUserId) {
        User user = friends.get(telegramUserId);
        if (user == null) {
            return Collections.emptyList();
        }
        return user.getFriends();
    }

    @Override
    public boolean friendExists(long telegramUserId, String name) {
        User user = friends.get(telegramUserId);
        if (user == null) {
            return false;
        }
        return user.getFriends().stream().anyMatch(friend -> friend.getName().equals(name));
    }

    @Override
    public void deleteFriend(long telegramUserId, String name) {
        User user = friends.get(telegramUserId);
        if (user != null) {
            user.getFriends().removeIf(friend -> friend.getName().equals(name));
        }
    }

    @Override
    public void close() {
    }

    @Override
    public List<Friend> getAllBySortedByDayMonth(long telegramUserId) {
        List<Friend> friends = getFriends(telegramUserId);
        if (friends.isEmpty()) {
            return friends;
        }
        friends.sort(Comparator.comparing(friend -> friend.getBirthDate().withYear(2000)));
        return friends;
    }

    @Override
    public List<Friend> getAllSortedByUpcomingBirthday(long telegramUserId) {
        List<Friend> friends = getFriends(telegramUserId);
        if (friends.isEmpty()) {
            return friends;
        }
        LocalDate currentDate = LocalDate.now();
        friends.sort(Comparator.comparing(friend -> nextBirthday(friend.getBirthDate(), currentDate)));
        return friends;
    }

    @Override
    public List<Long> getAllUserIds() {
        return new ArrayList<>(friends.keySet());
    }

    private LocalDate nextBirthday(LocalDate birthDate, LocalDate currentDate) {
        LocalDate nextBirthday = birthDate.withYear(currentDate.getYear());
        if (nextBirthday.isBefore(currentDate)) {
            nextBirthday = nextBirthday.plusYears(1);
        }
        return nextBirthday;
    }
}