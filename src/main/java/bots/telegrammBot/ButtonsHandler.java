package bots.telegrammBot;

import database.main.Database;
import logic.states.GlobalState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;

public class ButtonsHandler {
    private final Database database;
    private final ArrayList<KeyboardRow> commandKeyboard;
    private final ArrayList<KeyboardRow> sexKeyboard;
    private final ArrayList<KeyboardRow> esexKeyboard;
    private final ArrayList<KeyboardRow> fillStartKeyboard;
    private final ArrayList<KeyboardRow> editStartKeyboard;
    private final ArrayList<KeyboardRow> matchesChooseKeyboard;
    private final ArrayList<KeyboardRow> matchesProfilesKeyboard;
    private final ArrayList<KeyboardRow> likeKeyboard;
    private final ArrayList<KeyboardRow> simpleKeyboard;
    public ButtonsHandler(Database m_database){
        this.database = m_database;

        this.commandKeyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Мой профиль"));
        row1.add(new KeyboardButton("Подбор собеседника"));
        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Просмотренные профили"));
        row2.add(new KeyboardButton("Ожидающие ответа"));
        row2.add(new KeyboardButton("Описание команд"));
        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("Изменить профиль"));
        row3.add(new KeyboardButton("Заполнить заново"));
        row3.add(new KeyboardButton("Удалить профиль"));
        this.commandKeyboard.add(row1);
        this.commandKeyboard.add(row2);
        this.commandKeyboard.add(row3);

        this.sexKeyboard = new ArrayList<>();
        row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Парень"));
        row1.add(new KeyboardButton("Девушка"));
        this.sexKeyboard.add(row1);

        this.esexKeyboard = new ArrayList<>();
        row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Парень"));
        row1.add(new KeyboardButton("Девушка"));
        row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Без разницы"));
        this.esexKeyboard.add(row1);
        this.esexKeyboard.add(row2);

        this.fillStartKeyboard = new ArrayList<>();
        row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Хорошо"));
        this.fillStartKeyboard.add(row1);

        this.editStartKeyboard = new ArrayList<>();
        row1 = new KeyboardRow();
        row1.add(new KeyboardButton("1"));
        row1.add(new KeyboardButton("2"));
        row1.add(new KeyboardButton("3"));
        row1.add(new KeyboardButton("4"));
        row1.add(new KeyboardButton("5"));
        row2 = new KeyboardRow();
        row2.add(new KeyboardButton("6"));
        row2.add(new KeyboardButton("7"));
        row2.add(new KeyboardButton("8"));
        row2.add(new KeyboardButton("9"));
        row2.add(new KeyboardButton("10"));
        this.editStartKeyboard.add(row1);
        this.editStartKeyboard.add(row2);

        this.matchesChooseKeyboard = new ArrayList<>();
        row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Лайки"));
        row1.add(new KeyboardButton("Дизлайки"));
        row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Выйти"));
        this.matchesChooseKeyboard.add(row1);
        this.matchesChooseKeyboard.add(row2);

        this.matchesProfilesKeyboard = new ArrayList<>();
        row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Далее"));
        row1.add(new KeyboardButton("Назад"));
        row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Выйти"));
        this.matchesProfilesKeyboard.add(row1);
        this.matchesProfilesKeyboard.add(row2);

        this.likeKeyboard = new ArrayList<>();
        row1 = new KeyboardRow();
        row1.add(new KeyboardButton("❤️"));
        row1.add(new KeyboardButton("\uD83D\uDC4E"));
        this.likeKeyboard.add(row1);

        this.simpleKeyboard = new ArrayList<>();
        row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Да"));
        row1.add(new KeyboardButton("Нет"));
        this.simpleKeyboard.add(row1);
    }
    public void setKeyboard(String id, SendMessage sendMessage, SendPhoto sendPhoto){
        ReplyKeyboard replyKeyboard;
        switch (database.getUser(id).getGlobalState()){
            default -> replyKeyboard = new ReplyKeyboardRemove(true);
            case COMMAND -> replyKeyboard = new ReplyKeyboardMarkup(commandKeyboard);
            case PROFILE_FILL, PROFILE_EDIT -> {
                switch (database.getUser(id).getLocalState()){
                    default -> replyKeyboard = new ReplyKeyboardRemove(true);
                    case START -> {
                        if (database.getUser(id).getGlobalState() == GlobalState.PROFILE_FILL){
                            replyKeyboard = new ReplyKeyboardMarkup(fillStartKeyboard);
                        }else {
                            replyKeyboard = new ReplyKeyboardMarkup(editStartKeyboard);
                        }
                    }
                    case SEX -> replyKeyboard = new ReplyKeyboardMarkup(sexKeyboard);
                    case ESEX -> replyKeyboard = new ReplyKeyboardMarkup(esexKeyboard);
                    case FINISH -> replyKeyboard = new ReplyKeyboardMarkup(simpleKeyboard);
                }
            }
            case MATCHES -> {
                switch (database.getUser(id).getLocalState()){
                    default -> replyKeyboard = new ReplyKeyboardRemove();
                    case CHOICE -> replyKeyboard = new ReplyKeyboardMarkup(matchesChooseKeyboard);
                    case PROFILES -> replyKeyboard = new ReplyKeyboardMarkup(matchesProfilesKeyboard);
                }
            }
            case MATCHING, PENDING -> replyKeyboard = new ReplyKeyboardMarkup(likeKeyboard);
        }
        if (sendMessage == null){
            sendPhoto.setReplyMarkup(replyKeyboard);
        }else {
            sendMessage.setReplyMarkup(replyKeyboard);
        }
    }
}
