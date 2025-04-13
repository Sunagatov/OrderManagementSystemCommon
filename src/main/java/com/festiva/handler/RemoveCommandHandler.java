package com.festiva.handler;

import com.festiva.command.CommandHandler;
import com.festiva.friend.FriendRemover;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class RemoveCommandHandler implements CommandHandler {

    private final FriendRemover friendRemover;

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        return friendRemover.handleRemoveFriend(chatId, text);
    }
}
