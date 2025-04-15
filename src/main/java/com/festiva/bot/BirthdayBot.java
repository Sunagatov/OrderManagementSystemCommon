package com.festiva.bot;

import com.festiva.command.CommandRouter;
import com.festiva.metrics.MetricsSender;
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
    private final MetricsSender metricsSender;

    public BirthdayBot(CommandRouter commandRouter,
                       Callback callbackQuery,
                       @Value("${telegram.bot.token}") String botToken,
                       MetricsSender metricsSender) {
        super(botToken);
        this.commandRouter = commandRouter;
        this.callbackQuery = callbackQuery;
        this.metricsSender = metricsSender;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update == null) {
            log.warn("Received a null update.");
            return;
        }

        final long startTime = System.currentTimeMillis();
        try {
            if (update.hasCallbackQuery()) {
                processCallbackQuery(update);
            } else if (update.hasMessage()) {
                processMessageUpdate(update);
            } else {
                log.warn("Update contains neither a message nor a callback query: {}", update);
            }
            final long duration = System.currentTimeMillis() - startTime;
            metricsSender.sendMetrics(update, "SUCCESS", duration);
        } catch (Exception e) {
            final long duration = System.currentTimeMillis() - startTime;
            metricsSender.sendMetrics(update, "ERROR", duration);
            log.error("Error processing update: {}", update, e);
        }
    }

    private void processCallbackQuery(Update update) throws Exception {
        EditMessageText editMessage = callbackQuery.handleCallbackQuery(update.getCallbackQuery());
        if (editMessage == null) {
            log.warn("Callback query did not produce a response.");
        } else {
            execute(editMessage);
        }
    }

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

    public void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (Exception e) {
            log.error("Error while sending message: {}", message, e);
        }
    }
}
