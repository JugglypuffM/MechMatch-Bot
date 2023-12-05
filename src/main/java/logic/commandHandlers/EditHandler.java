package logic.commandHandlers;

import database.main.Database;
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
    public void handleMessage(User sender, String[] reply, String message) {
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
                database.deleteUser(sender.getId());
                database.deleteAllConnectionsWith(sender.getId());
                return;
            }
            reply[0] = "Введено неверное значение, процедура удаления прекращена.";
            sender.setGlobalState(GlobalState.COMMAND);
            database.addToFPL(sender.getId());
        }
        else {
            Boolean result = sender.setField(message);
            if (result) {
                reply[0] = "Изменение внесено.";
                sender.setGlobalState(GlobalState.COMMAND);
                database.addToFPL(sender.getId());
                sender.setProfileFilled(true);
            }else if (result == null){
                reply[0] = "Похоже ты не совсем честен по поводу пола.";
            }else {
                reply[0] = stateFSM.getWrongReplies().get(sender.getLocalState());
            }
        }
    }
}
