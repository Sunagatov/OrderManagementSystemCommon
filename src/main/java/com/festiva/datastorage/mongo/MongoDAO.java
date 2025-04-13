package com.festiva.datastorage.mongo;

import com.festiva.datastorage.CustomDAO;
import com.festiva.datastorage.entity.Friend;
import com.festiva.datastorage.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MongoDAO implements CustomDAO {

    private final UserRepository userRepository;

    @Override
    public void addFriend(long telegramUserId, Friend friend) {
        Optional<UserEntity> optional = userRepository.findByTelegramUserId(telegramUserId);
        UserEntity user;
        if(optional.isPresent()){
            user = optional.get();
        } else {
            user = new UserEntity();
            user.setTelegramUserId(telegramUserId);
        }
        user.getFriends().add(friend);
        userRepository.save(user);
    }

    @Override
    public List<Friend> getFriends(long telegramUserId) {
        Optional<UserEntity> optional = userRepository.findByTelegramUserId(telegramUserId);
        return optional.map(UserEntity::getFriends).orElse(new ArrayList<>());
    }

    @Override
    public boolean friendExists(long telegramUserId, String name) {
        List<Friend> friends = getFriends(telegramUserId);
        return friends.stream().anyMatch(friend -> friend.getName().equals(name));
    }

    @Override
    public void deleteFriend(long telegramUserId, String name) {
        Optional<UserEntity> optional = userRepository.findByTelegramUserId(telegramUserId);
        if(optional.isPresent()){
            UserEntity user = optional.get();
            user.getFriends().removeIf(friend -> friend.getName().equals(name));
            userRepository.save(user);
        }
    }

    @Override
    public List<Friend> getAllBySortedByDayMonth(long telegramUserId) {
        List<Friend> friends = getFriends(telegramUserId);
        friends.sort(Comparator.comparing(friend -> friend.getBirthDate().withYear(2000)));
        return friends;
    }

    @Override
    public List<Friend> getAllSortedByUpcomingBirthday(long telegramUserId) {
        LocalDate currentDate = LocalDate.now();
        List<Friend> friends = getFriends(telegramUserId);
        friends.sort(Comparator.comparing(friend -> nextBirthday(friend.getBirthDate(), currentDate)));
        return friends;
    }

    private LocalDate nextBirthday(LocalDate birthDate, LocalDate currentDate) {
        LocalDate nextBirthday = birthDate.withYear(currentDate.getYear());
        if (!nextBirthday.isAfter(currentDate)) {
            nextBirthday = nextBirthday.plusYears(1);
        }
        return nextBirthday;
    }

    @Override
    public List<Long> getAllUserIds() {
        List<UserEntity> users = userRepository.findAll();
        List<Long> ids = new ArrayList<>();
        for(UserEntity user : users){
            ids.add(user.getTelegramUserId());
        }
        return ids;
    }
}
