package pro.sky.telegrambot.service;

//Интерфейс для сервиса обработки сообщений
public interface MessageParseService {
    void parseMessage(Long chatId, String message);
}
