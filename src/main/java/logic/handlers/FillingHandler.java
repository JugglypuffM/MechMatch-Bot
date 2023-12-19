package logic.handlers;

import bots.platforms.Platform;
import database.main.Database;
import database.entities.Account;
import database.entities.Profile;
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
    public void handleMessage(Account user, Profile profile, String[] reply, String message, Platform platform) {
        switch (user.getLocalState()) {
            case START -> {
                reply[0] = "А теперь перейдем к заполнению анкеты.";
                reply[1] = "Введи свое имя";
                user.setLocalState(LocalState.NAME);
            }
            case FINISH -> {
                if (message.equalsIgnoreCase("да")) {
                    database.addToFPL(user.getId());
                    reply[0] = "Отлично, теперь можно переходить к использованию.";
                    reply[1] = giveHelp();
                    user.setGlobalState(GlobalState.COMMAND);
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
                    user.setGlobalState(GlobalState.PROFILE_EDIT);
                    user.setLocalState(LocalState.START);
                } else {
                    reply[0] = "Пожалуйста, напиши либо да, либо нет";
                }
            }
            default -> {
                Boolean result = database.setField(profile, message);
                if (result == null){
                    reply[0] = "Кажется ты меня обманываешь.";
                }else if (result) {
                    reply[0] = stateFSM.getRightReplies().get(user.getLocalState());
                    user.setLocalState(stateFSM.getNextDict().get(user.getLocalState()));
                }else {
                    reply[0] = stateFSM.getWrongReplies().get(user.getLocalState());
                }
            }
        }
    }
}
