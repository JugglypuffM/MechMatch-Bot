package logic.handlers;

import bots.platforms.Platform;
import database.entities.Profile;
import database.main.Database;
import database.entities.Account;
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
    public void handleMessage(Account user, Profile profile, String[] reply, String message, Platform platform) {
        List<Integer> friendLikes = new ArrayList<>();
        for (Integer i: database.getLikesOf(user.getSuggestedFriendID())){
            friendLikes.add(database.getConnection(i).getFriendID());
        }
        if (message.equalsIgnoreCase("да") || message.equals("❤️")){
            if (friendLikes.contains(user.getId())){
                String[] notification = new String[24];
                database.addConnection(user.getId(), user.getSuggestedFriendID(), true);
                notification[0] = "Ура! Тебе ответили взаимностью, можно переходить к общению.";
                notification[1] = database.getUserUsernames(user.getId());
                notificator.notifyFriend(user.getSuggestedFriendID(), notification);
                reply[0] = "Ура! Этот пользователь когда-то уже отвечал взаимностью, теперь вы можете перейти к общению.";
                reply[1] = database.getUserUsernames(user.getSuggestedFriendID());
            }
            else {
                String[] notification = new String[24];
                database.addConnection(user.getId(), user.getSuggestedFriendID(), true);
                database.addConnection(user.getSuggestedFriendID(), user.getId(), null);
                Account friend = database.getAccount(user.getSuggestedFriendID());
                friend.setGlobalState(GlobalState.PENDING);
                database.updateAccount(friend);
                notification[0] = "Твой профиль понравился кое-кому.";
                notification[1] = database.profileData(user.getId());
                notification[2] = "Напиши, хочешь ли ты начать общение с эти человеком(да/нет)?.";
                notification[13] = database.getProfile(user.getId()).getPhotoID();
                notificator.notifyFriend(user.getSuggestedFriendID(), notification);
                reply[0] = "Я уведомил этого пользователя, что он тебе приглянулся :)\nЕсли он ответит взаимностью, то вы сможете перейти к общению!";
            }
        }
        else if (message.equalsIgnoreCase("нет") || message.equals("\uD83D\uDC4E")){
            database.addConnection(user.getId(), user.getSuggestedFriendID(), false);
            reply[0] = "Очень жаль, в следующий раз постараюсь лучше :(";
        }
        else {
            reply[0] = "Введи да или нет.";
            return;
        }
        user.setSuggestedFriendID(null);
        user.setGlobalState(GlobalState.COMMAND);
    }
}
