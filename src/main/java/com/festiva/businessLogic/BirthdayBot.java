package com.festiva.businessLogic;

import com.festiva.businessLogic.friend.FriendCreator;
import com.festiva.businessLogic.friend.FriendRemover;
import com.festiva.datastorage.CustomDAO;
import com.festiva.datastorage.Friend;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.logging.Logger;
import java.util.List;

public class BirthdayBot extends TelegramLongPollingBot {

    private static final String BOT_TOKEN = System.getenv("TELEGRAM_BOT_TOKEN");
    private static final String BOT_USERNAME = System.getenv("TELEGRAM_BOT_USERNAME");
    private static final Logger LOGGER = Logger.getLogger(BirthdayBot.class.getName());

    private final CustomDAO dao;
    private final BirthdateInfoProvider birthdateInfoProvider;
    private final FriendCreator friendCreator;
    private final FriendRemover friendRemover;
    private final MonthSelector monthSelector;
    private final Callback callbackQuery;

    public BirthdayBot(CustomDAO dao) {
        this.dao = dao;
        this.birthdateInfoProvider = new BirthdateInfoProvider();
        this.friendCreator = new FriendCreator(dao);
        this.friendRemover = new FriendRemover(dao);
        this.monthSelector = new MonthSelector();
        this.callbackQuery = new Callback(dao);
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                Message message = update.getMessage();
                String messageText = message.getText();
                long chatId = message.getChatId();
                Long telegramUserId = update.getMessage().getFrom().getId();

                if (messageText.equals("/start")) {
                    sendMessage(chatId, """
                            Привет! Я бот для учета дней рождения. Используй команды:
                            /list - список всех пользователей
                            /add - добавить пользователя
                            /remove - удалить пользователя
                            /birthdays - дни рождения по месяцам
                            /upcomingBirthdays - ближайшие дни рождения
                            /jubilee - ближайшие юбилеи
                            /help - список команд""");
                } else if (messageText.equals("/list")) {
                    List<Friend> friends = dao.getAllBySortedByDayMonth(telegramUserId);
                    if (friends.isEmpty()) {
                        sendMessage(chatId, "Список пользователей пуст.");
                    } else {
                        String response = birthdateInfoProvider.allFriendsInfo(friends);
                        sendMessage(chatId, response);
                    }
                } else if (messageText.equals("/add")) {
                    sendMessage(chatId, "Введите имя и дату рождения следующим образом : /add Имя гггг-мм-дд");

                } else if (messageText.startsWith("/add ")) {
                    SendMessage messageAddingUser = friendCreator.handleAddFriend(chatId, telegramUserId, messageText);
                    sendMessage(messageAddingUser);

                } else if (messageText.equals("/remove")) {
                    sendMessage(chatId, "Введите имя пользователя для удаления: /remove Имя");
                } else if (messageText.startsWith("/remove ")) {
                    SendMessage removeMessage = friendRemover.handleRemoveFriend(chatId, messageText);
                    sendMessage(removeMessage);
                } else if (messageText.equals("/birthdays")) {
                    SendMessage messageMonthSelection = monthSelector.sendMonthSelection(chatId);
                    execute(messageMonthSelection);
                } else if (messageText.equals("/upcomingBirthdays")) {
                    List<Friend> friends = dao.getAllBySortedByDayMonth(telegramUserId);
                    String response = birthdateInfoProvider.upcomingBirthdaysWithin30Days(friends);
                    sendMessage(chatId, response);

                } else if (messageText.equals("/jubilee")) {
                    List<Friend> friends = dao.getAllSortedByUpcomingBirthday(telegramUserId);
                    if (friends.isEmpty()) {
                        sendMessage(chatId, "Список пользователей пуст.");
                    } else {
                        String response = birthdateInfoProvider.birthDateJubilee(friends);
                        sendMessage(chatId, response);
                    }
                } else if (messageText.equals("/help")) {
                    sendMessage(chatId, """
                            Список команд:
                            /list - список всех пользователей
                            /add - добавить пользователя
                            /remove - удалить пользователя
                            /birthdays - дни рождения по месяцам
                            /upcomingBirthdays - ближайшие дни рождения
                            /jubilee - ближайшие юбилеи
                            /help - список команд""");
                } else {
                    sendMessage(chatId, "Неизвестная команда. Используй /help для списка команд.");
                }
            }
            if (update.hasCallbackQuery()) {
                EditMessageText editMessageText;
                editMessageText = callbackQuery.handleCallbackQuery(update.getCallbackQuery());
                if (editMessageText != null) {
                    editMessageText(editMessageText);
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Ошибка при обработке обновления: " + e.getMessage());
        }
    }

    private void editMessageText(EditMessageText editMessageText) {
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            LOGGER.severe("Error: " + e.getMessage());
        }
    }
    private void editMessageText(long chatId, int messageId, String newText) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(String.valueOf(chatId));
        editMessageText.setMessageId(messageId);
        editMessageText.setText(newText);
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            LOGGER.severe("Error: " + e.getMessage());
        }
    }

    public void sendMessage(SendMessage message) {
        try {
            super.execute(message);
        } catch (TelegramApiException e) {
            LOGGER.severe("Error: " + e.getMessage());
        }
    }

    public void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            super.execute(message);
        } catch (TelegramApiException e) {
            LOGGER.severe("Error: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onClosing() {
        super.onClosing();
    }
}