package org.nosov.fincost;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.Arrays;
import java.util.List;

public class Bot {
    private final TelegramBot bot = new TelegramBot(System.getenv("FIN_BOT_TOKEN"));
    private final List<String> users = Arrays.asList("nosov_ao", "nosova_eirene");
    private final CSV csv;

    public Bot() {
        this.csv = new CSV();
    }

    public void serve() {
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::process); // process updates
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            if (e.response() != null) {
                // god bad response from telegram
                e.response().errorCode();
                e.response().description();
            } else {
                // probably network error
                e.printStackTrace();
            }
        });
    }

    private void process(Update update) {
        Message message = update.message();
        InlineQuery inlineQuery = update.inlineQuery();

        BaseRequest request = null;
        if (users.contains(message.from().username())) {
            request = process(message);
        }
        if (request != null) {
            bot.execute(request);
        }
    }

    private BaseRequest process(Message message) {
        BaseRequest request = null;
        if (message.text().startsWith("/start")) {
            request = new SendMessage(message.chat().id(), "Используйте такой формат");
        } else if (message.text().startsWith("/add")) {
            String msg = add(message) ? "Трата добавленна" : "Ошибка при добавлении траты";
            request = new SendMessage(message.chat().id(), msg);
        } else if (message.text().startsWith("/read")) {
            List<String[]> list = csv.readCSVFile();
            StringBuilder stringBuilder = new StringBuilder();
            list.forEach(row -> stringBuilder.append(Arrays.toString(row)).append("</br>"));
            request = new SendMessage(message.chat().id(), stringBuilder.toString());
        } else if (message.text().startsWith("/exit")) {
            System.exit(0);
        } else {
            request = new SendMessage(message.chat().id(), "Не известная команда");
        }
        return request;
    }

    private boolean add(Message message) {
        String text = message.text();
        String[] s = text.split(" ");
        if (s.length != 3) {
            System.out.println(text);
            return false;
        }

        String time = String.valueOf(System.currentTimeMillis());
        String name = message.from().username();
        String category = s[1];
        String cost = s[2];

        final char separator = ';';

        StringBuilder stringBuilder = new StringBuilder()
                .append(time).append(separator)
                .append(name).append(separator)
                .append(category).append(separator)
                .append(cost);
        csv.writeToCsv(stringBuilder.toString());
        return true;
    }

}
