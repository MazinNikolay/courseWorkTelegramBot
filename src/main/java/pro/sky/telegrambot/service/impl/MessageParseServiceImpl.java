package pro.sky.telegrambot.service.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.MessageParseService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MessageParseServiceImpl implements MessageParseService {
    private static final Pattern patternMessage = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
    Logger logger = LoggerFactory.getLogger(MessageParseServiceImpl.class);

    @Autowired
    NotificationTaskRepository repository;

    @Autowired
    TelegramBot telegramBot;

    @Override
    public void parseMessage(Long chatId, String message) {
        if (message != null) {
            Matcher matcher = patternMessage.matcher(message);

            if (matcher.find()) {
                String dateTime = matcher.group(1);
                String task = matcher.group(3);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyy HH:mm");
                try {
                    LocalDateTime sheduleDateTime = LocalDateTime.parse(dateTime, formatter);
                    NotificationTask notificationTask = new NotificationTask(task, chatId, sheduleDateTime);
                    repository.save(notificationTask);
                    SendMessage acceptTaskMessage = new SendMessage(chatId, "Task accepted");
                    SendResponse response = telegramBot.execute(acceptTaskMessage);
                } catch (DateTimeParseException e) {
                    logger.error("Invalid date-time format");
                    SendMessage incorrectTaskMessageFormat = new SendMessage(chatId, "Try again input task in right format \"dd.MM.yyy HH:mm task text\"");
                    SendResponse response = telegramBot.execute(incorrectTaskMessageFormat);
                }

            }
        }
    }
}
