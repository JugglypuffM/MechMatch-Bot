package logic.commandHandlers;

import database.main.Database;
import database.models.Account;
import database.models.User;
import logic.notificator.Notificator;
import logic.states.GlobalState;

import java.util.ArrayList;
import java.util.List;

/**
 * Matching procedure handler.
 * Decides whether to send notification to suggested friend or write him in the black list.
 * Method is synchronized to prevent situation when users like each other at once.
 */
public class MatchingHandler implements Handler{
    private final Database database;
    private final Notificator notificator;
    public MatchingHandler(Database m_database, Notificator m_notificator){
        this.database = m_database;
        this.notificator = m_notificator;
    }
    private String getUserUsernames(Integer id){
        String result = "";
        Account acc = database.getAccount(id);
        if (acc.getTgusername() != null)
            result += "\nВот ссылка на телеграмм профиль собеседника - @" + acc.getTgusername();
        if (acc.getDsusername() != null)
            result += "\nВот discord ник твоего собеседника - " + acc.getDsusername();
        return result;
    }
    public void handleMessage(Integer id, String[] reply, String message) {
        User sender = database.getUser(id);
        List<Integer> friendLikes = new ArrayList<>();
        for (Integer i: database.getLikesOf(sender.getSuggestedFriendID())){
            friendLikes.add(database.getConnection(i).getFriendID());
        }
        if (message.equalsIgnoreCase("да") || message.equals("❤️")){
            if (friendLikes.contains(id)){
                String[] notification = new String[24];
                database.addConnection(id, sender.getSuggestedFriendID(), true);
                notification[0] = "Ура! Тебе ответили взаимностью, можно переходить к общению.";
                notification[1] = getUserUsernames(id);
                notificator.notifyFriend(sender.getSuggestedFriendID(), notification);
                reply[0] = "Ура! Этот пользователь когда-то уже отвечал взаимностью, теперь вы можете перейти к общению.";
                reply[1] = getUserUsernames(sender.getSuggestedFriendID());
            }
            else {
                String[] notification = new String[24];
                database.addConnection(id, sender.getSuggestedFriendID(), true);
                database.addConnection(sender.getSuggestedFriendID(), id, null);
                User friend = database.getUser(sender.getSuggestedFriendID());
                friend.setGlobalState(GlobalState.PENDING);
                database.updateUser(friend);
                notification[0] = "Твой профиль понравился кое-кому.";
                notification[1] = database.profileData(id);
                notification[2] = "Напиши, хочешь ли ты начать общение с эти человеком(да/нет)?.";
                notification[13] = database.getProfile(id).getPhotoID();
                notificator.notifyFriend(sender.getSuggestedFriendID(), notification);
                reply[0] = "Я уведомил этого пользователя, что он тебе приглянулся :)\nЕсли он ответит взаимностью, то вы сможете перейти к общению!";
            }
        }
        else if (message.equalsIgnoreCase("нет") || message.equals("\uD83D\uDC4E")){
            database.addConnection(id, sender.getSuggestedFriendID(), false);
            reply[0] = "Очень жаль, в следующий раз постараюсь лучше :(";
        }
        else {
            reply[0] = "Введи да или нет.";
            return;
        }
        sender.setSuggestedFriendID(null);
        sender.setGlobalState(GlobalState.COMMAND);
    }
}
