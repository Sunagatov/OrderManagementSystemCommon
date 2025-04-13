package com.festiva.datastorage;

import java.util.List;

public interface CustomDAO {

    void addFriend(long telegramUserId, Friend friend);

    List<Friend> getFriends(long telegramUserId);

    boolean friendExists(long telegramUserId, String name);

    void deleteFriend(long telegramUserId, String name);

    List<Friend> getAllBySortedByDayMonth(long telegramUserId);

    List<Friend> getAllSortedByUpcomingBirthday(long telegramUserId);

    List<Long> getAllUserIds();
}