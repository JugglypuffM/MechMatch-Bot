package mainBot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageProcessor {
    public final UserStorage storage = new UserStorage();
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
        user.setAge(0);
        user.setSex("");
        user.setCity(null);
        user.setInformation(null);
        user.setMinExpectedAge(0);
        user.setMaxExpectedAge(999);
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
    private Map<String, String> getStateDict() {
        Map<String, String> stateDict = new HashMap<>();
        stateDict.put("имя", "pe_NAME");
        stateDict.put("1", "pe_NAME");
        stateDict.put("возраст", "pe_AGE");
        stateDict.put("2", "pe_AGE");
        stateDict.put("пол", "pe_SEX");
        stateDict.put("3", "pe_SEX");
        stateDict.put("город", "pe_CITY");
        stateDict.put("4", "pe_CITY");
        stateDict.put("информация о себе", "pe_ABOUT");
        stateDict.put("5", "pe_ABOUT");
        stateDict.put("нижний порог возраста собеседника", "pe_EAGEMIN");
        stateDict.put("6", "pe_EAGEMIN");
        stateDict.put("верхний порог возраста собеседника", "pe_EAGEMAX");
        stateDict.put("7", "pe_EAGEMAX");
        stateDict.put("пол собеседника", "pe_ESEX");
        stateDict.put("8", "pe_ESEX");
        stateDict.put("город собеседника", "pe_ECITY");
        stateDict.put("9", "pe_ECITY");
        return stateDict;
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
        int age;
        switch (sender.getGlobalState()){
            case "default":
                if (storage.getUser(id).getExpectedCity() == null){
                    message = "/start";
                }
                if (!(message.charAt(0) == '/')){
                    answer[0] = "Что-то я тебя не понимаю, если не знаешь что я умею - введи /help";
                    return answer;
                }
                switch (message){
                    default:
                        answer[0] = "Такой команды нет, введи /help, чтобы увидеть список всех команд";
                        return answer;
                    case "/start":
                        if (storage.getUser(id).getExpectedCity() != null){
                            answer[0] = giveHelp();
                            return answer;
                        }
                        answer[0] = "Привет! Ты попал на MechMatch - место, где тебе помогут найти твою вторую половинку или просто хорошего друга :)  ";
                        answer[1] = """
                        Перед началом хочется тебя предупредить, что бот никак не идентифицирует пользователя по документам, поэтому будь осторожен!\s
                        Отправь любое сообщение, чтобы подтвердить прочтение предупреждения.
                        """;
                        sender.setGlobalState("profile_fill");
                        sender.setLocalState("pf_START");
                        return answer;
                    case "/help":
                        answer[0] = giveHelp();
                        return answer;
                    case "/changeProfile":
                        eraseProfileData(id);
                        storage.deleteFromOPL(id);
                        answer[0] = "Сейчас тебе придется пройти процедуру заполнения анкеты заново. Напиши что-нибудь, если готов.";
                        sender.setGlobalState("profile_fill");
                        sender.setLocalState("pf_START");
                        return answer;
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
                        sender.setGlobalState("profile_edit");
                        sender.setLocalState("pe_START");
                        return answer;
                    case "/match":
                        if (storage.getOtherProfilesList(id).isEmpty()){
                            answer[0] = "Кроме тебя пока никого нет ;(";
                            return answer;
                        }
                        answer[0] = "Какую страницу анкет вывести(Всего: " + (storage.getOtherProfilesList(id).size()/10 + 1) + ")?";
                        sender.setGlobalState("matching");
                        return answer;
                    case "/myProfile":
                        answer[0] = profileData(id);
                        return answer;
                }
            case "profile_fill":
                switch (sender.getLocalState()){
                    case "pf_START":
                        answer[0] = "А теперь перейдем к заполнению анкеты.";
                        answer[1] = "Введи свое имя";
                        sender.setLocalState("pf_NAME");
                        break;
                    case "pf_NAME":
                        sender.setName(message);
                        answer[0] = "Отлично, теперь напиши цифрами, сколько тебе лет.";
                        sender.setLocalState("pf_AGE");
                        break;
                    case "pf_AGE":
                        try {
                            age = Integer.parseInt(message);
                        }catch (NumberFormatException e){
                            answer[0] = "Пожалуйста, введи ответ цифрами.";
                            return answer;
                        }
                        if (sender.setAge(age)){
                            answer[0] = "Теперь укажи свой пол(Парень/Девушка).";
                            sender.setLocalState("pf_SEX");
                        }else {
                            answer[0] = "Этот возраст выглядит неправдоподобно, введи значение между 15 и 119.";
                        }
                        break;
                    case "pf_SEX":
                        if (sender.setSex(message)){
                            answer[0] = "Хорошо, напиши название города, в котором живешь.";
                            sender.setLocalState("pf_CITY");
                        }
                        else {
                            answer[0] = "Ввведи один из двух ответов: парень или девушка.";
                        }
                        break;
                    case "pf_CITY":
                        sender.setCity(message);
                        answer[0] = "Расскажи о себе в одном сообщении, возможно это поможет подобрать тебе наиболее интересного собеседника.";
                        sender.setLocalState("pf_ABOUT");
                        break;
                    case "pf_ABOUT":
                        sender.setInformation(message);
                        answer[0] = "Теперь напиши минимальный возраст твоего потенциального собеседника.";
                        sender.setLocalState("pf_EAGEMIN");
                        break;
                    case "pf_EAGEMIN":
                        try {
                            age = Integer.parseInt(message);
                        }catch (NumberFormatException e){
                            answer[0] = "Пожалуйста, введи ответ цифрами.";
                            return answer;
                        }
                        if (sender.setMinExpectedAge(age)){
                            answer[0] = "Теперь напиши максимальный возраст твоего потенциального собеседника.";
                            sender.setLocalState("pf_EAGEMAX");
                        }else {
                            answer[0] = "Этот возраст выглядит неправдоподобно, введи значение между 15 и 119, а еще оно должно быть не больше максимального.";
                        }
                        break;
                    case "pf_EAGEMAX":
                        try {
                            age = Integer.parseInt(message);
                        }catch (NumberFormatException e){
                            answer[0] = "Пожалуйста, введи ответ цифрами.";
                            return answer;
                        }
                        if (sender.setMaxExpectedAge(age)){
                            answer[0] = "Теперь укажи пол твоего потенциального собеседника(Парень/Девушка/Без разницы).";
                            sender.setLocalState("pf_ESEX");
                        }else {
                            answer[0] = "Этот возраст выглядит неправдоподобно, введи значение между 15 и 119, а еще оно должно быть не меньше минимального.";
                        }
                        break;
                    case "pf_ESEX":
                        if (sender.setExpectedSex(message)){
                            answer[0] = "Хорошо, напиши название города, в котором хочешь найти собеседника.";
                            sender.setLocalState("pf_ECITY");
                        }else {
                            answer[0] = "Ввведи один из трех ответов: парень, девушка или без разницы.";
                        }
                        break;
                    case "pf_ECITY":
                        sender.setExpectedCity(message);
                        answer[0] = "Посмотри свою анкету еще раз, всё ли верно? Ответь да или нет.";
                        answer[2] = profileData(id);
                        sender.setLocalState("pf_FINISH");
                        break;
                    case "pf_FINISH":
                        if (message.equalsIgnoreCase("да")){
                            storage.addToOPL(id);
                            answer[0] = "Отлично, теперь можно переходить к использованию.";
                            answer[1] = giveHelp();
                            sender.setGlobalState("default");
                        }
                        else if (message.equalsIgnoreCase("нет")){
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
                            sender.setGlobalState("profile_edit");
                            sender.setLocalState("pe_START");
                            break;
                        }
                        else{
                            answer[0] = "Пожалуйста, напиши либо да, либо нет";
                        }
                        break;
                }
                break;
            case "profile_edit":
                answer[0] = "Изменение внесено.";
                switch (sender.getLocalState()){
                    case "pe_START":
                        Map<String, String> stateDict = getStateDict();
                        answer[0] = "Введи новое значение.";
                        if (!stateDict.containsKey(message)){
                            answer[0] = "Напиши либо цифру соответствующую полю, либо название поля";
                            return answer;
                        }
                        sender.setLocalState(stateDict.get(message));
                        break;
                    case "pe_NAME":
                        sender.setName(message);
                        sender.setGlobalState("default");
                        storage.addToOPL(id);
                        break;
                    case "pe_AGE":
                        try {
                            age = Integer.parseInt(message);
                        }
                        catch (NumberFormatException e){
                            answer[0] = "Пожалуйста, введи ответ цифрами.";
                            return answer;
                        }
                        if (sender.setAge(age)){
                            sender.setGlobalState("default");
                            storage.addToOPL(id);
                        }
                        else {
                            answer[0] = "Этот возраст выглядит неправдоподобно, введи значение между 15 и 119.";
                        }
                        break;
                    case "pe_SEX":
                        if (sender.setSex(message)){
                            sender.setGlobalState("default");
                            storage.addToOPL(id);
                        }
                        else {
                            answer[0] = "Ввведи один из двух ответов: парень или девушка.";
                        }
                        break;
                    case "pe_CITY":
                        sender.setCity(message);
                        sender.setGlobalState("default");
                        storage.addToOPL(id);
                        break;
                    case "pe_ABOUT":
                        sender.setInformation(message);
                        sender.setGlobalState("default");
                        storage.addToOPL(id);
                        break;
                    case "pe_EAGEMIN":
                        try {
                            age = Integer.parseInt(message);
                        }catch (NumberFormatException e){
                            answer[0] = "Пожалуйста, введи ответ цифрами.";
                            return answer;
                        }
                        if (sender.setMinExpectedAge(age)){
                            sender.setGlobalState("default");
                            storage.addToOPL(id);
                        }else {
                            answer[0] = "Этот возраст выглядит неправдоподобно, введи значение между 15 и 119.";
                        }
                        break;
                    case "pe_EAGEMAX":
                        try {
                            age = Integer.parseInt(message);
                        }catch (NumberFormatException e){
                            answer[0] = "Пожалуйста, введи ответ цифрами.";
                            return answer;
                        }
                        if (sender.setMaxExpectedAge(age)){
                            sender.setGlobalState("default");
                            storage.addToOPL(id);
                        }else {
                            answer[0] = "Этот возраст выглядит неправдоподобно, введи значение между 15 и 119.";
                        }
                        break;
                    case "pe_ESEX":
                        if (sender.setExpectedSex(message)){
                            sender.setGlobalState("default");
                            storage.addToOPL(id);
                        }else {
                            answer[0] = "Ввведи один из трех ответов: парень, девушка или без разницы.";
                        }
                        break;
                    case "pe_ECITY":
                        sender.setExpectedCity(message);
                        answer[2] = profileData(id);
                        sender.setGlobalState("default");
                        storage.addToOPL(id);
                        break;
                }
                break;
            case "matching":
                int page;
                try {
                    page = Integer.parseInt(message);
                }catch (NumberFormatException e){
                    answer[0] = "Пожалуйста, введи ответ цифрами.";
                    return answer;
                }
                if ((page < 1) || (page > storage.getOtherProfilesList(id).size()/10 + 1)){
                    answer[0] = "Нет страницы с таким номером.";
                    return answer;
                }
                answer[0] = "Профили на странице " + message + ":";
                getTenProfiles(answer, page - 1, id);
                sender.setGlobalState("default");
                break;
        }
        return answer;
    }
}
