package logic.handlers;

import bots.platforms.Platform;
import database.entities.Connection;
import database.entities.Profile;
import database.main.Database;
import database.entities.Account;
import logic.notificator.Notificator;
import logic.states.GlobalState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        if (message.equals("/stop") || message.equals("Остановить")){
            reply[0] = "Подбор собеседника прекращен.";
            user.setSuggestedFriendID(null);
        }
        List<Integer> friendLikes = new ArrayList<>();
        for (Integer i: database.getLikesOf(user.getSuggestedFriendID())){
            friendLikes.add(database.getConnection(i).getFriendID());
        }
        boolean connectionExists = false;
        Connection connection = null;
        for (Integer cid: database.getAllDeletedWith(user.getId())){
            connection = database.getConnection(cid);
            if(connection.getUserID().equals(user.getId()) &&
                    connection.getFriendID().equals(user.getSuggestedFriendID())){
                connectionExists = true;
                break;
            }
        }
        if (message.equalsIgnoreCase("да") || message.equals("❤️")){
            if (connectionExists){
                connection.setDeleted(false);
                connection.setIsLiked(true);
                database.updateConnection(connection);
                reply[0] = "Вы уже отвечали друг другу взаимностью, продолжайте общение!";
                reply[1] = database.getUserUsernames(user.getSuggestedFriendID());
            }
            else if (friendLikes.contains(user.getId())){
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
                notification[0] = "Твой профиль понравился кое-кому, проверь список пользователей, ожидающих ответа.";
                notificator.notifyFriend(user.getSuggestedFriendID(), notification);
                reply[0] = "Я уведомил этого пользователя, что он тебе приглянулся :)\n" +
                        "Если он ответит взаимностью, то вы сможете перейти к общению!";
            }
        }
        else if (message.equalsIgnoreCase("нет") || message.equals("\uD83D\uDC4E")){
            if (connectionExists){
                connection.setDeleted(false);
                connection.setIsLiked(false);
                database.updateConnection(connection);
            }
            else
                database.addConnection(user.getId(), user.getSuggestedFriendID(), false);
            reply[0] = "Очень жаль, в следующий раз постараюсь лучше :(";
        }
        else {
            reply[0] = "Введи да или нет.";
            return;
        }
        user.setSuggestedFriendID(null);
        Integer suggestedFriendId = database.getNewFriendId(user.getId());
        if (suggestedFriendId == -1){
            reply[1] = "Больше не нашлось никого, кто соответствует твоей уникальности ;(";
            user.setGlobalState(GlobalState.COMMAND);
        }
        else {
            Account friend = database.getAccount(suggestedFriendId);
            reply[1] = database.profileData(friend.getId());
            reply[2] = "Напиши, понравился ли тебе пользователь(да/нет).";
            reply[13] = database.getProfile(friend.getId()).getPhotoID();
            user.setSuggestedFriendID(friend.getId());
        }
    }
}
