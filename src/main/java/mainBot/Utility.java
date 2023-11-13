package mainBot;

import java.util.HashMap;
import java.util.Map;

public class Utility {
    public Map<String, LocalState> getStateDict() {
        Map<String, LocalState> stateDict = new HashMap<>();
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
        return stateDict;
    }
    public Map<LocalState, LocalState> getNextDict(){
        Map<LocalState, LocalState> nextDict = new HashMap<>();
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
        return nextDict;
    }
    public Map<LocalState, String> getRightReplies(){
        Map<LocalState, String> rightAnswers = new HashMap<>();
        rightAnswers.put(LocalState.NAME, "Отлично, теперь напиши цифрами, сколько тебе лет.");
        rightAnswers.put(LocalState.AGE, "Теперь укажи свой пол(Парень/Девушка).");
        rightAnswers.put(LocalState.SEX, "Хорошо, напиши название города, в котором живешь.");
        rightAnswers.put(LocalState.CITY, "Расскажи о себе в одном сообщении, возможно это поможет подобрать тебе наиболее интересного собеседника.");
        rightAnswers.put(LocalState.ABOUT, "Теперь напиши минимальный возраст твоего потенциального собеседника.");
        rightAnswers.put(LocalState.EAGEMIN, "Теперь напиши максимальный возраст твоего потенциального собеседника.");
        rightAnswers.put(LocalState.EAGEMAX, "Теперь укажи пол твоего потенциального собеседника(Парень/Девушка/Без разницы).");
        rightAnswers.put(LocalState.ESEX, "Хорошо, напиши название города, в котором хочешь найти собеседника.");
        rightAnswers.put(LocalState.ECITY, "Теперь отправь обложку для профиля.");
        rightAnswers.put(LocalState.PHOTO, "Посмотри свою анкету еще раз, всё ли верно? Ответь да или нет.");
        return rightAnswers;
    }
    public Map<LocalState, String> getWrongReplies(){
        Map<LocalState, String> wrongReplies = new HashMap<>();
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
        return wrongReplies;
    }
}
