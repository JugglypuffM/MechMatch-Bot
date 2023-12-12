package logic.commandHandlers;

import database.main.Database;
import database.models.Account;
import database.models.Connection;
import database.models.Profile;
import database.models.User;
import logic.notificator.Notificator;
import logic.states.GlobalState;

import java.util.List;

/**
 * Pending users watch handler.
 * Decides whether to send usernames to both of user or write suggested user in the black list.
 */
public class PendingHandler implements Handler{
    private final Database database;
    private final Notificator notificator;
    public PendingHandler(Database m_database, Notificator m_notificator){
        this.database = m_database;
        this.notificator = m_notificator;
    }
    private String getUserUsernames(Integer id){
        String result = "";
        Account acc = database.getAccount(id);
        if (acc.getTgusermane() != null)
            result += "\nВот ссылка на телеграмм профиль собеседника - @" + acc.getTgusermane();
        if (acc.getDsusername() != null)
            result += "\nВот discord ник твоего собеседника - " + acc.getDsusername();
        return result;
    }
    public void handleMessage(Integer id, String[] reply, String message) {
        User sender = database.getUser(id);
        List<Integer> pending = database.getPendingOf(id);
        Connection connection = database.getConnection(pending.get(0));
        if (message.equalsIgnoreCase("да") || message.equals("❤️")){
            String[] notification = new String[24];
            connection.setIsLiked(true);
            database.updateConnection(connection);
            notification[0] = "Ура! Тебе ответили взаимностью, можно переходить к общению.";
            notification[1] = getUserUsernames(id);
            notificator.notifyFriend(connection.getFriendID(), notification);
            reply[0] = "Ура! Теперь вы можете перейти к общению.";
            reply[1] = getUserUsernames(sender.getSuggestedFriendID());
        }
        else if (message.equalsIgnoreCase("нет") || message.equals("\uD83D\uDC4E")){
            connection.setIsLiked(false);
            database.updateConnection(connection);
            reply[0] = "Хорошо, больше ты этого человека не увидишь. Если только не решишь удалить его из списка не понравившихся профилей.";
        }
        else {
            reply[0] = "Введи да или нет.";
            return;
        }
        sender.setGlobalState(GlobalState.COMMAND);
    }
}
