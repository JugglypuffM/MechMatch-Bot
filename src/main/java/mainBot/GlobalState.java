package mainBot;

public enum GlobalState {
    /**
     * Default state of user, when bot waits for command to execute
     */
    COMMAND("COMMAND"),
    /**
     * State of user, while he is in profile filling procedure
     */
    PROFILE_FILL("PROFILE_FILL"),
    /**
     * State of user, while he is in profile editing procedure
     */
    PROFILE_EDIT("PROFILE_EDIT"),
    /**
     * State of user, while he is in matching procedure
     */
    MATCHING("MATCHING");
    final String value;
    GlobalState(String m_value){
        this.value = m_value;
    }
    @Override
    public String toString() {
        return value;
    }
}
