package logic.handlers;

import bots.platforms.Platform;
import database.entities.Client;
import database.main.Database;
import database.entities.Account;
import database.entities.Profile;
import logic.states.GlobalState;
import logic.states.LocalState;
import logic.states.StateFSM;

/**
 * Profile editing procedure handler.
 * Fills the exact profile data field of given user depending on the user's choice
 */
public class EditHandler implements Handler{
    private final Database database;
    private final StateFSM stateFSM;
    public EditHandler(Database m_database, StateFSM m_stateFSM){
        this.database = m_database;
        this.stateFSM = m_stateFSM;
    }
    public void handleMessage(Account user, Profile profile, String[] reply, String message, Platform platform) {
        if (user.getLocalState() == LocalState.START) {
            if (!stateFSM.getStateDict().containsKey(message)) {
                reply[0] = "Напиши либо цифру соответствующую полю, либо название поля.";
                return;
            }
            user.setLocalState(stateFSM.getStateDict().get(message));
            reply[0] = stateFSM.getEditReplies().get(user.getLocalState());
        }
        else if (user.getLocalState() == LocalState.DELETE){
            if (message.equals(user.getPlatformUsername(platform)) ||
                    message.equals("@" + user.getPlatformUsername(platform))){
                reply[0] = "Профиль успешно удален.";
                database.updateClient(new Client(user.getPlatformId(platform), platform.stringRepresentation()));
                database.deleteAccount(user.getId());
                database.deleteProfile(user.getId());
                database.deleteAllConnectionsWith(user.getId());
                return;
            }
            reply[0] = "Введено неверное значение, процедура удаления прекращена.";
            user.setGlobalState(GlobalState.COMMAND);
            database.addToFPL(user.getId());
        }
        else {
            Boolean result = database.setField(profile , message);
            if (result == null){
                reply[0] = "Похоже ты не совсем честен по поводу пола.";
            }else if (result) {
                reply[0] = "Изменение внесено.";
                user.setGlobalState(GlobalState.COMMAND);
                database.addToFPL(user.getId());
                profile.setProfileFilled(true);
            }else {
                reply[0] = stateFSM.getWrongReplies().get(user.getLocalState());
            }
        }
        database.updateAccount(user);
        database.updateProfile(profile);
    }
}
