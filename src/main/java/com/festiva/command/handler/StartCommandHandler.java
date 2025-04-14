package com.festiva.command.handler;

import com.festiva.command.CommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class StartCommandHandler implements CommandHandler {

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        String text = "<b>Добро пожаловать!</b>\n" +
                "Я бот для учета дней рождения. Я помогу вам управлять списком друзей и отслеживать их дни рождения.\n\n" +
                "<b>Основные команды:</b>\n" +
                "/start - Запуск бота и вывод этого сообщения\n" +
                "/help - Вывод списка команд\n\n" +
                "<b>Управление списком друзей:</b>\n" +
                "/list - Показать список друзей\n" +
                "/add - Добавить нового друга\n" +
                "/remove - Удалить существующего друга\n\n" +
                "<b>Просмотр дней рождения:</b>\n" +
                "/birthdays - Дни рождения по месяцам\n" +
                "/upcomingBirthdays - Ближайшие дни рождения\n" +
                "/jubilee - Юбилейные дни рождения\n\n" +
                "<b>Отмена операций:</b>\n" +
                "/cancel - Отмена текущей команды";

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setParseMode("HTML");
        message.setText(text);
        return message;
    }
}
