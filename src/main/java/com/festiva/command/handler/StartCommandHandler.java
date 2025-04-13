package com.festiva.command.handler;

import com.festiva.command.CommandHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
@RequiredArgsConstructor
public class StartCommandHandler implements CommandHandler {

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        String text = """
                Привет! Я бот для учета дней рождения. Используй команды:
                /list - список всех пользователей
                /add - добавить пользователя
                /remove - удалить пользователя
                /birthdays - дни рождения по месяцам
                /upcomingBirthdays - ближайшие дни рождения
                /jubilee - ближайшие юбилеи
                /help - список команд
                """;

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        return message;
    }
}
