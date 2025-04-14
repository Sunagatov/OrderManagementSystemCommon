package com.festiva.command;

import com.festiva.command.handler.AddFriendCommandHandler;
import com.festiva.command.handler.BirthdaysCommandHandler;
import com.festiva.command.handler.CancelCommandHandler;
import com.festiva.command.handler.DefaultCommandHandler;
import com.festiva.command.handler.HelpCommandHandler;
import com.festiva.command.handler.JubileeCommandHandler;
import com.festiva.command.handler.ListCommandHandler;
import com.festiva.command.handler.RemoveCommandHandler;
import com.festiva.command.handler.StartCommandHandler;
import com.festiva.command.handler.UpcomingBirthdaysCommandHandler;
import com.festiva.state.BotState;
import com.festiva.state.UserStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class CommandRouter {
    private final UserStateService userStateService;
    private final DefaultCommandHandler defaultHandler;
    private final StartCommandHandler startCommandHandler;
    private final ListCommandHandler listCommandHandler;
    private final AddFriendCommandHandler addFriendCommandHandler;
    private final RemoveCommandHandler removeCommandHandler;
    private final BirthdaysCommandHandler birthdaysCommandHandler;
    private final UpcomingBirthdaysCommandHandler upcomingBirthdaysCommandHandler;
    private final JubileeCommandHandler jubileeCommandHandler;
    private final HelpCommandHandler helpCommandHandler;
    private final CancelCommandHandler cancelCommandHandler;

    public SendMessage route(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return null;
        }

        Long telegramUserId = update.getMessage().getFrom().getId();
        String text = update.getMessage().getText().trim();

        // Извлекаем команду (первое слово)
        String command = text.split(" ")[0];
        BotState state = userStateService.getState(telegramUserId);

        // Если команда /cancel всегда обрабатывается отдельным хэндлером
        if ("/cancel".equals(command)) {
            return cancelCommandHandler.handle(update);
        }

        // Обработка многошаговых состояний пользователя
        return switch (state) {
            case WAITING_FOR_ADD_FRIEND_INPUT -> addFriendCommandHandler.handleAwaitingInput(update);
            case WAITING_FOR_REMOVE_FRIEND_INPUT -> removeCommandHandler.handleAwaitingInput(update);
            default ->
                // Если пользователь в состоянии IDLE, выбираем обработчик по введённой команде
                    switch (command) {
                        case "/start" -> startCommandHandler.handle(update);
                        case "/list" -> listCommandHandler.handle(update);
                        case "/add" -> addFriendCommandHandler.handle(update);
                        case "/remove" -> removeCommandHandler.handle(update);
                        case "/birthdays" -> birthdaysCommandHandler.handle(update);
                        case "/upcomingbirthdays" -> upcomingBirthdaysCommandHandler.handle(update);
                        case "/jubilee" -> jubileeCommandHandler.handle(update);
                        case "/help" -> helpCommandHandler.handle(update);
                        default -> defaultHandler.handle(update);
                    };
        };
    }
}
