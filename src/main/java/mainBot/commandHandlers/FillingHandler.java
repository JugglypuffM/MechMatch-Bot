package mainBot.commandHandlers;

import database.main.Database;
import database.models.User;
import mainBot.states.GlobalState;
import mainBot.states.LocalState;
import mainBot.states.StateFSM;

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
    public void handleMessage(User sender, String[] reply, String message) {
        switch (sender.getLocalState()) {
            case START -> {
                reply[0] = "А теперь перейдем к заполнению анкеты.";
                reply[1] = "Введи свое имя";
                sender.setLocalState(LocalState.NAME);
            }
            case FINISH -> {
                if (message.equalsIgnoreCase("да")) {
                    database.addToFPL(sender.getId());
                    reply[0] = "Отлично, теперь можно переходить к использованию.";
                    reply[1] = giveHelp();
                    sender.setGlobalState(GlobalState.COMMAND);
                } else if (message.equalsIgnoreCase("нет")) {
                    reply[0] = "Что хочешь изменить?";
                    reply[1] = "Вот список полей доступных для изменения:" +
                            " \n1 - Имя(" + sender.getName() +
                            ")\n2 - Возраст(" + sender.getAge() +
                            ")\n3 - Пол(" + sender.getSex() +
                            ")\n4 - Город(" + sender.getCity() +
                            ")\n5 - Информация о себе(" + sender.getInformation() +
                            ")\n6 - Нижний порог возраста собеседника(" + sender.getMinExpectedAge() +
                            ")\n7 - Верхний порог возраста собеседника(" + sender.getMaxExpectedAge() +
                            ")\n8 - Пол собеседника(" + sender.getExpectedSex() +
                            ")\n9 - Город собеседника(" + sender.getExpectedCity() +
                            ")\n10 - Фото";
                    sender.setGlobalState(GlobalState.PROFILE_EDIT);
                    sender.setLocalState(LocalState.START);
                } else {
                    reply[0] = "Пожалуйста, напиши либо да, либо нет";
                }
            }
            default -> {
                if (sender.setField(message)) {
                    reply[0] = stateFSM.getRightReplies().get(sender.getLocalState());
                    sender.setLocalState(stateFSM.getNextDict().get(sender.getLocalState()));
                } else {
                    reply[0] = stateFSM.getWrongReplies().get(sender.getLocalState());
                }
            }
        }
    }
}
