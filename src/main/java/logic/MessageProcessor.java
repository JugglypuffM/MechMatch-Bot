package logic;

import bots.BotDriver;
import database.main.Database;
import database.models.User;
import logic.commandHandlers.*;
import logic.notificator.Notificator;
import logic.states.GlobalState;
import logic.states.LocalState;
import logic.states.StateFSM;


public class MessageProcessor {
    private final StateFSM stateFSM = new StateFSM();
    /**
     * Database interface.
     */
    private final Database database;
    private final Notificator notificator;
    private final CommandHandler caseCommand;
    private final FillingHandler caseProfileFill;
    private final EditHandler caseProfileEdit;
    private final MatchesHandler caseMatches;
    private final MatchingHandler caseMatching;
    private final PendingHandler casePending;
    public MessageProcessor(Database m_database, BotDriver m_driver){
        this.database = m_database;
        this.notificator = new Notificator(m_driver);
        this.caseCommand = new CommandHandler(m_database);
        this.caseProfileFill = new FillingHandler(m_database, stateFSM);
        this.caseProfileEdit = new EditHandler(m_database, stateFSM);
        this.caseMatches = new MatchesHandler(m_database);
        this.caseMatching = new MatchingHandler(m_database, notificator);
        this.casePending = new PendingHandler(m_database, notificator);
    }
    private Handler chooseHandler(User sender){
        Handler handler;
        switch (sender.getGlobalState()){
            default -> {
                System.out.println("Unknown state for user: " + sender.getId() + "\n");
                handler = caseCommand;
            }
            case COMMAND -> handler = caseCommand;
            case PROFILE_FILL -> handler = caseProfileFill;
            case PROFILE_EDIT -> handler = caseProfileEdit;
            case MATCHES -> handler = caseMatches;
            case MATCHING -> handler = caseMatching;
            case PENDING -> handler = casePending;
        }
        return handler;
    }
    /**
     * Main message processing method.
     * Initializes reply variable as an array of strings with size 24.
     * Every string from 0 to 11 in this array is a separate message, which will be sent further.
     * Every string from 12 to 23 is an optional photoID, these photos are bounded to messages with shift = -12.
     * <p>For example, 12th photo will be bounded to 12-12=0 message<p>
     * Checks if user with given id exists and creates new one if not.
     * @param id string presentation of user id
     * @param message user message
     * @return reply to user message
     */
    public synchronized String[] processMessage(String id, String message){
        String[] reply = new String[24];
        if (database.getUser(id) == null) {
            String username, platform;
            if (message.startsWith("data")) {
                username = message.split("\\|")[0].substring(4);
                platform = message.split("\\|")[1];
            } else {
                reply[0] = "требуются данные";
                return reply;
            }
            database.addUser(id, username, platform);
        }
        User sender = database.getUser(id);
        Handler handler = chooseHandler(sender);
        handler.handleMessage(sender, reply, message);
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
    public synchronized String[] processPhoto(String id, String photoID){
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
