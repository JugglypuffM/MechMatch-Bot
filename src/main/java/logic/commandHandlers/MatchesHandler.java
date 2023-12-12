package logic.commandHandlers;

import database.main.Database;
import database.models.Account;
import database.models.User;
import logic.states.GlobalState;
import logic.states.LocalState;

import java.util.ArrayList;
import java.util.List;

/**
 * Matches case handler
 * Shows first ten profiles from users likes or dislikes list.
 * Pages are different depending on users reply(next/previous).
 * Data of each profile will be placed in separate array cell.
 * Offers to delete one profile by a number.
 */
public class MatchesHandler implements Handler{
    private final Database database;
    public MatchesHandler(Database m_database){
        this.database = m_database;
    }
    private List<Integer> getIdList(User sender){
        List<Integer> idList = new ArrayList<>();
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
    private String getUserUsernames(Integer id){
        String result = "";
        Account acc = database.getAccount(id);
        if (acc.getTgusername() != null)
            result += "\nВот ссылка на телеграмм профиль этого пользователя - @" + acc.getTgusername();
        if (acc.getDsusername() != null)
            result += "\nВот discord ник этого пользователя - " + acc.getDsusername();
        return result;
    }
    private void getTenProfiles(Integer id, String[] reply, List<Integer> idList){
        User sender = database.getUser(id);
        int page = sender.getProfilesPage()-1;
        reply[0] = "Профили на странице " + (page+1) + ":";
        for (int i = 0; i < 10; i++){
            if (i+page*10 < idList.size()){
                reply[2+i] = "Профиль " + (1+i+page*10) + ":\n" + database.profileData(idList.get(i+page*10));
                if (sender.getProfilesList().equalsIgnoreCase("лайки")){
                    List<Integer> friendLikes = new ArrayList<>();
                    for (Integer j: database.getLikesOf(idList.get(i+page*10))){
                        friendLikes.add(database.getConnection(j).getFriendID());
                    }
                    if (friendLikes.contains(id)){
                        reply[2+i] = reply[2+i] + getUserUsernames(id);
                    }
                }
                reply[14+i] = database.getProfile(idList.get(i+page*10)).getPhotoID();
            }else {
                break;
            }
        }
    }
    public void handleMessage(Integer id, String[] reply, String message) {
        User sender = database.getUser(id);
        switch (sender.getLocalState()){
            case CHOICE -> {
                switch (message.toLowerCase()){
                    case "выйти" ->{
                        reply[0] = "Процедура выбора списка отменена.";
                        sender.setProfilesList(null);
                        sender.setGlobalState(GlobalState.COMMAND);
                        return;
                    }
                    case "лайки" ->{
                        if (database.getLikesOf(id).isEmpty()) {
                            reply[0] = "Этот список пуст :(";
                            return;
                        }
                        sender.setProfilesList(message);
                    }case "дизлайки" ->{
                        if (database.getDislikesOf(id).isEmpty()) {
                            reply[0] = "Этот список пуст :(";
                            return;
                        }
                        sender.setProfilesList(message);
                    }
                    default -> {
                        reply[0] = "Такого списка нет, введи либо \"лайки\", либо \"дизлайки\". Или \"выйти\", если передумал.";
                        return;
                    }
                }

                sender.setProfilesPage(1);
                getTenProfiles(id, reply, getIdList(sender));
                sender.setLocalState(LocalState.PROFILES);
            }
            case PROFILES -> {
                switch (message.toLowerCase()) {
                    default -> {
                        try {
                            int toDelete = Integer.parseInt(message);
                            List<Integer> connectionIDs;
                            if (sender.getProfilesList().equalsIgnoreCase("лайки")) {
                                connectionIDs = database.getLikesOf(id);
                            } else {
                                connectionIDs = database.getDislikesOf(id);
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
                        }
                        getTenProfiles(id, reply, getIdList(sender));
                    }
                    case "выйти" -> {
                        reply[0] = "Процедура изменения списка отменена.";
                        sender.setProfilesList(null);
                        sender.setGlobalState(GlobalState.COMMAND);
                    }
                }
            }
        }
    }
}
