package logic.commandHandlers;

import database.main.Database;
import database.models.User;
import logic.states.GlobalState;
import logic.states.LocalState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Command handler.
 * Changes state depending on command.
 * If message does not start with '/' or is not a supported command - offers to watch command list.
 */
public class CommandHandler implements Handler{
    private final Database database;
    public CommandHandler(Database m_database){
        this.database = m_database;
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
        switch (message.toLowerCase()){
            default:
                if (database.getUser(sender.getId()).isProfileFilled()){
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
            case "/help", "описание команд":
                reply[0] = giveHelp();
                break;
            case "/changeprofile", "заполнить заново":
                database.deleteFromFPL(sender.getId());
                reply[0] = "Сейчас тебе придется пройти процедуру заполнения анкеты заново. Напиши что-нибудь, если готов.";
                sender.setMaxExpectedAge("999");
                sender.setGlobalState(GlobalState.PROFILE_FILL);
                sender.setLocalState(LocalState.START);
                break;
            case "/editprofile", "изменить профиль":
                database.deleteFromFPL(sender.getId());
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
            case "/match", "подбор собеседника":
                User friend;
                List<String> fpl = database.getFilledProfilesList(sender.getId());
                int tmpNum = 0;
                reply[0] = "Не нашлось никого, кто соответствует твоей уникальности ;(";
                while (tmpNum < fpl.size()) {
                    friend = database.getUser(fpl.get(tmpNum));
                    boolean senderSexMatch = (sender.getExpectedSex().equalsIgnoreCase("без разницы")) || (friend.getSex().equals(sender.getExpectedSex()));
                    boolean friendSexMatch = (friend.getExpectedSex().equalsIgnoreCase("без разницы")) || (sender.getSex().equals(friend.getExpectedSex()));
                    boolean senderCityMatch = (sender.getExpectedCity().equalsIgnoreCase("любой")) || (friend.getCity().equalsIgnoreCase(sender.getExpectedCity()));
                    boolean friendCityMatch = (friend.getExpectedCity().equalsIgnoreCase("любой")) || (sender.getCity().equalsIgnoreCase(friend.getExpectedCity()));
                    boolean senderAgeMatch = (friend.getAge() <= sender.getMaxExpectedAge()) && (friend.getAge() >= sender.getMinExpectedAge());
                    boolean friendAgeMatch = (sender.getAge() <= friend.getMaxExpectedAge()) && (sender.getAge() >= friend.getMinExpectedAge());
                    List<String> friendDislikes = new ArrayList<>();
                    for (Integer i: database.getDislikesOf(friend.getId())){
                        friendDislikes.add(database.getConnection(i).getFriendID());
                    }
                    if (senderSexMatch && senderCityMatch && senderAgeMatch &&
                            friendSexMatch && friendCityMatch && friendAgeMatch &&
                            !database.getAllConnectedUserIds(sender.getId()).contains(friend.getId()) &&
                            !friendDislikes.contains(sender.getId()) &&
                            !(Objects.equals(friend.getSuggestedFriendID(), sender.getId()))) {
                        reply[0] = database.profileData(friend.getId());
                        reply[1] = "Напиши, понравился ли тебе пользователь(да/нет).";
                        reply[12] = database.getUser(fpl.get(tmpNum)).getPhotoID();
                        sender.setSuggestedFriendID(friend.getId());
                        sender.setGlobalState(GlobalState.MATCHING);
                        break;
                    }
                    tmpNum++;
                }
                break;
            case "/myprofile", "мой профиль":
                reply[0] = database.profileData(sender.getId());
                reply[12] = sender.getPhotoID();
                break;
            case "/mymatches", "просмотренные профили":
                if (database.getAllConnectionsWith(sender.getId()).isEmpty()){
                    reply[0] = "Просмотренных профилей пока что нет ;(\nПопробуй ввести /match";
                    return;
                }
                reply[0] = "Какой список профилей вывести(лайки/дизлайки)?";
                reply[1] = "Также из этих списков можно будет удалить профиль по номеру на странице. " +
                        "А если удаление не требуется, то просто напиши \"выйти\"";
                sender.setGlobalState(GlobalState.MATCHES);
                sender.setLocalState(LocalState.CHOICE);
                break;
            case "/deleteprofile", "удалить профиль":
                reply[0] = "Ты уверен, что хочешь этого? Все твои данные удалятся, в том числе и список понравившихся тебе людей!";
                reply[1] = "Если ты действительно этого хочешь, то введи свое имя пользователя(то что с собачкой)";
                sender.setGlobalState(GlobalState.PROFILE_EDIT);
                sender.setLocalState(LocalState.DELETE);
                database.deleteFromFPL(sender.getId());
                break;
            case "/pending", "ожидающие ответа":
                if(database.getPendingOf(sender.getId()).isEmpty()){
                    reply[0] = "Нет профилей, ожидающих твоего ответа.";
                    return;
                }
                reply[0] = database.profileData(database.getConnection(database.getPendingOf(sender.getId()).get(0)).getFriendID());
                reply[1] = "Напиши, хочешь ли ты начать общение с эти человеком(да/нет)?.";
                reply[12] = database.getUser(database.getConnection(database.getPendingOf(sender.getId()).get(0)).getFriendID()).getPhotoID();
                sender.setGlobalState(GlobalState.PENDING);
                break;
        }
    }
}
