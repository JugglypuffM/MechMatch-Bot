package logic.commandHandlers;

import database.main.Database;
import database.models.User;
import logic.states.GlobalState;
import logic.notificator.Notificator;

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
    public void handleMessage(User sender, String[] reply, String message) {
        List<String> friendLikes = new ArrayList<>();
        for (Integer i: database.getLikesOf(sender.getSuggestedFriendID())){
            friendLikes.add(database.getConnection(i).getFriendID());
        }
        if (message.equalsIgnoreCase("да") || message.equals("❤️")){
            if (friendLikes.contains(sender.getId())){
                String[] notification = new String[24];
                database.addConnection(sender.getId(), sender.getSuggestedFriendID(), true);
                notification[0] = "Ура! Тебе ответили взаимностью, можно переходить к общению.";
                notification[1] = "Вот ссылка на профиль собеседника - @" + sender.getUsername();
                notificator.notifyFriend(database.getUser(sender.getSuggestedFriendID()).getPlatform(), sender.getSuggestedFriendID(), database.getUser(sender.getSuggestedFriendID()).getUsername(), notification);
                reply[0] = "Ура! Этот пользователь когда-то уже отвечал взаимностью, теперь вы можете перейти к общению.";
                reply[1] = "Вот ссылка на профиль собеседника - @" + database.getUser(sender.getSuggestedFriendID()).getUsername();
            }
            else {
                String[] notification = new String[24];
                database.addConnection(sender.getId(), sender.getSuggestedFriendID(), true);
                database.addConnection(sender.getSuggestedFriendID(), sender.getId(), null);
                User friend = database.getUser(sender.getSuggestedFriendID());
                friend.setGlobalState(GlobalState.PENDING);
                database.updateUser(friend);
                notification[0] = "Твой профиль понравился кое-кому.";
                notification[1] = database.profileData(sender.getId());
                notification[2] = "Напиши, хочешь ли ты начать общение с эти человеком(да/нет)?.";
                notification[13] = sender.getPhotoID();
                notificator.notifyFriend(database.getUser(sender.getSuggestedFriendID()).getPlatform(), sender.getSuggestedFriendID(), database.getUser(sender.getSuggestedFriendID()).getUsername(), notification);
                reply[0] = "Я уведомил этого пользователя, что он тебе приглянулся :)\nЕсли он ответит взаимностью, то вы сможете перейти к общению!";
            }
        }
        else if (message.equalsIgnoreCase("нет") || message.equals("\uD83D\uDC4E")){
            database.addConnection(sender.getId(), sender.getSuggestedFriendID(), false);
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
