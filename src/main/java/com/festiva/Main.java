package com.festiva;

import com.festiva.businessLogic.BirthdayBot;
import com.festiva.datastorage.CustomDAO;
import com.festiva.datastorage.mongo.MongoDAO;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {

    public static void main(String[] args) {
        try {

            CustomDAO dao = new MongoDAO();
            BirthdayBot bot = new BirthdayBot(dao);

            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);

            System.out.println("Бот запущен!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}