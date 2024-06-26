package pro.sky.telegrambot.service;

import pro.sky.telegrambot.entity.NotificationTask;

public interface MessageParseService {
    void parseMessage(Long chatId, String message);
}
