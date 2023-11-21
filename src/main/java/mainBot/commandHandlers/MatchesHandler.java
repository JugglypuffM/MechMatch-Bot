package mainBot.commandHandlers;

import database.models.User;
import mainBot.GlobalState;
import mainBot.LocalState;

import java.util.List;

/**
 * Matches case handler
 * Shows first ten profiles from users likes or dislikes list.
 * Pages are different depending on users reply(next/previous).
 * Data of each profile will be placed in separate array cell.
 * Offers to delete one profile by a number.
 */
public class MatchesHandler implements Handler{
    public void handleMessage(User sender, String[] reply, String message) {
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
}
