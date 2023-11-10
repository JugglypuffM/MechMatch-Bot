package mainBot;

public enum GlobalState {
    /**
     * Default state of user, when bot waits for command to execute
     */
    COMMAND,
    /**
     * State of user, while he is in profile filling procedure
     */
    PROFILE_FILL,
    /**
     * State of user, while he is in profile editing procedure
     */
    PROFILE_EDIT,
    /**
     * State of user, while he is in matching procedure
     */
    MATCHING
}
