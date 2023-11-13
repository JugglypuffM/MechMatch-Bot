package mainBot;

import java.util.List;
import java.util.Map;

public class MessageProcessor {
    Utility util = new Utility();
    /**
     * Storage of users, which are represented by {@link User} class and can be accessed by id.
     */
    private final UserStorage storage = new UserStorage();
    /**
     * Dictionary with field names and states, in which this fields changed
     * Key - field name(or number), Value - state
     */
    private final Map<String, LocalState> stateDict = util.getStateDict();
    /**
     * Dictionary with state sequences.
     * Key - current state, Value - next state after current
     */
    private final Map<LocalState, LocalState> nextDict = util.getNextDict();
    /**
     * Dictionary with replies for case, when user gave valid data
     * Key - state, Value - reply text
     */
    private final Map<LocalState, String> rightReplies = util.getRightReplies();
    /**
     * Dictionary with replies for case, when user gave invalid data
     * Key - state, Value - reply text
     */
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
     * Erase all profile data to fill it again
     * @param id string representation of user id
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
                answer[14+i] = storage.getUser(opl.get(i+page*10)).getPhotoID();
            }else {
                break;
            }
        }
    }
    /**
     * Command handler.
     * Changes state depending on command.
     * If message does not start with '/' or is not a supported command - offers to watch command list
     * @param id string representation of user id
     * @param message user message
     * @param sender instance of {@link User} class representing a sender
     * @param reply array of strings with size of 12, where every string is a separate message
     */
    private void caseCommand(String id, String message, User sender, String[] reply){
        if (storage.getUser(id).getExpectedCity() == null){
            message = "/start";
        }
        if (!(message.charAt(0) == '/')){
            reply[0] = "Что-то я тебя не понимаю, если не знаешь что я умею - введи /help";
            return;
        }
        switch (message){
            default:
                reply[0] = "Такой команды нет, введи /help, чтобы увидеть список всех команд";
                break;
            case "/start":
                if (storage.getUser(id).getExpectedCity() != null){
                    reply[0] = giveHelp();
                    break;
                }
                reply[0] = "Привет! Ты попал на MechMatch - место, где тебе помогут найти твою вторую половинку или просто хорошего друга :)  ";
                reply[1] = """
                        Перед началом хочется тебя предупредить, что бот никак не идентифицирует пользователя по документам, поэтому будь осторожен!\s
                        Отправь любое сообщение, чтобы подтвердить прочтение предупреждения.
                        """;
                sender.setGlobalState(GlobalState.PROFILE_FILL);
                sender.setLocalState(LocalState.START);
                break;
            case "/help":
                reply[0] = giveHelp();
                break;
            case "/changeProfile":
                eraseProfileData(id);
                storage.deleteFromOPL(id);
                reply[0] = "Сейчас тебе придется пройти процедуру заполнения анкеты заново. Напиши что-нибудь, если готов.";
                sender.setGlobalState(GlobalState.PROFILE_FILL);
                sender.setLocalState(LocalState.START);
                break;
            case "/editProfile":
                storage.deleteFromOPL(id);
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
                reply[13] = sender.getPhotoID();
                sender.setGlobalState(GlobalState.PROFILE_EDIT);
                sender.setLocalState(LocalState.START);
                break;
            case "/match":
                if (storage.getOtherProfilesList(id).isEmpty()){
                    reply[0] = "Кроме тебя пока никого нет ;(";
                    break;
                }
                reply[0] = "Какую страницу анкет вывести(Всего: " + (storage.getOtherProfilesList(id).size()/10 + 1) + ")?";
                sender.setGlobalState(GlobalState.MATCHING);
                break;
            case "/myProfile":
                reply[0] = profileData(id);
                reply[12] = sender.getPhotoID();
                break;
        }
    }

    /**
     * Profile filling procedure handler.
     * Fills the exact profile data fields of given user depending on its state
     * @param id string representation of user id
     * @param message user message
     * @param sender instance of {@link User} class representing a sender
     * @param reply array of strings with size of 12, where every string is a separate message
     */
    private void caseProfileFill(String id, String message, User sender, String[] reply) {
        switch (sender.getLocalState()) {
            case START:
                reply[0] = "А теперь перейдем к заполнению анкеты.";
                reply[1] = "Введи свое имя";
                sender.setLocalState(LocalState.NAME);
                break;
            case FINISH:
                if (message.equalsIgnoreCase("да")) {
                    storage.addToOPL(id);
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
                    break;
                } else {
                    reply[0] = "Пожалуйста, напиши либо да, либо нет";
                }
                break;
            default:
                if (sender.setField(message)) {
                    reply[0] = rightReplies.get(sender.getLocalState());
                    if (sender.getLocalState() == LocalState.PHOTO) {
                        reply[2] = profileData(id);
                        reply[14] = sender.getPhotoID();
                    }
                    sender.setLocalState(nextDict.get(sender.getLocalState()));
                } else {
                    reply[0] = wrongReplies.get(sender.getLocalState());
                }
                break;
        }
    }
    /**
     * Profile editing procedure handler.
     * Fills the exact profile data field of given user depending on the user's choice
     * @param id string representation of user id
     * @param message user message
     * @param sender instance of {@link User} class representing a sender
     * @param reply array of strings with size of 12, where every string is a separate message
     */
    private void caseProfileEdit(String id, String message, User sender, String[] reply){
        if (sender.getLocalState().equals(LocalState.START)) {
            reply[0] = "Введи новое значение.";
            if (!stateDict.containsKey(message)) {
                reply[0] = "Напиши либо цифру соответствующую полю, либо название поля.";
                return;
            }
            sender.setLocalState(stateDict.get(message));
        } else {
            if (sender.setField(message)) {
                reply[0] = "Изменение внесено.";
                sender.setGlobalState(GlobalState.COMMAND);
                storage.addToOPL(id);
            } else {
                reply[0] = wrongReplies.get(sender.getLocalState());
            }
        }
    }

    /**
     * Matching procedure handler.
     * Fills reply with decade of profiles on given page.
     * Data of each profile will be placed in separate array cell
     * @param id string representation of user id
     * @param message user message
     * @param sender instance of {@link User} class representing a sender
     * @param reply array of strings with size of 12, where every string is a separate message
     */
    private void caseMatching(String id, String message, User sender, String[] reply){
        int page;
        try {
            page = Integer.parseInt(message);
        } catch (NumberFormatException e) {
            reply[0] = "Пожалуйста, введи ответ цифрами.";
            return;
        }
        if ((page < 1) || (page > storage.getOtherProfilesList(id).size() / 10 + 1)) {
            reply[0] = "Нет страницы с таким номером.";
            return;
        }
        reply[0] = "Профили на странице " + message + ":";
        getTenProfiles(reply, page - 1, id);
        sender.setGlobalState(GlobalState.COMMAND);
    }
    /**
     * Main message processing method.
     * Initializes reply variable as an array of strings with size 12.
     * Every string in this array is a separate message, which will be sent further.
     * Checks if user with given id exists and creates new one if not.
     * @param id string presentation of user id
     * @param message user message
     * @return reply to user message
     */
    public String[] processMessage(String id, String message){
        String[] reply = new String[24];
        if (storage.getUser(id) == null){
            storage.addUser(id);
        }
        User sender = storage.getUser(id);
        switch (sender.getGlobalState()){
            case COMMAND -> caseCommand(id, message, sender, reply);
            case PROFILE_FILL -> caseProfileFill(id, message, sender, reply);
            case PROFILE_EDIT -> caseProfileEdit(id, message, sender, reply);
            case MATCHING ->  caseMatching(id, message, sender, reply);
        }
        return reply;
    }

    /**
     * Photo handler.
     * Asks to send a message if {@link LocalState} of user with given id is not {@link LocalState#PHOTO}.
     * If it is sets user's photoID with given photoID
     * @param id string presentation of user id
     * @param photoID id of picture, which is going to be user's profile photo
     * @return reply to user message
     */
    public String[] processPhoto(String id, String photoID){
        String[] reply = new String[24];
        User sender = storage.getUser(id);
        if (sender.getLocalState() != LocalState.PHOTO){
            reply[0] = "Пожалуйста, отправь сообщение.";
            return reply;
        }
        sender.setPhotoID(photoID);
        sender.setLocalState(nextDict.get(LocalState.PHOTO));
        reply[0] = rightReplies.get(LocalState.PHOTO);
        if (sender.getGlobalState() == GlobalState.PROFILE_EDIT){
            reply[0] = "Изменение внесено.";
            sender.setGlobalState(GlobalState.COMMAND);
            storage.addToOPL(id);
        }
        return reply;
    }
}
