package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.configuration.TelegramBotConfiguration;
import pro.sky.telegrambot.service.impl.MessageParseServiceImpl;

import javax.annotation.PostConstruct;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final TelegramBot telegramBot;
    private final MessageParseServiceImpl messageParseService;
    private final TelegramBotConfiguration config;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    //Получаем коллекцию записей чата и итерируемся по ней
    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            //Если команда содержит текст, то записываем ее текст и Id чата в переменные
            String inCommand = "";
            Long chatId = update.message().chat().id();
            if (update != null) {
                inCommand = update.message().text();
            }
            //Если команда /start, то отправляем приветственное сообщение
            if (inCommand.equals("/start")) {
                SendMessage helloMessage = new SendMessage(chatId, config.getWelcome());
                SendResponse response = telegramBot.execute(helloMessage);
                if (!response.isOk()) {
                    logger.error("Response isn't correct. Error code: " + response.errorCode());
                }
                //В противном случае парсим строку и проверяем на соответствие шаблону, сохраняем в БД
            } else {
                messageParseService.parseMessage(chatId, inCommand);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
