package bots.platforms;

public enum Platform {
    TELEGRAM("TELEGRAM"),
    DISCORD("DISCORD");
    final String value;
    Platform(String m_value){
        this.value = m_value;
    }
    @Override
    public String toString() {
        return value;
    }
}
