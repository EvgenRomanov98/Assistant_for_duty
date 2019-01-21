package com.attendant.threads;

import com.attendant.model.ReminderEntity;
import com.attendant.telegramBotAssistant.TelegramBotAssistant;
import com.attendant.utils.UtilsDB;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.attendant.telegramBotAssistant.TelegramBotAssistant.*;

public class ReminderThread extends Thread {

    public ReminderThread(String name) {
        super(name);
    }

    @Override
    public void run() {
        while (true) {
            final int DAY_IN_MILLISECONDS = 86400000;

            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                Date date = simpleDateFormat.parse(simpleDateFormat.format(new Date()));

                String curDate = simpleDateFormat.format(date);
//            String oneDayBeforCurDay = simpleDateFormat.format(new Date(date.getTime() - 86400000));
//            String twoDayBeforCurDay = simpleDateFormat.format(new Date(date.getTime() - 172800000));

                /*
                 * 1. вытянуть с базы тех пользователей, у которых 2 дня до дежурства
                 * 2. вытянуть с базы у кого 1 день до дежурства
                 * 3. вытянуть с базы тех, у кого дежурство сегодня
                 * 4. отправить сообщение соответствующим пользователям про дежурство. */

                int i = 0; // нулевой индекс означает, что это текущая дата. 1 означает что это позавчера, индекс 2, поза-вчера.
                // Это достигается тем, что вконце цикла я отнимаю 1 сутки в милесекундах

                HashMap<Integer, ArrayList<ReminderEntity>> mapReminders = new HashMap<>();
                do {
                    ArrayList<ReminderEntity> reminders = new ArrayList<>(UtilsDB.getReminderGivenDate(curDate));
                    mapReminders.put(i, reminders);

                    i++;
                    curDate = simpleDateFormat.format(new Date(date.getTime() + DAY_IN_MILLISECONDS)); // прибавляю, что бы получить завтрашний день. делаю это 2 раза, что бы получить завтрашний и после-завтрашний дни.
                } while (i < 3);

                TelegramBotAssistant telegramBotAssistant = new TelegramBotAssistant();
                telegramBotAssistant.sendReminder(mapReminders); // отправить напоминание

                //sleep();

            } catch (ParseException e) {
                System.out.println("ReminderThread run() exception");
                e.printStackTrace();
            }

        }
    }
}