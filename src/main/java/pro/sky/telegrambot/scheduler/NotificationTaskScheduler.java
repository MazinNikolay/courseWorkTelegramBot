package pro.sky.telegrambot.scheduler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class NotificationTaskScheduler {

    private final NotificationTaskRepository repository;
    private final TelegramBot telegramBot;

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void scheduleTaskCheck() {
        LocalDateTime timeCurrent = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> tasks = repository.findAllBySheduleDateTime(timeCurrent);
        tasks.forEach(e -> responseTask(e));
    }

    private void responseTask(NotificationTask task) {
        Long chatId = task.getChatId();
        String taskMessage = task.getMessageText();
        SendMessage message = new SendMessage(chatId, taskMessage);
        SendResponse response = telegramBot.execute(message);
    }
}
