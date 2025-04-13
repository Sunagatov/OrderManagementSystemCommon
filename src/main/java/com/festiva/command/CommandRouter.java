package com.festiva.command;

import com.festiva.handler.AddFriendCommandHandler;
import com.festiva.handler.BirthdaysCommandHandler;
import com.festiva.handler.DefaultCommandHandler;
import com.festiva.handler.HelpCommandHandler;
import com.festiva.handler.JubileeCommandHandler;
import com.festiva.handler.ListCommandHandler;
import com.festiva.handler.RemoveCommandHandler;
import com.festiva.handler.StartCommandHandler;
import com.festiva.handler.UpcomingBirthdaysCommandHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CommandRouter {

    // This map will hold the command strings and their corresponding handlers
    private final Map<String, CommandHandler> handlers = new HashMap<>();

    // Command handlers
    private final DefaultCommandHandler defaultHandler;
    private final StartCommandHandler startCommandHandler;
    private final ListCommandHandler listCommandHandler;
    private final AddFriendCommandHandler addFriendCommandHandler;
    private final RemoveCommandHandler removeCommandHandler;
    private final BirthdaysCommandHandler birthdaysCommandHandler;
    private final UpcomingBirthdaysCommandHandler upcomingBirthdaysCommandHandler;
    private final JubileeCommandHandler jubileeCommandHandler;
    private final HelpCommandHandler helpCommandHandler;

    @PostConstruct
    public void init() {
        handlers.put("/start", startCommandHandler);
        handlers.put("/list", listCommandHandler);
        handlers.put("/add", addFriendCommandHandler);
        handlers.put("/remove", removeCommandHandler);
        handlers.put("/birthdays", birthdaysCommandHandler);
        handlers.put("/upcomingBirthdays", upcomingBirthdaysCommandHandler);
        handlers.put("/jubilee", jubileeCommandHandler);
        handlers.put("/help", helpCommandHandler);
    }

    public SendMessage route(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return null;
        }
        String text = update.getMessage().getText().trim();
        String command = text.split(" ")[0];
        CommandHandler handler = handlers.getOrDefault(command, defaultHandler);
        return handler.handle(update);
    }
}
