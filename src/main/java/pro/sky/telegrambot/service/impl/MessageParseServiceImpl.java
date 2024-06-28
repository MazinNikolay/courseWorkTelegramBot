package pro.sky.telegrambot.service.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.configuration.TelegramBotConfiguration;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.MessageParseService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Сервис обработки сообщений
@RequiredArgsConstructor
@Service
public class MessageParseServiceImpl implements MessageParseService {
    //Шаблон для сравнения сообщения
    private static final Pattern patternMessage = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
    private final Logger logger = LoggerFactory.getLogger(MessageParseServiceImpl.class);
    private final TelegramBotConfiguration config;
    private final NotificationTaskRepository repository;
    private final TelegramBot telegramBot;

    @Override
    public void parseMessage(Long chatId, String message) {
        if (message != null) {
            //Сравниваем текст сообщения с шаблоном
            Matcher matcher = patternMessage.matcher(message);
            //Если подходит:
            if (matcher.find()) {
                //Присваеваем переменным соответствующие группы из matcher
                String dateTime = matcher.group(1);
                String task = matcher.group(3);
                //создаем шаблон для преобразования текста в LocalDateTime
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyy HH:mm");
                try {
                    //Преобразование текста в дату и время с обработкой ошибки, так как возможен выброс исключения
                    LocalDateTime sheduleDateTime = LocalDateTime.parse(dateTime, formatter);
                    NotificationTask notificationTask = new NotificationTask(task, chatId, sheduleDateTime);
                    //Сохраняем в базу данных
                    repository.save(notificationTask);
                    //Сообщение о принятии задания
                    SendMessage acceptTaskMessage = new SendMessage(chatId, config.getAcceptTask());
                    SendResponse response = telegramBot.execute(acceptTaskMessage);
                    if (!response.isOk()) {
                        logger.error("Response isn't correct. Error code: " + response.errorCode());
                    }
                } catch (DateTimeParseException e) {
                    //Вывод сообщения и запись в лог, что формат записи не соответствует
                    logger.error("Invalid date-time format");
                    SendMessage incorrectTaskMessageFormat = new SendMessage(chatId, config.getErrorFormatTask());
                    SendResponse response = telegramBot.execute(incorrectTaskMessageFormat);
                    if (!response.isOk()) {
                        logger.error("Response isn't correct. Error code: " + response.errorCode());
                    }
                }
            }
        }
    }
}
