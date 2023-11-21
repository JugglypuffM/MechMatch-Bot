package mainBot;

import java.util.HashMap;
import java.util.Map;

public class StateFSM {
    /**
     * Dictionary with field names and states, in which this fields changed
     * Key - field name(or number), Value - state
     */
    private final Map<String, LocalState> stateDict;
    /**
     * Dictionary with state sequences.
     * Key - current state, Value - next state after current
     */
    private final Map<LocalState, LocalState> nextDict;
    /**
     * Dictionary with replies for case, when user gave valid data
     * Key - state, Value - reply text
     */
    private final Map<LocalState, String> rightReplies;
    /**
     * Dictionary with replies for case, when user gave invalid data
     * Key - state, Value - reply text
     */
    private final Map<LocalState, String> wrongReplies;
    /**
     * Dictionary with replies for case, when user is asked to edit something
     * Key - state, Value - reply text
     */
    private final Map<LocalState, String> editReplies;
    /**
     * Dictionary with string representations of global states.
     * Used to translate the string into the global state
     * Key - string representation of state, Value - the global state itself
     */
    private final Map<String, GlobalState> globalStateMap;
    /**
     * Dictionary with string representations of local states.
     * Used to translate the string into the local state
     * Key - string representation of state, Value - the local state itself
     */
    private final Map<String, LocalState> localStateMap;
    public StateFSM(){
        this.stateDict = new HashMap<>();
        stateDict.put("имя", LocalState.NAME);
        stateDict.put("1", LocalState.NAME);
        stateDict.put("возраст", LocalState.AGE);
        stateDict.put("2", LocalState.AGE);
        stateDict.put("пол", LocalState.SEX);
        stateDict.put("3", LocalState.SEX);
        stateDict.put("город", LocalState.CITY);
        stateDict.put("4", LocalState.CITY);
        stateDict.put("информация о себе", LocalState.ABOUT);
        stateDict.put("5", LocalState.ABOUT);
        stateDict.put("нижний порог возраста собеседника", LocalState.EAGEMIN);
        stateDict.put("6", LocalState.EAGEMIN);
        stateDict.put("верхний порог возраста собеседника", LocalState.EAGEMAX);
        stateDict.put("7", LocalState.EAGEMAX);
        stateDict.put("пол собеседника", LocalState.ESEX);
        stateDict.put("8", LocalState.ESEX);
        stateDict.put("город собеседника", LocalState.ECITY);
        stateDict.put("9", LocalState.ECITY);
        stateDict.put("фото", LocalState.PHOTO);
        stateDict.put("10", LocalState.PHOTO);

        this.nextDict = new HashMap<>();
        nextDict.put(LocalState.NAME, LocalState.AGE);
        nextDict.put(LocalState.AGE, LocalState.SEX);
        nextDict.put(LocalState.SEX, LocalState.CITY);
        nextDict.put(LocalState.CITY, LocalState.ABOUT);
        nextDict.put(LocalState.ABOUT, LocalState.EAGEMIN);
        nextDict.put(LocalState.EAGEMIN, LocalState.EAGEMAX);
        nextDict.put(LocalState.EAGEMAX, LocalState.ESEX);
        nextDict.put(LocalState.ESEX, LocalState.ECITY);
        nextDict.put(LocalState.ECITY, LocalState.PHOTO);
        nextDict.put(LocalState.PHOTO, LocalState.FINISH);

        this.rightReplies = new HashMap<>();
        rightReplies.put(LocalState.NAME, "Отлично, теперь напиши цифрами, сколько тебе лет.");
        rightReplies.put(LocalState.AGE, "Теперь укажи свой пол(Парень/Девушка).");
        rightReplies.put(LocalState.SEX, "Хорошо, напиши название города, в котором живешь.");
        rightReplies.put(LocalState.CITY, "Расскажи о себе в одном сообщении, возможно это поможет подобрать тебе наиболее интересного собеседника.");
        rightReplies.put(LocalState.ABOUT, "Теперь напиши минимальный возраст твоего потенциального собеседника.");
        rightReplies.put(LocalState.EAGEMIN, "Теперь напиши максимальный возраст твоего потенциального собеседника.");
        rightReplies.put(LocalState.EAGEMAX, "Теперь укажи пол твоего потенциального собеседника(Парень/Девушка/Без разницы).");
        rightReplies.put(LocalState.ESEX, "Хорошо, напиши название города, в котором хочешь найти собеседника. Можешь написать \"любой\", чтобы этот параметр не учитывался при подборе собеседника");
        rightReplies.put(LocalState.ECITY, "Теперь отправь обложку для профиля.");
        rightReplies.put(LocalState.PHOTO, "Посмотри свою анкету еще раз, всё ли верно? Ответь да или нет.");

        this.editReplies = new HashMap<>();
        editReplies.put(LocalState.NAME, "Напиши новое имя.");
        editReplies.put(LocalState.AGE, "Напиши цифрами новый возраст.");
        editReplies.put(LocalState.SEX, "Укажи новый пол(Парень/Девушка).");
        editReplies.put(LocalState.CITY, "Напиши новое название города, в котором живешь.");
        editReplies.put(LocalState.ABOUT, "Расскажи о себе еще раз, возможно это поможет подобрать тебе наиболее интересного собеседника.");
        editReplies.put(LocalState.EAGEMIN, "Напиши новый минимальный возраст твоего потенциального собеседника.");
        editReplies.put(LocalState.EAGEMAX, "Напиши новый максимальный возраст твоего потенциального собеседника.");
        editReplies.put(LocalState.ESEX, "Укажи новый пол твоего потенциального собеседника(Парень/Девушка/Без разницы).");
        editReplies.put(LocalState.ECITY, "Напиши новое название города, в котором хочешь найти собеседника. Можешь написать \"любой\", чтобы этот параметр не учитывался при подборе собеседника.");
        editReplies.put(LocalState.PHOTO, "Отправь новую обложку для профиля.");

        this.wrongReplies = new HashMap<>();
        wrongReplies.put(LocalState.NAME, "");
        wrongReplies.put(LocalState.AGE, "Этот возраст выглядит неправдоподобно, введи значение между 15 и 119.");
        wrongReplies.put(LocalState.SEX, "Ввведи один из двух ответов: парень или девушка.");
        wrongReplies.put(LocalState.CITY, "");
        wrongReplies.put(LocalState.ABOUT, "");
        wrongReplies.put(LocalState.EAGEMIN, "Этот возраст выглядит неправдоподобно, введи значение между 15 и 119, а еще оно должно быть не больше максимального.");
        wrongReplies.put(LocalState.EAGEMAX, "Этот возраст выглядит неправдоподобно, введи значение между 15 и 119, а еще оно должно быть не меньше минимального.");
        wrongReplies.put(LocalState.ESEX, "Ввведи один из трех ответов: парень, девушка или без разницы.)");
        wrongReplies.put(LocalState.ECITY, "");
        wrongReplies.put(LocalState.PHOTO, "Пожалуйста, отправь картинку.");

        this.globalStateMap = new HashMap<>();
        globalStateMap.put("COMMAND", GlobalState.COMMAND);
        globalStateMap.put("PROFILE_FILL", GlobalState.PROFILE_FILL);
        globalStateMap.put("PROFILE_EDIT", GlobalState.PROFILE_EDIT);
        globalStateMap.put("MATCHES", GlobalState.MATCHES);
        globalStateMap.put("MATCHING", GlobalState.MATCHING);
        globalStateMap.put("PENDING", GlobalState.PENDING);

        this.localStateMap = new HashMap<>();
        localStateMap.put("START", LocalState.START);
        localStateMap.put("NAME", LocalState.NAME);
        localStateMap.put("AGE", LocalState.AGE);
        localStateMap.put("SEX", LocalState.SEX);
        localStateMap.put("CITY", LocalState.CITY);
        localStateMap.put("ABOUT", LocalState.ABOUT);
        localStateMap.put("EAGEMIN", LocalState.EAGEMIN);
        localStateMap.put("EAGEMAX", LocalState.EAGEMAX);
        localStateMap.put("ESEX", LocalState.ESEX);
        localStateMap.put("ECITY", LocalState.ECITY);
        localStateMap.put("PHOTO", LocalState.PHOTO);
        localStateMap.put("FINISH", LocalState.FINISH);
        localStateMap.put("CHOICE", LocalState.CHOICE);
        localStateMap.put("PROFILES", LocalState.PROFILES);
        localStateMap.put("EDIT", LocalState.EDIT);
        localStateMap.put("DELETE", LocalState.DELETE);
    }

    public Map<String, LocalState> getStateDict(){
        return stateDict;
    }
    public Map<LocalState, LocalState> getNextDict() {
        return nextDict;
    }

    public Map<LocalState, String> getRightReplies() {
        return rightReplies;
    }

    public Map<LocalState, String> getWrongReplies() {
        return wrongReplies;
    }

    public Map<LocalState, String> getEditReplies() {
        return editReplies;
    }

    public Map<String, GlobalState> getGlobalStateMap() {
        return globalStateMap;
    }

    public Map<String, LocalState> getLocalStateMap() {
        return localStateMap;
    }
}
