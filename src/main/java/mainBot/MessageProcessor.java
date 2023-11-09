package mainBot;

import java.util.List;
import java.util.Map;

public class MessageProcessor {
    Utility util = new Utility();
    private final UserStorage storage = new UserStorage();
    private final Map<String, LocalState> stateDict = util.getStateDict();
    private final Map<LocalState, LocalState> nextDict = util.getNextDict();
    private final Map<LocalState, String> rightReplies = util.getRightReplies();
    private final Map<LocalState, String> wrongReplies = util.getWrongReplies();
    /**
     * Simple help method
     * @return a description of the commands
     */
    private String giveHelp(){
        return """
               Вот, что я умею:\s
                /help - вывод описания всех команд\s
                /start - начало работы с ботом\s
                /myProfile - бот отправляет пользователю его анкету\s
                /match - начало поиска собеседника, вывод анкет пользователей\s
                /changeProfile - бот сбрасывает текущую анкету пользователя и создает новую, начинает заново процедуру заполнения анкеты\s
                /editProfile - бот предлагает изменить одно из полей анкеты пользователя
               """;
    }
    /**
     * Collecting all user data in a string
     * @param id string presentation of user id
     * @return formatted user profile data
     */
    private String profileData(String id){
        User user = storage.getUser(id);
        return "Имя: " + user.getName() +
                "\nВозраст: " + user.getAge() +
                "\nПол: " + user.getSex() +
                "\nГород: " + user.getCity() +
                "\nИнформация о себе: " + user.getInformation() +
                "\nДиапазон возраста собеседника: " + user.getMinExpectedAge() + " - " + user.getMaxExpectedAge() +
                "\nПол собеседника: " + user.getExpectedSex() +
                "\nГород собеседника: " + user.getExpectedCity();
    }
    /**
     * Delete data
     * @param id initial state
     */
    private void eraseProfileData(String id){
        User user = storage.getUser(id);
        user.setName(null);
        user.setAge("0");
        user.setSex("");
        user.setCity(null);
        user.setInformation(null);
        user.setMinExpectedAge("0");
        user.setMaxExpectedAge("999");
        user.setExpectedSex("");
        user.setExpectedCity(null);
    }
    /**
     * Method that writes data of ten profiles from {@link UserStorage#getOtherProfilesList}
     * into answer variable from {@link MessageProcessor#processMessage}
     * @param answer link to answer variable in {@link MessageProcessor#processMessage}
     * @param page number of profiles decade
     * @param id id of requester, this user will not be within the profiles
     */
    private void getTenProfiles(String[] answer, int page, String id){
        List<String> opl = storage.getOtherProfilesList(id);
        for (int i = 0; i < 10; i++){
            if (i+page*10 < opl.size()){
                answer[2+i] = profileData(opl.get(i+page*10));
            }else {
                break;
            }
        }
    }
    /**
     * Getter for stateDict from {@link MessageProcessor#processMessage}
     * @return map of local states
     */
    private void caseDefault(String id, String message, User sender, String[] answer){
        if (storage.getUser(id).getExpectedCity() == null){
            message = "/start";
        }
        if (!(message.charAt(0) == '/')){
            answer[0] = "Что-то я тебя не понимаю, если не знаешь что я умею - введи /help";
            return;
        }
        switch (message){
            default:
                answer[0] = "Такой команды нет, введи /help, чтобы увидеть список всех команд";
                break;
            case "/start":
                if (storage.getUser(id).getExpectedCity() != null){
                    answer[0] = giveHelp();
                    break;
                }
                answer[0] = "Привет! Ты попал на MechMatch - место, где тебе помогут найти твою вторую половинку или просто хорошего друга :)  ";
                answer[1] = """
                        Перед началом хочется тебя предупредить, что бот никак не идентифицирует пользователя по документам, поэтому будь осторожен!\s
                        Отправь любое сообщение, чтобы подтвердить прочтение предупреждения.
                        """;
                sender.setGlobalState(GlobalState.PROFILE_FILL);
                sender.setLocalState(LocalState.START);
                break;
            case "/help":
                answer[0] = giveHelp();
                break;
            case "/changeProfile":
                eraseProfileData(id);
                storage.deleteFromOPL(id);
                answer[0] = "Сейчас тебе придется пройти процедуру заполнения анкеты заново. Напиши что-нибудь, если готов.";
                sender.setGlobalState(GlobalState.PROFILE_FILL);
                sender.setLocalState(LocalState.START);
                break;
            case "/editProfile":
                storage.deleteFromOPL(id);
                answer[0] = "Что хочешь изменить?";
                answer[1] = "Вот список полей доступных для изменения:" +
                        " \n1 - Имя(" + sender.getName() +
                        ")\n2 - Возраст(" + sender.getAge() +
                        ")\n3 - Пол(" + sender.getSex() +
                        ")\n4 - Город(" + sender.getCity() +
                        ")\n5 - Информация о себе(" + sender.getInformation() +
                        ")\n6 - Нижний порог возраста собеседника(" + sender.getMinExpectedAge() +
                        ")\n7 - Верхний порог возраста собеседника(" + sender.getMaxExpectedAge() +
                        ")\n8 - Пол собеседника(" + sender.getExpectedSex() +
                        ")\n9 - Город собеседника(" + sender.getExpectedCity() + ")";
                sender.setGlobalState(GlobalState.PROFILE_EDIT);
                sender.setLocalState(LocalState.START);
                break;
            case "/match":
                if (storage.getOtherProfilesList(id).isEmpty()){
                    answer[0] = "Кроме тебя пока никого нет ;(";
                    break;
                }
                answer[0] = "Какую страницу анкет вывести(Всего: " + (storage.getOtherProfilesList(id).size()/10 + 1) + ")?";
                sender.setGlobalState(GlobalState.MATCHING);
                break;
            case "/myProfile":
                answer[0] = profileData(id);
                break;
        }
    }
    private void caseProfileFill(String id, String message, User sender, String[] answer) {
        switch (sender.getLocalState()) {
            case START:
                answer[0] = "А теперь перейдем к заполнению анкеты.";
                answer[1] = "Введи свое имя";
                sender.setLocalState(LocalState.NAME);
                break;
            case FINISH:
                if (message.equalsIgnoreCase("да")) {
                    storage.addToOPL(id);
                    answer[0] = "Отлично, теперь можно переходить к использованию.";
                    answer[1] = giveHelp();
                    sender.setGlobalState(GlobalState.DEFAULT);
                } else if (message.equalsIgnoreCase("нет")) {
                    answer[0] = "Что хочешь изменить?";
                    answer[1] = "Вот список полей доступных для изменения:" +
                            " \n1 - Имя(" + sender.getName() +
                            ")\n2 - Возраст(" + sender.getAge() +
                            ")\n3 - Пол(" + sender.getSex() +
                            ")\n4 - Город(" + sender.getCity() +
                            ")\n5 - Информация о себе(" + sender.getInformation() +
                            ")\n6 - Нижний порог возраста собеседника(" + sender.getMinExpectedAge() +
                            ")\n7 - Верхний порог возраста собеседника(" + sender.getMaxExpectedAge() +
                            ")\n8 - Пол собеседника(" + sender.getExpectedSex() +
                            ")\n9 - Город собеседника(" + sender.getExpectedCity() + ")";
                    sender.setGlobalState(GlobalState.PROFILE_EDIT);
                    sender.setLocalState(LocalState.START);
                    break;
                } else {
                    answer[0] = "Пожалуйста, напиши либо да, либо нет";
                }
                break;
            default:
                if (sender.setField(message)) {
                    answer[0] = rightReplies.get(sender.getLocalState());
                    if (sender.getLocalState().equals(LocalState.ECITY)) {
                        answer[2] = profileData(id);
                    }
                    sender.setLocalState(nextDict.get(sender.getLocalState()));
                } else {
                    answer[0] = wrongReplies.get(sender.getLocalState());
                }
                break;
        }
    }
    private void caseProfileEdit(String id, String message, User sender, String[] answer){
        answer[0] = "Изменение внесено.";
        if (sender.getLocalState().equals(LocalState.START)) {
            answer[0] = "Введи новое значение.";
            if (!stateDict.containsKey(message)) {
                answer[0] = "Напиши либо цифру соответствующую полю, либо название поля.";
                return;
            }
            sender.setLocalState(stateDict.get(message));
        } else {
            if (sender.setField(message)) {
                sender.setGlobalState(GlobalState.DEFAULT);
                storage.addToOPL(id);
            } else {
                answer[0] = wrongReplies.get(sender.getLocalState());
            }
        }
    }
    private void caseMatching(String id, String message, User sender, String[] answer){
        int page;
        try {
            page = Integer.parseInt(message);
        } catch (NumberFormatException e) {
            answer[0] = "Пожалуйста, введи ответ цифрами.";
            return;
        }
        if ((page < 1) || (page > storage.getOtherProfilesList(id).size() / 10 + 1)) {
            answer[0] = "Нет страницы с таким номером.";
            return;
        }
        answer[0] = "Профили на странице " + message + ":";
        getTenProfiles(answer, page - 1, id);
        sender.setGlobalState(GlobalState.DEFAULT);
    }
    /**
     * Main message processing method
     * @param id string presentation of user id
     * @param message user message
     * @return reply to user message
     */
    public String[] processMessage(String id, String message){
        String[] answer = new String[12];
        if (storage.getUser(id) == null){
            storage.addUser(id);
        }
        User sender = storage.getUser(id);
        switch (sender.getGlobalState()){
            case DEFAULT -> caseDefault(id, message, sender, answer);
            case PROFILE_FILL -> caseProfileFill(id, message, sender, answer);
            case PROFILE_EDIT -> caseProfileEdit(id, message, sender, answer);
            case MATCHING ->  caseMatching(id, message, sender, answer);
        }
        return answer;
    }
}
