package com.festiva.handler;

import com.festiva.command.CommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class HelpCommandHandler implements CommandHandler {

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();

        String text = """
                Список команд:
                /list - список всех пользователей
                /add - добавить пользователя
                /remove - удалить пользователя
                /birthdays - дни рождения по месяцам
                /upcomingBirthdays - ближайшие дни рождения
                /jubilee - ближайшие юбилеи
                /help - список команд
                """;

        SendMessage response = new SendMessage();
        response.setChatId(String.valueOf(chatId));
        response.setText(text);

        return response;
    }
}
