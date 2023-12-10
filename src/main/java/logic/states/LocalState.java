package logic.states;

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
    CHOICE("CHOICE"),
    PROFILES("PROFILES"),
    EDIT("EDIT"),
    DELETE("DELETE");
    final String value;
    LocalState(String m_value){
        this.value = m_value;
    }
    public String stringRepresentation() {
        return value;
    }
}
