package com.festiva.command.handler;

import com.festiva.command.CommandHandler;
import com.festiva.state.BotState;
import com.festiva.state.UserStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class CancelCommandHandler implements CommandHandler {

    private final UserStateService userStateService;

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        Long telegramUserId = update.getMessage().getFrom().getId();

        SendMessage response = new SendMessage();
        response.setChatId(String.valueOf(chatId));
        response.setParseMode("HTML");

        if (userStateService.getState(telegramUserId) == BotState.IDLE) {
            response.setText("<b><i>Нет активной команды для отмены. Я и так ничего не делал. Zzzzz...</i></b>");
        } else {
            userStateService.clearState(telegramUserId);
            response.setText("<b><i>Текущая команда отменена. Чем ещё могу помочь? Отправьте /help для списка команд.</i></b>");
        }
        return response;
    }
}
