package pro.sky.telegrambot.service;

public interface MessageParseService {
    void parseMessage(Long chatId, String message);
}
