package mainBot.commandHandlers;

import database.models.User;

public interface Handler {
    /**
     * Message handling function.
     * @param sender instance of {@link User} class representing a sender
     * @param reply array of strings with size of 12, where every string is a separate message
     * @param message user message
     */
    public void handleMessage(User sender, String[] reply, String message);
}
