package logic.commandHandlers;

import database.main.Database;
import database.models.Profile;
import database.models.User;
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
    /**
     * Method to unify field filling.
     * Uses different setters depending on current local state.
     * @param value user's message
     * @return true if field was filled successfully and false if not
     */
    public Boolean setField(Integer id, String value){
        Profile profile = database.getProfile(id);
        switch (database.getUser(id).getLocalState()){
            case NAME:
                profile.setName(value);
                return true;
            case AGE:
                return profile.setAge(value);
            case SEX:
                return profile.setSex(value);
            case CITY:
                profile.setCity(value);
                return true;
            case ABOUT:
                profile.setInformation(value);
                return true;
            case EAGEMIN:
                return profile.setMinExpectedAge(value);
            case EAGEMAX:
                return profile.setMaxExpectedAge(value);
            case ESEX:
                return profile.setExpectedSex(value);
            case ECITY:
                profile.setExpectedCity(value);
                return true;
            case PHOTO:
                return false;
        }
        return false;
    }
    public void handleMessage(Integer id, String[] reply, String message) {
        User sender = database.getUser(id);
        Profile profile = database.getProfile(id);
        if (sender.getLocalState() == LocalState.START) {
            if (!stateFSM.getStateDict().containsKey(message)) {
                reply[0] = "Напиши либо цифру соответствующую полю, либо название поля.";
                return;
            }
            sender.setLocalState(stateFSM.getStateDict().get(message));
            reply[0] = stateFSM.getEditReplies().get(sender.getLocalState());
        }
        else if (sender.getLocalState() == LocalState.DELETE){
            if (message.equals(sender.getUsername()) || message.equals("@" + sender.getUsername())){
                reply[0] = "Профиль успешно удален.";
                database.deleteUser(id);
                database.deleteAllConnectionsWith(id);
                return;
            }
            reply[0] = "Введено неверное значение, процедура удаления прекращена.";
            sender.setGlobalState(GlobalState.COMMAND);
            database.addToFPL(id);
        }
        else {
            Boolean result = setField(id, message);
            if (result == null){
                reply[0] = "Похоже ты не совсем честен по поводу пола.";
            }else if (result) {
                reply[0] = "Изменение внесено.";
                sender.setGlobalState(GlobalState.COMMAND);
                database.addToFPL(id);
                profile.setProfileFilled(true);
            }else {
                reply[0] = stateFSM.getWrongReplies().get(sender.getLocalState());
            }
        }
    }
}
