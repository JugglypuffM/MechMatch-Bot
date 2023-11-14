package mainBot;

public enum LocalState {
    START("START"),
    NAME("NAME"),
    AGE("AGE"),
    SEX("SEX"),
    CITY("CITY"),
    ABOUT("ABOUT"),
    EAGEMIN("EAGEMIN"),
    EAGEMAX("EAGEMAX"),
    ESEX("ESEX"),
    ECITY("ECITY"),
    PHOTO("PHOTO"),
    FINISH("FINISH"),
    ALL("ALL"),
    MATCHES("MATCHES");
    final String value;
    LocalState(String m_value){
        this.value = m_value;
    }
    @Override
    public String toString() {
        return value;
    }
}
