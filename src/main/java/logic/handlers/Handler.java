package logic.handlers;

import bots.platforms.Platform;
import database.entities.Account;
import database.entities.Profile;

public interface Handler {
    /**
     * Message handling function.
     *
     * @param user account of user who sent the message
     * @param profile profile of user who sent the message
     * @param reply    array of strings with size of 12, where every string is a separate message
     * @param message  user message
     * @param platform platform from which user sent message
     */
    void handleMessage(Account user, Profile profile, String[] reply, String message, Platform platform);
}
