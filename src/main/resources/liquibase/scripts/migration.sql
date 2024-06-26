-- liquibase formatted sql
-- changeset n_mazin:step_2_create_table_with_right_names_collumns
CREATE TABLE notification_tasks(
    key_task SERIAL,
    message_text TEXT NOT NULL,
    chat_id BIGINT NOT NULL,
    shedule_date_time TIMESTAMP NOT NULL
);