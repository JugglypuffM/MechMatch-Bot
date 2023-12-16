package logic.handlers;

import database.entities.Account;
import database.entities.Client;
import database.main.Database;
import logic.states.GlobalState;
import logic.states.LocalState;

public class AuthorizationHandler {
    Database database;
    public AuthorizationHandler(Database database){
        this.database = database;
    }

    /**
     * Method to handle authorization data.
     * @param client client from which the message was received
     * @param reply reply to user
     * @param message user message
     */
    public void handleData(Client client, String[] reply, String message){
        if (!client.isLoggedIn()){
            if ((message.equals("/cancel") || message.equals("Отменить")) && (client.getGlobalState() != GlobalState.COMMAND)){
                if ((client.getGlobalState() == GlobalState.SIGN_UP) && (client.getLogin() != null)){
                    database.deleteAccount(database.getAccountWithLogin(client.getLogin()).getId());
                }
                client.setLogin(null);
                client.setGlobalState(GlobalState.COMMAND);
                client.setLocalState(LocalState.LOGIN);
                reply[0] = "Процедура отменена.";
                return ;
            }
            switch (client.getGlobalState()){
                case COMMAND -> {
                    if (message.equals("/register") || message.equals("Зарегистрироваться")){
                        client.setGlobalState(GlobalState.SIGN_UP);
                        reply[0] = "Введи логин новой учетной записи.";
                    }
                    else if (message.equals("/login") || message.equals("Войти")){
                        client.setGlobalState(GlobalState.SIGN_IN);
                        reply[0] = "Введи логин твоей учетной записи.";
                    }
                    else {
                        reply[0] = """
                        Похоже ты еще не авторизован, поэтому тебе доступны только команды:
                            /login - войти в существующую учетную запись
                            /register - создать новую учетную запись
                            /cancel - доступна после отправки одной из вышеперечисленных команд, отменяет процедуру и возвращает обратно в это меню
                        """;
                    }
                    database.updateClient(client);
                }
                case SIGN_UP -> {
                    if (client.getLocalState() == LocalState.LOGIN){
                        if (database.getAccountWithLogin(message) != null){
                            reply[0] = "Учетная запись с таким логином уже существует, попробуй другой.";
                            return;
                        }
                        database.addAccount(message);
                        client.setLogin(message);
                        client.setLocalState(LocalState.PASSWORD);
                        database.updateClient(client);
                        reply[0] = "Теперь введи пароль для новой учетной записи.";
                    }
                    else {
                        Account account = database.getAccountWithLogin(client.getLogin());
                        account.setPasshash(message.hashCode()+"");
                        database.updateAccount(account);
                        client.setLoggedIn(true);
                        database.updateClient(client);
                        database.addProfile(account.getId());
                        reply[0] = "data" + client.getLogin();
                    }
                }
                case SIGN_IN -> {
                    if (client.getLocalState() == LocalState.LOGIN){
                        if (database.getAccountWithLogin(message) == null){
                            reply[0] = "Учетной записи с таким логином не существует, попробуй другой или создай новую.";
                            return ;
                        }
                        client.setLogin(message);
                        client.setLocalState(LocalState.PASSWORD);
                        database.updateClient(client);
                        reply[0] = "Теперь введи пароль от этой учетной записи.";
                    }
                    else {
                        Account account = database.getAccountWithLogin(client.getLogin());
                        if (!account.getPasshash().equals(message.hashCode()+"")){
                            reply[0] = "Неправильный пароль.";
                            return;
                        }
                        client.setLoggedIn(true);
                        database.updateClient(client);
                        reply[0] = "data" + client.getLogin();
                    }
                }
            }
        }
    }
}
