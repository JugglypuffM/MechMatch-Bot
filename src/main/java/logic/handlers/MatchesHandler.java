package logic.handlers;

import bots.platforms.Platform;
import database.entities.Profile;
import database.main.Database;
import database.entities.Account;
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
    private List<Integer> getIdList(Account user){
        List<Integer> idList = new ArrayList<>();
        if (user.getProfilesList().equalsIgnoreCase("лайки")){
            for (Integer i: database.getLikesOf(user.getId())){
                idList.add(database.getConnection(i).getFriendID());
            }
        }
        else{
            for (Integer i: database.getDislikesOf(user.getId())){
                idList.add(database.getConnection(i).getFriendID());
            }
        }
        return idList;
    }
    private void getTenProfiles(Integer id, String[] reply, List<Integer> idList){
        Account user = database.getAccount(id);
        int page = user.getProfilesPage()-1;
        reply[0] = "Профили на странице " + (page+1) + ":";
        for (int i = 0; i < 10; i++){
            if (i+page*10 < idList.size()){
                reply[2+i] = "Профиль " + (1+i+page*10) + ":\n" + database.profileData(idList.get(i+page*10));
                if (user.getProfilesList().equalsIgnoreCase("лайки")){
                    List<Integer> friendLikes = new ArrayList<>();
                    for (Integer j: database.getLikesOf(idList.get(i+page*10))){
                        friendLikes.add(database.getConnection(j).getFriendID());
                    }
                    if (friendLikes.contains(user.getId())){
                        reply[2+i] = reply[2+i] + "\n" + database.getUserUsernames(user.getId());
                    }
                }
                reply[14+i] = database.getProfile(idList.get(i+page*10)).getPhotoID();
            }else {
                break;
            }
        }
    }
    public void handleMessage(Account user, Profile profile, String[] reply, String message, Platform platform) {
        switch (user.getLocalState()){
            case CHOICE -> {
                switch (message.toLowerCase()){
                    case "выйти" ->{
                        reply[0] = "Процедура выбора списка отменена.";
                        user.setProfilesList(null);
                        user.setGlobalState(GlobalState.COMMAND);
                        return;
                    }
                    case "лайки" ->{
                        if (database.getLikesOf(user.getId()).isEmpty()) {
                            reply[0] = "Этот список пуст :(";
                            return;
                        }
                        user.setProfilesList(message);
                    }case "дизлайки" ->{
                        if (database.getDislikesOf(user.getId()).isEmpty()) {
                            reply[0] = "Этот список пуст :(";
                            return;
                        }
                        user.setProfilesList(message);
                    }
                    default -> {
                        reply[0] = "Такого списка нет, введи либо \"лайки\", либо \"дизлайки\". Или \"выйти\", если передумал.";
                        return;
                    }
                }

                user.setProfilesPage(1);
                getTenProfiles(user.getId(), reply, getIdList(user));
                user.setLocalState(LocalState.PROFILES);
            }
            case PROFILES -> {
                switch (message.toLowerCase()) {
                    default -> {
                        try {
                            int toDelete = Integer.parseInt(message);
                            List<Integer> connectionIDs;
                            if (user.getProfilesList().equalsIgnoreCase("лайки")) {
                                connectionIDs = database.getLikesOf(user.getId());
                            } else {
                                connectionIDs = database.getDislikesOf(user.getId());
                            }
                            if ((toDelete < 1) || (toDelete > connectionIDs.size())) {
                                reply[0] = "Нет профиля с таким номером.";
                                return;
                            }
                            toDelete = toDelete - 1;
                            database.deleteConnection(connectionIDs.get(toDelete));
                            reply[0] = "Профиль успешно удален из списка.";
                            user.setProfilesList(null);
                            user.setGlobalState(GlobalState.COMMAND);
                        } catch (NumberFormatException e) {
                            reply[0] = "Введи \"далее\" или \"назад\" для смены страниц, \"выйти\" для выхода или номер профиля, который хочешь удалить.";
                        }
                    }
                    case "далее", "назад" -> {
                        if (message.equalsIgnoreCase("далее")) {
                            if (user.getProfilesPage() < getIdList(user).size() / 10) {
                                user.setProfilesPage(user.getProfilesPage() + 1);
                            } else {
                                reply[0] = "Больше страниц нет.";
                                return;
                            }
                        } else {
                            if (user.getProfilesPage() == 1) {
                                reply[0] = "Это первая страница.";
                                return;
                            } else {
                                user.setProfilesPage(user.getProfilesPage() - 1);
                            }
                        }
                        getTenProfiles(user.getId(), reply, getIdList(user));
                    }
                    case "выйти" -> {
                        reply[0] = "Процедура изменения списка отменена.";
                        user.setProfilesList(null);
                        user.setGlobalState(GlobalState.COMMAND);
                    }
                }
            }
        }
    }
}
