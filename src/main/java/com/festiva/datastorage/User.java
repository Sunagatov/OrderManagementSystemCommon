package com.festiva.datastorage;

import java.util.ArrayList;
import java.util.List;

public class User {

    private long chatId;
    private List<Friend> friends;

    public User(long chatId) {
        this.chatId = chatId;
        this.friends = new ArrayList<>();
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public void addFriend(Friend friend) {
        this.friends.add(friend);
    }

    public void removeFriend(Friend friend) {
        this.friends.remove(friend);
    }
}