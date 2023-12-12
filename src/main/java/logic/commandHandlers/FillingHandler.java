package logic.commandHandlers;

import database.main.Database;
import database.models.Profile;
import database.models.User;
import logic.states.GlobalState;
import logic.states.LocalState;
import logic.states.StateFSM;

/**
 * Profile filling procedure handler.
 * Fills the exact profile data fields of given user depending on its state
 */
public class FillingHandler implements Handler{
    private final Database database;
    private final StateFSM stateFSM;
    public FillingHandler(Database m_database, StateFSM m_stateFSM){
        this.database = m_database;
        this.stateFSM = m_stateFSM;
    }
    /**
     * Simple help method
     * @return a description of the commands
     */
    private String giveHelp(){
        return """
               Вот, что я умею:\s
                /help - вывод описания всех команд\s
                /myProfile - посмотреть данные своей анкеты\s
                /match - поиск собеседника\s
                /myMatches - посмотреть анкету уже предложенных пользователей\s
                /pending - посмотреть людей, ожидающих твоего ответа\s
                /changeProfile - удалить текущую анкету и заполнить новую\s
                /editProfile - изменить одно из полей анкеты\s
                /deleteProfile - полностью удалить профиль
               """;
    }
    /**
     * Method to unify field filling.
     * Uses different setters depending on current local state.
     * @param value user's message
     * @return true if field was filled successfully and false if not
     */
    public Boolean setField(Integer id, String value){
        Profile profile = database.getProfile(id);
        switch (database.getUser(id).getLocalState()){
            case NAME:
                profile.setName(value);
                return true;
            case AGE:
                return profile.setAge(value);
            case SEX:
                return profile.setSex(value);
            case CITY:
                profile.setCity(value);
                return true;
            case ABOUT:
                profile.setInformation(value);
                return true;
            case EAGEMIN:
                return profile.setMinExpectedAge(value);
            case EAGEMAX:
                return profile.setMaxExpectedAge(value);
            case ESEX:
                return profile.setExpectedSex(value);
            case ECITY:
                profile.setExpectedCity(value);
                return true;
            case PHOTO:
                return false;
        }
        return false;
    }
    public void handleMessage(Integer id, String[] reply, String message) {
        User sender = database.getUser(id);
        Profile profile = database.getProfile(id);
        switch (sender.getLocalState()) {
            case START -> {
                reply[0] = "А теперь перейдем к заполнению анкеты.";
                reply[1] = "Введи свое имя";
                sender.setLocalState(LocalState.NAME);
            }
            case FINISH -> {
                if (message.equalsIgnoreCase("да")) {
                    database.addToFPL(id);
                    reply[0] = "Отлично, теперь можно переходить к использованию.";
                    reply[1] = giveHelp();
                    sender.setGlobalState(GlobalState.COMMAND);
                } else if (message.equalsIgnoreCase("нет")) {
                    reply[0] = "Что хочешь изменить?";
                    reply[1] = "Вот список полей доступных для изменения:" +
                            " \n1 - Имя(" + profile.getName() +
                            ")\n2 - Возраст(" + profile.getAge() +
                            ")\n3 - Пол(" + profile.getSex() +
                            ")\n4 - Город(" + profile.getCity() +
                            ")\n5 - Информация о себе(" + profile.getInformation() +
                            ")\n6 - Нижний порог возраста собеседника(" + profile.getMinExpectedAge() +
                            ")\n7 - Верхний порог возраста собеседника(" + profile.getMaxExpectedAge() +
                            ")\n8 - Пол собеседника(" + profile.getExpectedSex() +
                            ")\n9 - Город собеседника(" + profile.getExpectedCity() +
                            ")\n10 - Фото";
                    sender.setGlobalState(GlobalState.PROFILE_EDIT);
                    sender.setLocalState(LocalState.START);
                } else {
                    reply[0] = "Пожалуйста, напиши либо да, либо нет";
                }
            }
            default -> {
                Boolean result = setField(id, message);
                if (result == null){
                    reply[0] = "Кажется ты меня обманываешь.";
                }else if (result) {
                    reply[0] = stateFSM.getRightReplies().get(sender.getLocalState());
                    sender.setLocalState(stateFSM.getNextDict().get(sender.getLocalState()));
                }else {
                    reply[0] = stateFSM.getWrongReplies().get(sender.getLocalState());
                }
            }
        }
    }
}
