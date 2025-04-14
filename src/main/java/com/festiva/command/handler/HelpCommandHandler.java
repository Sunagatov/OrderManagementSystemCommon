package com.festiva.command.handler;

import com.festiva.command.CommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class HelpCommandHandler implements CommandHandler {

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        String text = "<b>Список доступных команд:</b>\n\n" +
                "<b>Основные команды:</b>\n" +
                "/start - Запуск бота и вывод приветственного сообщения\n" +
                "/help - Вывод этого списка команд\n\n" +
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

        SendMessage response = new SendMessage();
        response.setChatId(String.valueOf(chatId));
        response.setParseMode("HTML");
        response.setText(text);
        return response;
    }
}
