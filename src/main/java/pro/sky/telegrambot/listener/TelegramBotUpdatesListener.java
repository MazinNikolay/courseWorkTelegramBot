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

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            // Process your updates here
            String inCommand = "";
            Long chatId = update.message().chat().id();
            if (update != null) {
                inCommand = update.message().text();
            }
            if (inCommand.equals("/start")) {
                SendMessage helloMessage = new SendMessage(chatId, config.getWelcome());
                SendResponse response = telegramBot.execute(helloMessage);
                if (!response.isOk()) {
                    logger.error("Response isn't correct. Error code: " + response.errorCode());
                }
            }
            messageParseService.parseMessage(chatId, inCommand);
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
