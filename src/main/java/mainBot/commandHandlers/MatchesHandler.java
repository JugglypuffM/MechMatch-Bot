package mainBot.commandHandlers;

import database.main.Database;
import database.models.User;
import mainBot.states.GlobalState;
import mainBot.states.LocalState;

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
    private List<String> getIdList(User sender){
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
    private void getTenProfiles(User sender, String[] reply, List<String> idList){
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
    public void handleMessage(User sender, String[] reply, String message) {
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
                        if (database.getLikesOf(sender.getId()).isEmpty()) {
                            reply[0] = "Этот список пуст :(";
                            return;
                        }
                        sender.setProfilesList(message);
                    }case "дизлайки" ->{
                        if (database.getDislikesOf(sender.getId()).isEmpty()) {
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
                getTenProfiles(sender, reply, getIdList(sender));
                sender.setLocalState(LocalState.PROFILES);
            }
            case PROFILES -> {
                switch (message.toLowerCase()) {
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
                        }
                        getTenProfiles(sender, reply, getIdList(sender));
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
