package com.festiva.bot;

import com.festiva.command.CommandRouter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class BirthdayBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String botUsername;

    private final CommandRouter commandRouter;
    private final Callback callbackQuery;

    public BirthdayBot(CommandRouter commandRouter,
                       Callback callbackQuery,
                       @Value("${telegram.bot.token}") String botToken) {
        super(botToken);
        this.commandRouter = commandRouter;
        this.callbackQuery = callbackQuery;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update == null) {
            log.warn("Received a null update.");
            return;
        }
        try {
            if (update.hasCallbackQuery()) {
                processCallbackQuery(update);
            } else {
                processMessageUpdate(update);
            }
        } catch (Exception e) {
            log.error("Error processing update: {}", update, e);
        }
    }

    /**
     * Processes a callback query update.
     */
    private void processCallbackQuery(Update update) throws Exception {
        EditMessageText editMessage = callbackQuery.handleCallbackQuery(update.getCallbackQuery());
        if (editMessage == null) {
            log.warn("Callback query did not produce a response.");
        } else {
            execute(editMessage);
        }
    }

    /**
     * Processes a message update by routing the update and sending the result.
     */
    private void processMessageUpdate(Update update) throws Exception {
        SendMessage response = commandRouter.route(update);
        if (response == null) {
            log.warn("No response generated from command router for update: {}", update);
        } else {
            execute(response);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    /**
     * Convenience method to send a message externally.
     */
    public void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (Exception e) {
            log.error("Error while sending message: {}", message, e);
        }
    }
}
