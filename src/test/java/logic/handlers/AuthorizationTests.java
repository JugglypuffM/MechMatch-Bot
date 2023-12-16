package logic.handlers;

import database.entities.Account;
import database.entities.Client;
import database.main.Database;
import database.main.DatabaseMock;
import logic.MessageProcessor;
import logic.states.GlobalState;
import logic.states.LocalState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AuthorizationTests {
    /**
     * Instance of {@link Database}.
     * Uses mock database realization.
     */
    private Database database;
    /**
     * Instance of {@link MessageProcessor}.
     * Used to process all test messages.
     */
    private AuthorizationHandler handler;
    Client client1 = new Client("0", "TELEGRAM");
    Client client2 = new Client("1", "DISCORD");

    /**
     * Initialization of handler and database classes.
     */
    @BeforeEach
    public void initialize(){
        this.database = new DatabaseMock();
        this.handler = new AuthorizationHandler(this.database);
    }

    /**
     * Authorization procedure test
     */
    @Test
    public void loginTest(){
        String[] reply = new String[24];
        database.addAccount("login");
        Account account = database.getAccountWithLogin("login");
        account.setPasshash("1234".hashCode()+"");
        account.setPlatformId(client1.getPlatformId(), client1.getPlatform());
        account.setPlatformUsername("stas", client1.getPlatform());
        handler.handleData(client2, reply, "/login");
        Assertions.assertEquals("Введи логин твоей учетной записи.", reply[0]);
        handler.handleData(client2, reply, "login");
        Assertions.assertEquals("Теперь введи пароль от этой учетной записи.", reply[0]);
        handler.handleData(client2, reply, "1234");
        Assertions.assertEquals("datalogin", reply[0]);
    }
    /**
     * Registration procedure test
     */
    @Test
    public void registrationTest(){
        String[] reply = new String[24];
        Assertions.assertNull(database.getAccountWithLogin("login"));
        handler.handleData(client1, reply, "/register");
        Assertions.assertEquals("Введи логин новой учетной записи.", reply[0]);
        handler.handleData(client1, reply, "login");
        Assertions.assertEquals("Теперь введи пароль для новой учетной записи.", reply[0]);
        handler.handleData(client1, reply, "1234");
        Assertions.assertEquals("datalogin", reply[0]);
        Assertions.assertNotNull(database.getAccountWithLogin("login"));
        Assertions.assertEquals(database.getAccountWithLogin("login").getPasshash(), "1234".hashCode()+"");
    }

    /**
     * Test for wrong login case in authorization procedure
     */
    @Test
    public void wrongLoginTest(){
        String[] reply = new String[24];
        handler.handleData(client1, reply, "dadsf");
        Assertions.assertEquals("""
                        Похоже ты еще не авторизован, поэтому тебе доступны только команды:
                            /login - войти в существующую учетную запись
                            /register - создать новую учетную запись
                            /cancel - доступна после отправки одной из вышеперечисленных команд, отменяет процедуру и возвращает обратно в это меню
                        """,
                        reply[0]);
        handler.handleData(client1, reply, "/login");
        handler.handleData(client1, reply, "dfgsdfg");
        Assertions.assertEquals("Учетной записи с таким логином не существует, попробуй другой или создай новую.", reply[0]);
    }

    /**
     * Test for wrong password case in authorization procedure
     */
    @Test
    public void wrongPasswordTest(){
        String[] reply = new String[24];
        database.addAccount("login");
        Account account = database.getAccountWithLogin("login");
        account.setPasshash("1234".hashCode()+"");
        account.setPlatformId(client1.getPlatformId(), client1.getPlatform());
        account.setPlatformUsername("stas", client1.getPlatform());
        handler.handleData(client1, reply, "/login");
        handler.handleData(client1, reply, "login");
        handler.handleData(client1, reply, "123");
        Assertions.assertEquals("Неправильный пароль.", reply[0]);
    }

    /**
     * Test for authorization procedure cancellation case
     */
    @Test
    public void loginCancelTest(){
        String[] reply = new String[24];
        database.addAccount("login");
        Account account = database.getAccountWithLogin("login");
        account.setPasshash("1234".hashCode()+"");
        account.setPlatformId(client1.getPlatformId(), client1.getPlatform());
        account.setPlatformUsername("stas", client1.getPlatform());
        handler.handleData(client1, reply, "/login");
        handler.handleData(client1, reply, "login");
        handler.handleData(client1, reply, "/cancel");
        Assertions.assertEquals("Процедура отменена.", reply[0]);
        handler.handleData(client1, reply, "dadsf");
        Assertions.assertEquals("""
                        Похоже ты еще не авторизован, поэтому тебе доступны только команды:
                            /login - войти в существующую учетную запись
                            /register - создать новую учетную запись
                            /cancel - доступна после отправки одной из вышеперечисленных команд, отменяет процедуру и возвращает обратно в это меню
                        """,
                reply[0]);
        Assertions.assertNull(client1.getLogin());
        Assertions.assertEquals(client1.getGlobalState(), GlobalState.COMMAND);
        Assertions.assertEquals(client1.getLocalState(), LocalState.LOGIN);
    }
    /**
     * Test for registration procedure cancellation case
     */
    @Test
    public void registrationCancelTest(){
        String[] reply = new String[24];
        handler.handleData(client1, reply, "/register");
        Assertions.assertEquals("Введи логин новой учетной записи.", reply[0]);
        handler.handleData(client1, reply, "login1");
        Assertions.assertEquals("Теперь введи пароль для новой учетной записи.", reply[0]);
        handler.handleData(client1, reply, "/cancel");
        Assertions.assertEquals("Процедура отменена.", reply[0]);
        handler.handleData(client1, reply, "dadsf");
        Assertions.assertEquals("""
                        Похоже ты еще не авторизован, поэтому тебе доступны только команды:
                            /login - войти в существующую учетную запись
                            /register - создать новую учетную запись
                            /cancel - доступна после отправки одной из вышеперечисленных команд, отменяет процедуру и возвращает обратно в это меню
                        """,
                reply[0]);
        Assertions.assertNull(client1.getLogin());
        Assertions.assertEquals(client1.getGlobalState(), GlobalState.COMMAND);
        Assertions.assertEquals(client1.getLocalState(), LocalState.LOGIN);
    }
}
