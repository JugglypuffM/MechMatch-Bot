package logic.handlers;

import bots.platforms.Platform;
import database.entities.Account;
import database.main.Database;
import database.main.DatabaseMock;
import logic.MessageProcessor;
import logic.states.GlobalState;
import logic.states.LocalState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class MatchesTests {
    /**
     * Instance of {@link Database}.
     * Uses mock database realization.
     */
    private Database database;
    /**
     * Instance of {@link MessageProcessor}.
     * Used to process all test messages.
     */
    private MatchesHandler handler;

    /**
     * Initialization of handler and database classes.
     */
    @BeforeEach
    public void initialize(){
        this.database = new DatabaseMock();
        this.handler = new MatchesHandler(this.database);
    }
    @Test
    public void pagesTest(){
        String[] reply = new String[24];
        database.addAccount("0");
        database.addProfile(0);
        Account account = database.getAccountWithLogin("0");
        account.setGlobalState(GlobalState.MATCHES);
        account.setLocalState(LocalState.CHOICE);
        account.setProfilesPage(1);
        database.updateAccount(account);
        for (int i = 1; i < 12; i++){
            database.addAccount(i+"");
            database.addProfile(i);
            database.getProfile(i).setName(i+"");
            database.addConnection(0, i, true);
        }
        handler.handleMessage(database.getAccountWithLogin("0"),
                database.getProfile(0), reply, "лайки", Platform.TELEGRAM);
        Assertions.assertEquals("Профиль 1:\n" +
                "Имя: 1\n" +
                "Возраст: 0\n" +
                "Пол: null\n" +
                "Город: null\n" +
                "Информация о себе: null\n" +
                "Диапазон возраста собеседника: 0 - 999\n" +
                "Пол собеседника: null\n" +
                "Город собеседника: null", reply[2]);
        Assertions.assertEquals("Профиль 10:\n" +
                "Имя: 10\n" +
                "Возраст: 0\n" +
                "Пол: null\n" +
                "Город: null\n" +
                "Информация о себе: null\n" +
                "Диапазон возраста собеседника: 0 - 999\n" +
                "Пол собеседника: null\n" +
                "Город собеседника: null", reply[11]);
        handler.handleMessage(database.getAccountWithLogin("0"),
                database.getProfile(0), reply, "далее", Platform.TELEGRAM);
        Assertions.assertEquals("Профиль 11:\n" +
                "Имя: 11\n" +
                "Возраст: 0\n" +
                "Пол: null\n" +
                "Город: null\n" +
                "Информация о себе: null\n" +
                "Диапазон возраста собеседника: 0 - 999\n" +
                "Пол собеседника: null\n" +
                "Город собеседника: null", reply[2]);
    }
}
