package logic.commandHandlers;

public interface Handler {
    /**
     * Message handling function.
     *
     * @param id
     * @param reply   array of strings with size of 12, where every string is a separate message
     * @param message user message
     */
    void handleMessage(Integer id, String[] reply, String message);
}
