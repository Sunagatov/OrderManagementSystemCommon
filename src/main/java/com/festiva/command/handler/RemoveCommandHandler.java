package com.festiva.command.handler;

import com.festiva.command.CommandHandler;
import com.festiva.datastorage.CustomDAO;
import com.festiva.state.BotState;
import com.festiva.state.UserStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class RemoveCommandHandler implements CommandHandler {

    private final CustomDAO dao;
    private final UserStateService userStateService;

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        Long telegramUserId = update.getMessage().getFrom().getId();
        String messageText = update.getMessage().getText().trim();

        SendMessage response = new SendMessage();
        response.setChatId(String.valueOf(chatId));
        response.setParseMode("HTML");

        // Если пользователь отправил только "/remove" – переводим его в режим ожидания ввода имени
        if (messageText.equals("/remove")) {
            userStateService.setState(telegramUserId, BotState.WAITING_FOR_REMOVE_FRIEND_INPUT);
            response.setText("<b><i>Введите имя пользователя, которого необходимо удалить:</i></b>");
            return response;
        } else {
            return handleAwaitingInput(update);
        }
    }

    /**
     * Обрабатывает ввод пользователя в состоянии WAITING_FOR_REMOVE_FRIEND_INPUT.
     * Ожидается, что пользователь введёт только имя друга.
     */
    public SendMessage handleAwaitingInput(Update update) {
        long chatId = update.getMessage().getChatId();
        Long telegramUserId = update.getMessage().getFrom().getId();
        String friendName = update.getMessage().getText().trim();

        SendMessage response = new SendMessage();
        response.setChatId(String.valueOf(chatId));
        response.setParseMode("HTML");

        if (!dao.friendExists(telegramUserId, friendName)) {
            response.setText("<b><i>Пользователь с именем \"" + friendName +
                    "\" не найден. Введите другое имя или отправьте /cancel для отмены.</i></b>");
            return response;
        }

        dao.deleteFriend(telegramUserId, friendName);
        response.setText("<b><i>Пользователь \"" + friendName + "\" успешно удалён!</i></b>");
        userStateService.clearState(telegramUserId);
        return response;
    }
}
