package pro.sky.telegrambot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.DeleteMyCommands;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//Доступ п геттерам и сеттерам с помощью Lombok
@Data
@Configuration
public class TelegramBotConfiguration {
    //Переменные из properties
    @Value("${telegram.bot.response.welcome}")
    private String welcome;

    @Value("${telegram.bot.response.error-format-task}")
    private String errorFormatTask;

    @Value("${telegram.bot.response.accept-task}")
    private String acceptTask;

    @Value("${telegram.bot.token}")
    private String token;

    //Создаем бот
    @Bean
    public TelegramBot telegramBot() {
        TelegramBot bot = new TelegramBot(token);
        bot.execute(new DeleteMyCommands());
        return bot;
    }
}
