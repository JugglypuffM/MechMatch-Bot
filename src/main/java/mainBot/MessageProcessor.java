package mainBot;

import database.Database;
import database.models.Connection;
import database.models.User;
import mainBot.commandHandlers.CommandHandler;
import mainBot.commandHandlers.EditHandler;
import mainBot.commandHandlers.FillingHandler;

import java.util.ArrayList;
import java.util.List;

public class MessageProcessor {
    private final StateFSM stateFSM = new StateFSM();
    /**
     * Database interface.
     */
    private final Database database;
    private final Notificator notificator = new Notificator();
    private final CommandHandler caseCommand;
    private final FillingHandler caseProfileFill;
    private final EditHandler caseProfileEdit;
    public MessageProcessor(Database database){
        this.database = database;
        this.caseCommand = new CommandHandler(database);
        this.caseProfileFill = new FillingHandler(database, stateFSM);
        this.caseProfileEdit = new EditHandler(database, stateFSM);
    }
    public List<String> getIdList(User sender){
        List<String> idList = new ArrayList<>();
        if (sender.getProfilesList().equalsIgnoreCase("лайки")){
            for (Integer i: database.getLikesOf(sender.getId())){
                idList.add(database.getConnection(i).getFriendID());
            }
        }
        else{
            for (Integer i: database.getDislikesOf(sender.getId())){
                idList.add(database.getConnection(i).getFriendID());
            }
        }
        return idList;
    }
    public void getTenProfiles(User sender, String[] reply, List<String> idList){
        int page = sender.getProfilesPage()-1;
        reply[0] = "Профили на странице " + (page+1) + ":";
        for (int i = 0; i < 10; i++){
            if (i+page*10 < idList.size()){
                reply[2+i] = "Профиль " + (1+i+page*10) + ":\n" + database.profileData(idList.get(i+page*10));
                if (sender.getProfilesList().equalsIgnoreCase("лайки")){
                    List<String> friendLikes = new ArrayList<>();
                    for (Integer j: database.getLikesOf(idList.get(i+page*10))){
                        friendLikes.add(database.getConnection(j).getFriendID());
                    }
                    if (friendLikes.contains(sender.getId())){
                        reply[2+i] = reply[2+i] + "\nВот ссылка на профиль этого пользователя - @" + database.getUser(idList.get(i+page*10)).getUsername();
                    }
                }
                reply[14+i] = database.getUser(idList.get(i+page*10)).getPhotoID();
            }else {
                break;
            }
        }
    }
    /**
     * Matches case handler
     * Shows first ten profiles from users likes or dislikes list.
     * Pages are different depending on users reply(next/previous).
     * Data of each profile will be placed in separate array cell.
     * Offers to delete one profile by a number.
     * @param message user message
     * @param sender instance of {@link User} class representing a sender
     * @param reply array of strings with size of 12, where every string is a separate message
     */
    private void caseMatches(String message, User sender, String[] reply){
        switch (sender.getLocalState()){
            case CHOICE -> {
                if (message.equalsIgnoreCase("лайки")) {
                    if (database.getLikesOf(sender.getId()).isEmpty()) {
                        reply[0] = "Этот список пуст :(";
                        sender.setGlobalState(GlobalState.COMMAND);
                        return;
                    }
                    sender.setProfilesList(message);
                } else if (message.equalsIgnoreCase("дизлайки")) {
                    if (database.getDislikesOf(sender.getId()).isEmpty()) {
                        reply[0] = "Этот список пуст :(";
                        sender.setGlobalState(GlobalState.COMMAND);
                        return;
                    }
                    sender.setProfilesList(message);
                } else {
                    reply[0] = "Такого списка нет, введи либо \"лайки\", либо \"дизлайки\".";
                    return;
                }
                sender.setProfilesPage(1);
                getTenProfiles(sender, reply, getIdList(sender));
                sender.setLocalState(LocalState.PROFILES);
            }
            case PROFILES -> {
                switch (message) {
                    default -> {
                        try {
                            int toDelete = Integer.parseInt(message);
                            List<Integer> connectionIDs;
                            if (sender.getProfilesList().equalsIgnoreCase("лайки")) {
                                connectionIDs = database.getLikesOf(sender.getId());
                            } else {
                                connectionIDs = database.getDislikesOf(sender.getId());
                            }
                            if ((toDelete < 1) || (toDelete > connectionIDs.size())) {
                                reply[0] = "Нет профиля с таким номером.";
                                return;
                            }
                            toDelete = toDelete - 1;
                            database.deleteConnection(connectionIDs.get(toDelete));
                            reply[0] = "Профиль успешно удален из списка.";
                            sender.setProfilesList(null);
                            sender.setGlobalState(GlobalState.COMMAND);
                        } catch (NumberFormatException e) {
                            reply[0] = "Введи \"далее\" или \"назад\" для смены страниц, \"выйти\" для выхода или номер профиля, который хочешь удалить.";
                        }
                    }
                    case "далее", "назад" -> {
                        if (message.equalsIgnoreCase("далее")) {
                            if (sender.getProfilesPage() < getIdList(sender).size() / 10) {
                                sender.setProfilesPage(sender.getProfilesPage() + 1);
                            } else {
                                reply[0] = "Больше страниц нет.";
                                return;
                            }
                        } else {
                            if (sender.getProfilesPage() == 1) {
                                reply[0] = "Это первая страница.";
                                return;
                            } else {
                                sender.setProfilesPage(sender.getProfilesPage() - 1);
                            }
                            sender.setProfilesPage(sender.getProfilesPage() - 1);
                        }
                        getTenProfiles(sender, reply, getIdList(sender));
                    }
                    case "выйти" -> {
                        reply[0] = "Процедура изменения списка отменена.";
                        sender.setGlobalState(GlobalState.COMMAND);
                    }
                }
            }
        }
    }
    /**
     * Matching procedure handler.
     * Decides whether to send notification to suggested friend or write him in the black list.
     * Method is synchronized to prevent situation when users like each other at once.
     * @param id string representation of user id
     * @param message user message
     * @param sender instance of {@link User} class representing a sender
     * @param reply array of strings with size of 12, where every string is a separate message
     */
    private synchronized void caseMatching(String id, String message, User sender, String[] reply){
        if (database.getUser(sender.getSuggestedFriendID()).getSuggestedFriendID() != null){
            if (database.getUser(sender.getSuggestedFriendID()).getSuggestedFriendID().equals(sender.getId())){
                return;
            }
        }
        List<String> friendLikes = new ArrayList<>();
        for (Integer i: database.getLikesOf(sender.getSuggestedFriendID())){
            friendLikes.add(database.getConnection(i).getFriendID());
        }
        if (friendLikes.contains(id)){
            String[] notification = new String[24];
            database.addConnection(id, sender.getSuggestedFriendID(), true);
            notification[0] = "Ура! Тебе ответили взаимностью, можно переходить к общению.";
            notification[1] = "Вот ссылка на профиль собеседника - @" + sender.getUsername();
            notificator.notifyFriend(sender.getSuggestedFriendID(), database.getUser(sender.getSuggestedFriendID()).getUsername(), notification);
            reply[0] = "Ура! Этот пользователь когда-то уже отвечал взаимностью, теперь вы можете перейти к общению.";
            reply[1] = "Вот ссылка на профиль собеседника - @" + database.getUser(sender.getSuggestedFriendID()).getUsername();
        }
        else if (message.equalsIgnoreCase("да")){
            String[] notification = new String[24];
            database.addConnection(id, sender.getSuggestedFriendID(), true);
            database.addConnection(sender.getSuggestedFriendID(), id, null);
            User friend = database.getUser(sender.getSuggestedFriendID());
            friend.setGlobalState(GlobalState.PENDING);
            database.updateUser(friend);
            notification[0] = "Твой профиль понравился кое-кому.";
            notification[1] = database.profileData(id);
            notification[2] = "Напиши, хочешь ли ты начать общение с эти человеком(да/нет)?.";
            notification[13] = sender.getPhotoID();
            notificator.notifyFriend(sender.getSuggestedFriendID(), database.getUser(sender.getSuggestedFriendID()).getUsername(), notification);
            reply[0] = "Я уведомил этого пользователя, что он тебе приглянулся :)\nЕсли он ответит взаимностью, то вы сможете перейти к общению!";
        }
        else if (message.equalsIgnoreCase("нет")){
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
    /**
     * Pending users watch handler.
     * Decides whether to send usernames to both of user or write suggested user in the black list.
     * @param id string representation of user id
     * @param message user message
     * @param sender instance of {@link User} class representing a sender
     * @param reply array of strings with size of 12, where every string is a separate message
     */
    private void casePending(String id, String message, User sender, String[] reply){
        List<Integer> pending = database.getPendingOf(id);
        Connection connection = database.getConnection(pending.get(0));
        if (message.equalsIgnoreCase("да")){
            String[] notification = new String[24];
            connection.setIsLiked(true);
            database.updateConnection(connection);
            notification[0] = "Ура! Тебе ответили взаимностью, можно переходить к общению.";
            notification[1] = "Вот ссылка на профиль собеседника - @" + sender.getUsername();
            notificator.notifyFriend(connection.getFriendID(), database.getUser(connection.getFriendID()).getUsername(), notification);
            reply[0] = "Ура! Теперь вы можете перейти к общению.";
            reply[1] = "Вот ссылка на профиль собеседника - @" + database.getUser(connection.getFriendID()).getUsername();
        }
        else if (message.equalsIgnoreCase("нет")){
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
    /**
     * Main message processing method.
     * Initializes reply variable as an array of strings with size 12.
     * Every string in this array is a separate message, which will be sent further.
     * Checks if user with given id exists and creates new one if not.
     * @param id string presentation of user id
     * @param message user message
     * @return reply to user message
     */
    public String[] processMessage(String id, String message){
        String[] reply = new String[24];
        if (database.getUser(id) == null) {
            String username;
            if (message.startsWith("username")) {
                username = message.substring(8);
            } else {
                reply[0] = "требуется имя пользователя";
                return reply;
            }
            database.addUser(id, username);
        }
        User sender = database.getUser(id);
        switch (sender.getGlobalState()){
            case COMMAND -> caseCommand.handleMessage(sender, reply, message);
            case PROFILE_FILL -> caseProfileFill.handleMessage(sender, reply, message);
            case PROFILE_EDIT -> caseProfileEdit.handleMessage(sender, reply, message);
            case MATCHES -> caseMatches(message, sender, reply);
            case MATCHING -> caseMatching(id, message, sender, reply);
            case PENDING -> casePending(id, message, sender, reply);
        }
        database.updateUser(sender);
        return reply;
    }

    /**
     * Photo handler.
     * Asks to send a message if {@link LocalState} of user with given id is not {@link LocalState#PHOTO}.
     * If it is sets user's photoID with given photoID
     * @param id string presentation of user id
     * @param photoID id of picture, which is going to be user's profile photo
     * @return reply to user message
     */
    public String[] processPhoto(String id, String photoID){
        String[] reply = new String[24];
        User sender = database.getUser(id);
        if (sender.getLocalState() != LocalState.PHOTO){
            reply[0] = "Пожалуйста, отправь сообщение.";
            return reply;
        }
        sender.setPhotoID(photoID);
        sender.setLocalState(stateFSM.getNextDict().get(LocalState.PHOTO));
        if (sender.getGlobalState() == GlobalState.PROFILE_EDIT){
            reply[0] = "Изменение внесено.";
            sender.setGlobalState(GlobalState.COMMAND);
            database.addToFPL(id);
            sender.setProfileFilled(true);
        }
        else {
            reply[0] = stateFSM.getRightReplies().get(LocalState.PHOTO);
            reply[2] = database.profileData(id);
            reply[14] = sender.getPhotoID();
        }
        database.updateUser(sender);
        return reply;
    }
}
