package logic.states;

public enum GlobalState {
    /**
     * Default state of user, when tgBot waits for command to execute
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
     * State of user, while he is in getting profiles procedure
     */
    MATCHES("MATCHES"),
    MATCHING("MATCHING"),
    PENDING("PENDING");
    final String value;
    GlobalState(String m_value){
        this.value = m_value;
    }
    public String stringRepresentation() {
        return value;
    }
}
