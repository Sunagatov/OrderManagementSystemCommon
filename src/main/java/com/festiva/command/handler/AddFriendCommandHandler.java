package com.festiva.command.handler;

import com.festiva.command.CommandHandler;
import com.festiva.datastorage.CustomDAO;
import com.festiva.datastorage.entity.Friend;
import com.festiva.state.BotState;
import com.festiva.state.UserStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class AddFriendCommandHandler implements CommandHandler {

    private final CustomDAO dao;
    private final UserStateService userStateService;

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        Long telegramUserId = update.getMessage().getFrom().getId();
        String text = update.getMessage().getText().trim();

        // Если команда ровно "/add" — начинаем процесс добавления и переводим пользователя в состояние ожидания.
        if (text.equals("/add")) {
            userStateService.setState(telegramUserId, BotState.WAITING_FOR_ADD_FRIEND_INPUT);
            SendMessage response = new SendMessage();
            response.setChatId(String.valueOf(chatId));
            response.setText("Введите имя и дату рождения следующим образом:\nИмя гггг-мм-дд");
            return response;
        } else {
            // Если текст не является командой, пробуем его обработать как ответ на запрос ввода.
            return handleAwaitingInput(update);
        }
    }

    /**
     * Обрабатывает ввод пользователя в состоянии ожидания данных для добавления друга.
     * Ожидается формат: "Имя гггг-мм-дд".
     */
    public SendMessage handleAwaitingInput(Update update) {
        long chatId = update.getMessage().getChatId();
        Long telegramUserId = update.getMessage().getFrom().getId();
        String messageText = update.getMessage().getText().trim();

        SendMessage response = new SendMessage();
        response.setChatId(String.valueOf(chatId));

        // Проверка формата ввода – ищем последний пробел для разделения имени и даты.
        int lastSpaceIndex = messageText.lastIndexOf(" ");
        if (lastSpaceIndex <= 0) {
            response.setText("Неверный формат. Используйте: Имя гггг-мм-дд");
            return response;
        }

        String name = messageText.substring(0, lastSpaceIndex).trim();
        String birthDateStr = messageText.substring(lastSpaceIndex + 1).trim();

        if (name.isEmpty()) {
            response.setText("Имя не может быть пустым.");
            return response;
        }

        LocalDate birthDate;
        try {
            birthDate = LocalDate.parse(birthDateStr);
        } catch (Exception e) {
            response.setText("Неверный формат даты. Используйте: гггг-мм-дд");
            return response;
        }

        if (birthDate.isAfter(LocalDate.now())) {
            response.setText("Дата рождения не может быть в будущем.");
            return response;
        }

        Friend friend = new Friend(name, birthDate);
        dao.addFriend(telegramUserId, friend);

        response.setText("Пользователь " + name + " успешно добавлен!");
        // Сбрасываем состояние после успешного добавления.
        userStateService.clearState(telegramUserId);
        return response;
    }
}
