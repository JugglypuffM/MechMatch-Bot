package mainBot;

import database.UserService;
import database.models.Connection;
import database.models.User;

import java.util.ArrayList;
import java.util.List;

public class MessageProcessor {
    private final StateFSM stateFSM = new StateFSM();
    /**
     * Database interface.
     */
    private final UserService service;
    private final Notificator notificator = new Notificator();
    public MessageProcessor(){
        this.service = new UserService();
    }
    public MessageProcessor(UserService service){
        this.service = service;
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
    public List<String> getIdList(User sender){
        List<String> idList = new ArrayList<>();
        if (sender.getProfilesList().equalsIgnoreCase("лайки")){
            for (Integer i: service.getLikesOf(sender.getId())){
                idList.add(service.getConnection(i).getFriendID());
            }
        }
        else{
            for (Integer i: service.getDislikesOf(sender.getId())){
                idList.add(service.getConnection(i).getFriendID());
            }
        }
        return idList;
    }
    public void getTenProfiles(User sender, String[] reply, List<String> idList){
        int page = sender.getProfilesPage()-1;
        reply[0] = "Профили на странице " + (page+1) + ":";
        for (int i = 0; i < 10; i++){
            if (i+page*10 < idList.size()){
                reply[2+i] = "Профиль " + (1+i+page*10) + ":\n" + service.profileData(idList.get(i+page*10));
                if (sender.getProfilesList().equalsIgnoreCase("лайки")){
                    List<String> friendLikes = new ArrayList<>();
                    for (Integer j: service.getLikesOf(idList.get(i+page*10))){
                        friendLikes.add(service.getConnection(j).getFriendID());
                    }
                    if (friendLikes.contains(sender.getId())){
                        reply[2+i] = reply[2+i] + "\nВот ссылка на профиль этого пользователя - @" + service.getUser(idList.get(i+page*10)).getUsername();
                    }
                }
                reply[14+i] = service.getUser(idList.get(i+page*10)).getPhotoID();
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
        if (service.getUser(id).getExpectedCity() == null){
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
                if (service.getUser(id).getExpectedCity() != null){
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
                service.eraseProfileData(id);
                service.deleteFromFPL(id);
                sender.setProfileFilled(false);
                reply[0] = "Сейчас тебе придется пройти процедуру заполнения анкеты заново. Напиши что-нибудь, если готов.";
                sender.setGlobalState(GlobalState.PROFILE_FILL);
                sender.setLocalState(LocalState.START);
                break;
            case "/editProfile":
                service.deleteFromFPL(id);
                sender.setProfileFilled(false);
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
                User friend;
                List<String> fpl = service.getFilledProfilesList(id);
                int tmpNum = 0;
                reply[0] = "Не нашлось никого, кто соответствует твоей уникальности ;(";
                while (tmpNum < fpl.size()) {
                    friend = service.getUser(fpl.get(tmpNum));
                    boolean senderSexMatch = (sender.getExpectedSex().equals("без разницы")) || (friend.getSex().equals(sender.getExpectedSex()));
                    boolean friendSexMatch = (friend.getExpectedSex().equals("без разницы")) || (sender.getSex().equals(friend.getExpectedSex()));
                    boolean senderCityMatch = (sender.getExpectedCity().equals("любой")) || (friend.getCity().equals(sender.getExpectedCity()));
                    boolean friendCityMatch = (friend.getExpectedCity().equals("любой")) || (sender.getCity().equals(friend.getExpectedCity()));
                    boolean senderAgeMatch = (friend.getAge() <= sender.getMaxExpectedAge()) && (friend.getAge() >= sender.getMinExpectedAge());
                    boolean friendAgeMatch = (sender.getAge() <= friend.getMaxExpectedAge()) && (sender.getAge() >= friend.getMinExpectedAge());
                    List<String> friendDislikes = new ArrayList<>();
                    for (Integer i: service.getDislikesOf(friend.getId())){
                        friendDislikes.add(service.getConnection(i).getFriendID());
                    }
                    if (senderSexMatch && senderCityMatch && senderAgeMatch &&
                            friendSexMatch && friendCityMatch && friendAgeMatch &&
                            (!service.getAllConnectedUserIds(id).contains(friend.getId())) &&
                            (!friendDislikes.contains(id))) {
                        reply[0] = service.profileData(friend.getId());
                        reply[1] = "Напиши, понравился ли тебе пользователь(да/нет).";
                        reply[12] = service.getUser(fpl.get(tmpNum)).getPhotoID();
                        sender.setSuggestedFriendID(friend.getId());
                        sender.setGlobalState(GlobalState.MATCHING);
                        break;
                    }
                    tmpNum++;
                }
                break;
            case "/myProfile":
                reply[0] = service.profileData(id);
                reply[12] = sender.getPhotoID();
                break;
            case "/myMatches":
                if (service.getAllConnectionsWith(id).isEmpty()){
                    reply[0] = "Просмотренных профилей пока что нет ;(\nПопробуй ввести /match";
                    return;
                }
                reply[0] = "Какой список профилей вывести(лайки/дизлайки)?";
                reply[1] = "Также из этих списков можно будет удалить профиль по номеру на странице. " +
                        "А если удаление не требуется, то просто напиши \"выйти\"";
                sender.setGlobalState(GlobalState.MATCHES);
                sender.setLocalState(LocalState.CHOICE);
                break;
            case "/deleteProfile":
                reply[0] = "Ты уверен, что хочешь этого? Все твои данные удалятся, в том числе и список понравившихся тебе людей!";
                reply[1] = "Если ты действительно этого хочешь, то введи свое имя пользователя(то что с собачкой)";
                sender.setGlobalState(GlobalState.PROFILE_EDIT);
                sender.setLocalState(LocalState.DELETE);
                sender.setProfileFilled(false);
                break;
            case "/pending":
                if(service.getPendingOf(id).isEmpty()){
                    reply[0] = "Нет профилей, ожидающих твоего ответа.";
                    return;
                }
                reply[0] = service.profileData(service.getConnection(service.getPendingOf(id).get(0)).getFriendID());
                reply[1] = "Напиши, хочешь ли ты начать общение с эти человеком(да/нет)?.";
                reply[12] = service.getUser(service.getConnection(service.getPendingOf(id).get(0)).getFriendID()).getPhotoID();
                sender.setGlobalState(GlobalState.PENDING);
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
                    service.addToFPL(id);
                    sender.setProfileFilled(true);
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
                    reply[0] = stateFSM.getRightReplies().get(sender.getLocalState());
                    sender.setLocalState(stateFSM.getNextDict().get(sender.getLocalState()));
                } else {
                    reply[0] = stateFSM.getWrongReplies().get(sender.getLocalState());
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
            if (!stateFSM.getStateDict().containsKey(message)) {
                reply[0] = "Напиши либо цифру соответствующую полю, либо название поля.";
                return;
            }
            sender.setLocalState(stateFSM.getStateDict().get(message));
            reply[0] = stateFSM.getEditReplies().get(sender.getLocalState());
        }
        else if (sender.getLocalState() == LocalState.DELETE){
            if (message.equals(sender.getUsername()) || message.equals("@" + sender.getUsername())){
                reply[0] = "Профиль успешно удален.";
                service.deleteUser(id);
                service.deleteAllConnectionsWith(id);
                return;
            }
            reply[0] = "Введено неверное значение, процедура удаления прекращена.";
            sender.setGlobalState(GlobalState.COMMAND);
            service.addToFPL(id);
            sender.setProfileFilled(true);
        }
        else {
            if (sender.setField(message)) {
                reply[0] = "Изменение внесено.";
                sender.setGlobalState(GlobalState.COMMAND);
                service.addToFPL(id);
                sender.setProfileFilled(true);
            } else {
                reply[0] = stateFSM.getWrongReplies().get(sender.getLocalState());
            }
        }
    }
    /**
     * Matches case handler
     * Shows first ten profiles from users likes or dislikes list.
     * Pages are different depending on users reply(next/previous).
     * Data of each profile will be placed in separate array cell.
     * Offers to delete one profile by a number.
     * @param message user message
     * @param sender instance of {@link User} class representing a sender
     * @param reply array of strings with size of 12, where every string is a separate message
     */
    private void caseMatches(String message, User sender, String[] reply){
        switch (sender.getLocalState()){
            case CHOICE -> {
                if (message.equalsIgnoreCase("лайки")) {
                    if (service.getLikesOf(sender.getId()).isEmpty()) {
                        reply[0] = "Этот список пуст :(";
                        sender.setGlobalState(GlobalState.COMMAND);
                        return;
                    }
                    sender.setProfilesList(message);
                } else if (message.equalsIgnoreCase("дизлайки")) {
                    if (service.getDislikesOf(sender.getId()).isEmpty()) {
                        reply[0] = "Этот список пуст :(";
                        sender.setGlobalState(GlobalState.COMMAND);
                        return;
                    }
                    sender.setProfilesList(message);
                } else {
                    reply[0] = "Такого списка нет, введи либо \"лайки\", либо \"дизлайки\".";
                    return;
                }
                sender.setProfilesPage(1);
                getTenProfiles(sender, reply, getIdList(sender));
                sender.setLocalState(LocalState.PROFILES);
            }
            case PROFILES -> {
                switch (message) {
                    default -> {
                        try {
                            int toDelete = Integer.parseInt(message);
                            List<Integer> connectionIDs;
                            if (sender.getProfilesList().equalsIgnoreCase("лайки")) {
                                connectionIDs = service.getLikesOf(sender.getId());
                            } else {
                                connectionIDs = service.getDislikesOf(sender.getId());
                            }
                            if ((toDelete < 1) || (toDelete > connectionIDs.size())) {
                                reply[0] = "Нет профиля с таким номером.";
                                return;
                            }
                            toDelete = toDelete - 1;
                            service.deleteConnection(connectionIDs.get(toDelete));
                            reply[0] = "Профиль успешно удален из списка.";
                            sender.setProfilesList(null);
                            sender.setGlobalState(GlobalState.COMMAND);
                        } catch (NumberFormatException e) {
                            reply[0] = "Введи \"далее\" или \"назад\" для смены страниц, \"выйти\" для выхода или номер профиля, который хочешь удалить.";
                        }
                    }
                    case "далее", "назад" -> {
                        if (message.equalsIgnoreCase("далее")) {
                            if (sender.getProfilesPage() < getIdList(sender).size() / 10) {
                                sender.setProfilesPage(sender.getProfilesPage() + 1);
                            } else {
                                reply[0] = "Больше страниц нет.";
                                return;
                            }
                        } else {
                            if (sender.getProfilesPage() == 1) {
                                reply[0] = "Это первая страница.";
                                return;
                            } else {
                                sender.setProfilesPage(sender.getProfilesPage() - 1);
                            }
                            sender.setProfilesPage(sender.getProfilesPage() - 1);
                        }
                        getTenProfiles(sender, reply, getIdList(sender));
                    }
                    case "выйти" -> {
                        reply[0] = "Процедура изменения списка отменена.";
                        sender.setGlobalState(GlobalState.COMMAND);
                    }
                }
            }
        }
    }
    /**
     * Matching procedure handler.
     * Decides whether to send notification to suggested friend or write him in the black list.
     * Method is synchronized to prevent situation when users like each other at once.
     * @param id string representation of user id
     * @param message user message
     * @param sender instance of {@link User} class representing a sender
     * @param reply array of strings with size of 12, where every string is a separate message
     */
    private synchronized void caseMatching(String id, String message, User sender, String[] reply){
        if (service.getUser(sender.getSuggestedFriendID()).getSuggestedFriendID() != null){
            if (service.getUser(sender.getSuggestedFriendID()).getSuggestedFriendID().equals(sender.getId())){
                return;
            }
        }
        List<String> friendLikes = new ArrayList<>();
        for (Integer i: service.getLikesOf(sender.getSuggestedFriendID())){
            friendLikes.add(service.getConnection(i).getFriendID());
        }
        if (friendLikes.contains(id)){
            String[] notification = new String[24];
            service.addConnection(id, sender.getSuggestedFriendID(), true);
            notification[0] = "Ура! Тебе ответили взаимностью, можно переходить к общению.";
            notification[1] = "Вот ссылка на профиль собеседника - @" + sender.getUsername();
            notificator.notifyFriend(sender.getSuggestedFriendID(), service.getUser(sender.getSuggestedFriendID()).getUsername(), notification);
            reply[0] = "Ура! Этот пользователь когда-то уже отвечал взаимностью, теперь вы можете перейти к общению.";
            reply[1] = "Вот ссылка на профиль собеседника - @" + service.getUser(sender.getSuggestedFriendID()).getUsername();
        }
        else if (message.equalsIgnoreCase("да")){
            String[] notification = new String[24];
            service.addConnection(id, sender.getSuggestedFriendID(), true);
            service.addConnection(sender.getSuggestedFriendID(), id, null);
            User friend = service.getUser(sender.getSuggestedFriendID());
            friend.setGlobalState(GlobalState.PENDING);
            service.updateUser(friend);
            notification[0] = "Твой профиль понравился кое-кому.";
            notification[1] = service.profileData(id);
            notification[2] = "Напиши, хочешь ли ты начать общение с эти человеком(да/нет)?.";
            notification[13] = sender.getPhotoID();
            notificator.notifyFriend(sender.getSuggestedFriendID(), service.getUser(sender.getSuggestedFriendID()).getUsername(), notification);
            reply[0] = "Я уведомил этого пользователя, что он тебе приглянулся :)\nЕсли он ответит взаимностью, то вы сможете перейти к общению!";
        }
        else if (message.equalsIgnoreCase("нет")){
            service.addConnection(id, sender.getSuggestedFriendID(), false);
            reply[0] = "Очень жаль, в следующий раз постараюсь лучше :(";
        }
        else {
            reply[0] = "Введи да или нет.";
            return;
        }
        sender.setSuggestedFriendID(null);
        sender.setGlobalState(GlobalState.COMMAND);
    }
    /**
     * Pending users watch handler.
     * Decides whether to send usernames to both of user or write suggested user in the black list.
     * @param id string representation of user id
     * @param message user message
     * @param sender instance of {@link User} class representing a sender
     * @param reply array of strings with size of 12, where every string is a separate message
     */
    private void casePending(String id, String message, User sender, String[] reply){
        List<Integer> pending = service.getPendingOf(id);
        Connection connection = service.getConnection(pending.get(0));
        if (message.equalsIgnoreCase("да")){
            String[] notification = new String[24];
            connection.setIsLiked(true);
            service.updateConnection(connection);
            notification[0] = "Ура! Тебе ответили взаимностью, можно переходить к общению.";
            notification[1] = "Вот ссылка на профиль собеседника - @" + sender.getUsername();
            notificator.notifyFriend(connection.getFriendID(), service.getUser(connection.getFriendID()).getUsername(), notification);
            reply[0] = "Ура! Теперь вы можете перейти к общению.";
            reply[1] = "Вот ссылка на профиль собеседника - @" + service.getUser(connection.getFriendID()).getUsername();
        }
        else if (message.equalsIgnoreCase("нет")){
            connection.setIsLiked(false);
            service.updateConnection(connection);
            reply[0] = "Хорошо, больше ты этого человека не увидишь. Если только не решишь удалить его из списка не понравившихся профилей.";
        }
        else {
            reply[0] = "Введи да или нет.";
            return;
        }
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
        if (service.getUser(id) == null) {
            String username;
            if (message.startsWith("username")) {
                username = message.substring(8);
            } else {
                reply[0] = "требуется имя пользователя";
                return reply;
            }
            service.addUser(id, username);
        }
        User sender = service.getUser(id);
        switch (sender.getGlobalState()){
            case COMMAND -> caseCommand(id, message, sender, reply);
            case PROFILE_FILL -> caseProfileFill(id, message, sender, reply);
            case PROFILE_EDIT -> caseProfileEdit(id, message, sender, reply);
            case MATCHES -> caseMatches(message, sender, reply);
            case MATCHING -> caseMatching(id, message, sender, reply);
            case PENDING -> casePending(id, message, sender, reply);
        }
        service.updateUser(sender);
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
        User sender = service.getUser(id);
        if (sender.getLocalState() != LocalState.PHOTO){
            reply[0] = "Пожалуйста, отправь сообщение.";
            return reply;
        }
        sender.setPhotoID(photoID);
        sender.setLocalState(stateFSM.getNextDict().get(LocalState.PHOTO));
        if (sender.getGlobalState() == GlobalState.PROFILE_EDIT){
            reply[0] = "Изменение внесено.";
            sender.setGlobalState(GlobalState.COMMAND);
            service.addToFPL(id);
            sender.setProfileFilled(true);
        }
        else {
            reply[0] = stateFSM.getRightReplies().get(LocalState.PHOTO);
            reply[2] = service.profileData(id);
            reply[14] = sender.getPhotoID();
        }
        service.updateUser(sender);
        return reply;
    }
}
