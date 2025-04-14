package com.festiva.state;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserStateService {

    private final ConcurrentHashMap<Long, BotState> userStates = new ConcurrentHashMap<>();

    public BotState getState(Long userId) {
        return userStates.getOrDefault(userId, BotState.IDLE);
    }

    public void setState(Long userId, BotState state) {
        userStates.put(userId, state);
    }

    public void clearState(Long userId) {
        userStates.put(userId, BotState.IDLE);
    }
}
