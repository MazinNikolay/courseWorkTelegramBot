package pro.sky.telegrambot.scheduler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;


//Класс с методом, работающим по рассписанию
@Component
@EnableScheduling
@RequiredArgsConstructor
public class NotificationTaskScheduler {

    private final NotificationTaskRepository repository;
    private final TelegramBot telegramBot;
    private final Logger logger = LoggerFactory.getLogger(NotificationTaskScheduler.class);

    //Интервал 1 минута
    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void scheduleTaskCheck() {
        //Округлаем текущее время до начала минуты
        LocalDateTime timeCurrent = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        //Поиск записей с совпадающей датой и временем
        List<NotificationTask> tasks = repository.findAllBySheduleDateTime(timeCurrent);
        //Вызов метода отправки сообщений
        tasks.forEach(e -> responseTask(e));
        //Удаление из таблицы отработанных сообщений
        tasks.forEach(e -> repository.delete(e));
    }

    //Метод отправки сообщений
    private void responseTask(NotificationTask task) {
        //Получение ID чата и текста сообщения из объекта
        Long chatId = task.getChatId();
        String taskMessage = task.getMessageText();
        //Отправка сообщения
        SendMessage message = new SendMessage(chatId, taskMessage);
        SendResponse response = telegramBot.execute(message);
        if (!response.isOk()) {
            logger.error("Response isn't correct. Error code: " + response.errorCode());
        }
    }
}
