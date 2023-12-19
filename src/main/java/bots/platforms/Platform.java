package bots.platforms;

public enum Platform {
    TELEGRAM("TELEGRAM"),
    DISCORD("DISCORD");
    final String value;
    Platform(String m_value){
        this.value = m_value;
    }
    public String stringRepresentation() {
        return value;
    }
}
