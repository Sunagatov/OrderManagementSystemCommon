package com.festiva.bot;

import com.festiva.command.CommandRouter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class BirthdayBot extends TelegramLongPollingBot {

    private final CommandRouter commandRouter;
    private final Callback callbackQuery;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Override
    public void onUpdateReceived(Update update) {
        if (update == null) {
            log.warn("Received a null update.");
            return;
        }
        log.info("Update received: {}", update);
        try {
            if (update.hasCallbackQuery()) {
                log.info("Processing a callback query update.");
                processCallbackQuery(update);
            } else {
                log.info("Processing a message update.");
                processMessageUpdate(update);
            }
        } catch (Exception e) {
            log.error("Error processing update: {}", update, e);
        }
    }

    /**
     * Processes a callback query update.
     *
     */
    private void processCallbackQuery(Update update) throws Exception {
        EditMessageText editMessage = callbackQuery.handleCallbackQuery(update.getCallbackQuery());
        if (editMessage == null) {
            log.warn("Callback query did not produce a response.");
        } else {
            log.info("Sending response for callback query: {}", editMessage);
            execute(editMessage);
        }
    }

    /**
     * Processes a message update by routing the update and sending the result.
     *
     */
    private void processMessageUpdate(Update update) throws Exception {
        SendMessage response = commandRouter.route(update);
        if (response == null) {
            log.warn("No response generated from command router for update: {}", update);
        } else {
            log.info("Sending message response: {}", response);
            execute(response);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    /**
     * Convenience method to send a message externally.
     *
     */
    public void sendMessage(SendMessage message) {
        try {
            log.info("Sending message: {}", message);
            execute(message);
        } catch (Exception e) {
            log.error("Error while sending message: {}", message, e);
        }
    }
}
