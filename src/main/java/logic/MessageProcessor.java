package logic;

import bots.BotDriver;
import bots.platforms.Platform;
import database.main.Database;
import database.entities.Account;
import database.entities.Client;
import database.entities.Profile;
import logic.handlers.*;
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
    private final CommandHandler caseCommand;
    private final FillingHandler caseProfileFill;
    private final EditHandler caseProfileEdit;
    private final MatchesHandler caseMatches;
    private final MatchingHandler caseMatching;
    private final PendingHandler casePending;
    public MessageProcessor(Database m_database, BotDriver m_driver){
        this.database = m_database;
        Notificator notificator = new Notificator(m_driver);
        this.caseCommand = new CommandHandler(m_database);
        this.caseProfileFill = new FillingHandler(m_database, stateFSM);
        this.caseProfileEdit = new EditHandler(m_database, stateFSM);
        this.caseMatches = new MatchesHandler(m_database);
        this.caseMatching = new MatchingHandler(m_database, notificator);
        this.casePending = new PendingHandler(m_database, notificator);
    }
    private Handler chooseHandler(Account sender){
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
     * @param platformId string presentation of user id
     * @param message user message
     * @return reply to user message
     */
    public synchronized String[] processMessage(String platformId, Platform platform, String message){
        String[] reply = new String[24];
        Client sender = database.getClient(platformId);
        if (sender == null){
        }
        if (!sender.isLoggedIn()){
        }
        Account user = database.getAccountWithPlatformId(platformId, platform);
        Profile profile = database.getProfile(user.getId());
        Handler handler = chooseHandler(user);
        handler.handleMessage(user, profile, reply, message, platform);
        database.updateAccount(user);
        database.updateProfile(profile);
        return reply;
    }

    /**
     * Photo handler.
     * Asks to send a message if {@link LocalState} of user with given id is not {@link LocalState#PHOTO}.
     * If it is sets user's photoID with given photoID
     * @param platformId string presentation of user id
     * @param photoID id of picture, which is going to be user's profile photo
     * @return reply to user message
     */
    public synchronized String[] processPhoto(String platformId, Platform platform, String photoID){
        String[] reply = new String[24];
        Account user = database.getAccountWithPlatformId(platformId, platform);
        if ((user == null) || (user.getLocalState() != LocalState.PHOTO)){
            reply[0] = "Пожалуйста, отправь сообщение.";
            return reply;
        }
        Profile profile = database.getProfile(user.getId());
        profile.setPhotoID(photoID);
        user.setLocalState(stateFSM.getNextDict().get(LocalState.PHOTO));
        if (user.getGlobalState() == GlobalState.PROFILE_EDIT){
            reply[0] = "Изменение внесено.";
            user.setGlobalState(GlobalState.COMMAND);
            database.addToFPL(user.getId());
            profile.setProfileFilled(true);
        }
        else {
            reply[0] = stateFSM.getRightReplies().get(LocalState.PHOTO);
            reply[2] = database.profileData(user.getId());
            reply[14] = profile.getPhotoID();
        }
        database.updateAccount(user);
        database.updateProfile(profile);
        return reply;
    }
}
