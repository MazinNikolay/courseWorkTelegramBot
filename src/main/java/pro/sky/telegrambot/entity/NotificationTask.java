package pro.sky.telegrambot.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "notification_tasks")
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_task")
    private long keyTask;

    @Column(name = "message_text", nullable = false)
    private String messageText;

    @Column(name = "chat_id", nullable = false)
    private long chatId;

    @Column(name = "shedule_date_time", nullable = false)
    private LocalDateTime sheduleDateTime;

    public NotificationTask(String maessageText, long chatId, LocalDateTime sheduleDateTime) {
        this.messageText = maessageText;
        this.chatId = chatId;
        this.sheduleDateTime = sheduleDateTime;
    }
}
