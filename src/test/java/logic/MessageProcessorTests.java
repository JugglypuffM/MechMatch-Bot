package logic;

import bots.platforms.Platform;
import database.main.Database;
import database.main.DatabaseMock;
import database.entities.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MessageProcessorTests {
    /**
     * Instance of {@link Database}.
     * Uses mock database realization.
     */
    private Database database;
    /**
     * Instance of {@link MessageProcessor}.
     * Used to process all test messages.
     */
    private MessageProcessor processor;
    final String platformId = "0";
    final Platform platform = Platform.TELEGRAM;

    /**
     * Utility method to register an account
     * @param login login of new account
     * @param platformId platform id
     * @param platform platform
     * @param processor message processor
     */
    public void registerAccount(String login, String platformId, Platform platform, MessageProcessor processor){
        processor.processMessage(platformId,  platform,"/register");
        processor.processMessage(platformId,  platform,login);
        processor.processMessage(platformId,  platform,"password");
        Account account = database.getAccountWithLogin(login);
        account.setPlatformId(platformId, platform);
        account.setPlatformUsername("stas" + platformId, platform);
        database.updateAccount(account);
    }
    /**
     * Utility method to fill all profile data at once
     * @param platformId user id
     * @param processor instance of {@link MessageProcessor}
     */
    public void fillProfile(String platformId, Platform platform, MessageProcessor processor){
        processor.processMessage(platformId,  platform,"/start");
        processor.processMessage(platformId,  platform,"Стас");
        processor.processMessage(platformId,  platform,"Стас");
        processor.processMessage(platformId,  platform,"19");
        processor.processMessage(platformId,  platform,"Парень");
        processor.processMessage(platformId,  platform,"Екатеринбург");
        processor.processMessage(platformId,  platform,"просто круд");
        processor.processMessage(platformId,  platform,"17");
        processor.processMessage(platformId,  platform,"23");
        processor.processMessage(platformId,  platform,"без разницы");
        processor.processMessage(platformId,  platform,"любой");
        processor.processPhoto(platformId,  platform,"Екатеринбург");
        processor.processMessage(platformId,  platform,"да");
    }

    /**
     * Initialization of {@link MessageProcessor} and basic user with id 0
     */
    @BeforeEach
    public void initialize(){
        this.database = new DatabaseMock();
        this.processor = new MessageProcessor(database, null);
        processor.processMessage(platformId,  platform,"/register");
        processor.processMessage(platformId,  platform,"stas0");
        processor.processMessage(platformId,  platform,"password");
        Account account = database.getAccountWithLogin("stas0");
        account.setPlatformId(platformId, platform);
        account.setPlatformUsername("stas0", platform);
        database.updateAccount(account);
        processor.processMessage(platformId,  platform,"/start");
        processor.processMessage(platformId,  platform,"Стас");
        processor.processMessage(platformId,  platform,"Стас");
        processor.processMessage(platformId,  platform,"19");
        processor.processMessage(platformId,  platform,"Парень");
        processor.processMessage(platformId,  platform,"Екатеринбург");
        processor.processMessage(platformId,  platform,"просто круд");
        processor.processMessage(platformId,  platform,"17");
        processor.processMessage(platformId,  platform,"23");
        processor.processMessage(platformId,  platform,"без разницы");
        processor.processMessage(platformId,  platform,"любой");
        processor.processPhoto(platformId,  platform,"Екатеринбург");
    }
    /**
     * Test of profile filling procedure.
     * Tests if states switch correctly and data stores appropriately
     */
    @Test
    public void profileFillTest(){
        processor.processMessage(platformId,  platform,"да");
        String[] reply = processor.processMessage(platformId,  platform,"/myProfile");
        Assertions.assertEquals("""
                Имя: Стас
                Возраст: 19
                Пол: парень
                Город: Екатеринбург
                Информация о себе: просто круд
                Диапазон возраста собеседника: 17 - 23
                Пол собеседника: без разницы
                Город собеседника: любой""", reply[0]);
        Assertions.assertEquals("Екатеринбург", reply[12]);
    }

    /**
     * Test of transition from fill to edit
     */
    @Test
    public void editAfterFillTest(){
        String[] reply = processor.processMessage(platformId,  platform,"нет");
        Assertions.assertEquals("Что хочешь изменить?", reply[0]);
        Assertions.assertEquals("""
                Вот список полей доступных для изменения:\s
                1 - Имя(Стас)
                2 - Возраст(19)
                3 - Пол(парень)
                4 - Город(Екатеринбург)
                5 - Информация о себе(просто круд)
                6 - Нижний порог возраста собеседника(17)
                7 - Верхний порог возраста собеседника(23)
                8 - Пол собеседника(без разницы)
                9 - Город собеседника(любой)
                10 - Фото""", reply[1]);
        processor.processMessage(platformId,  platform,"2");
        processor.processMessage(platformId,  platform,"18");
    }

    /**
     * Test of profile editing procedure.
     * Tests state switching and appropriate data storage
     */
    @Test
    public void profileEditTest(){
        processor.processMessage(platformId,  platform,"да");
        processor.processMessage(platformId,  platform,"/editProfile");
        Assertions.assertEquals("Напиши либо цифру соответствующую полю, либо название поля.", processor.processMessage(platformId,  platform,"svfand")[0]);
        Assertions.assertEquals("Напиши цифрами новый возраст.", processor.processMessage(platformId,  platform,"2")[0]);
        Assertions.assertEquals("Изменение внесено.", processor.processMessage(platformId,  platform,"18")[0]);
        Assertions.assertEquals("""
                Имя: Стас
                Возраст: 18
                Пол: парень
                Город: Екатеринбург
                Информация о себе: просто круд
                Диапазон возраста собеседника: 17 - 23
                Пол собеседника: без разницы
                Город собеседника: любой""", processor.processMessage(platformId,  platform,"/myProfile")[0]);
    }

    /**
     * Test for photo editing case.
     * Tests if processPhoto method works correctly
     */
    @Test
    public void photoEditTest(){
        processor.processMessage(platformId,  platform,"да");
        processor.processMessage(platformId,  platform,"/editProfile");
        processor.processMessage(platformId,  platform,"10");
        Assertions.assertEquals("Пожалуйста, отправь картинку.", processor.processMessage(platformId,  platform,"dfgsdfgsdfg")[0]);
        Assertions.assertEquals("Изменение внесено.", processor.processPhoto(platformId,  platform,"dfgsdfgsdfg")[0]);
        Assertions.assertEquals("dfgsdfgsdfg", processor.processMessage(platformId,  platform,"/myProfile")[12]);
    }

    /**
     * Test for profile change procedure.
     * Edit added just in case it may work wrong.
     */
    @Test
    public void changeProfileTest(){
        processor.processMessage(platformId,  platform,"да");
        processor.processMessage(platformId,  platform,"/editProfile");
        processor.processMessage(platformId,  platform,"2");
        processor.processMessage(platformId,  platform,"18");
        processor.processMessage(platformId,  platform,"/changeProfile");
        processor.processMessage(platformId,  platform,"Стас");
        processor.processMessage(platformId,  platform,"сатС");
        processor.processMessage(platformId,  platform,"91");
        processor.processMessage(platformId,  platform,"Девушка");
        processor.processMessage(platformId,  platform,"грубниретакЕ");
        processor.processMessage(platformId,  platform,"дурк отсорп");
        processor.processMessage(platformId,  platform,"71");
        processor.processMessage(platformId,  platform,"82");
        processor.processMessage(platformId,  platform,"Парень");
        processor.processMessage(platformId,  platform,"грубниретакЕ");
        processor.processPhoto(platformId,  platform,"грубниретакЕ");
        processor.processMessage(platformId,  platform,"да");
        Assertions.assertEquals("""
                Имя: сатС
                Возраст: 91
                Пол: девушка
                Город: грубниретакЕ
                Информация о себе: дурк отсорп
                Диапазон возраста собеседника: 71 - 82
                Пол собеседника: парень
                Город собеседника: грубниретакЕ""", processor.processMessage(platformId,  platform,"/myProfile")[0]);
        Assertions.assertEquals("грубниретакЕ", processor.processMessage(platformId,  platform,"/myProfile")[12]);
    }

    /**
     * Test of matching procedure.
     */
    @Test
    public void matchingFailTest(){
        processor.processMessage(platformId,  platform,"да");
        registerAccount("stas1", "1", Platform.TELEGRAM, processor);
        fillProfile("1", platform, processor);
        processor.processMessage("1", platform, "/editProfile");
        processor.processMessage("1", platform, "8");
        processor.processMessage("1", platform, "девушка");
        Assertions.assertEquals("Пока нет никого, кто соответствует твоей уникальности ;(", processor.processMessage("0",  platform,"/match")[0]);
    }

    /**
     * Test case for mutual like of two users
     */
    @Test
    public void matchingLikeLikeTest(){
        processor.processMessage(platformId,  platform,"да");
        registerAccount("stas1", "1", Platform.TELEGRAM, processor);
        fillProfile("1", platform, processor);
        Assertions.assertEquals(processor.processMessage("1", platform, "/myProfile")[0],
                processor.processMessage("0", platform, "/match")[0]);
        Assertions.assertEquals("Введи да или нет.", processor.processMessage("0",
                platform, "дварыера")[0]);
        Assertions.assertEquals("Я уведомил этого пользователя, что он тебе приглянулся :)\n" +
                "Если он ответит взаимностью, то вы сможете перейти к общению!",
                processor.processMessage("0", platform, "да")[0]);
        Assertions.assertEquals(processor.processMessage("0", platform, "/myProfile")[0],
                processor.processMessage("1", platform, "/pending")[0]);
        Assertions.assertEquals("Введи да или нет.", processor.processMessage("1", platform, "дварыера")[0]);
        String[] reply = processor.processMessage("1", platform, "да");
        Assertions.assertEquals("Ура! Теперь вы можете перейти к общению.", reply[0]);
        Assertions.assertEquals("Вот имена этого пользователя на разных платформах:\nTELEGRAM - @stas0\n", reply[1]);
    }

    /**
     * Test case for rejection by second user
     */
    @Test
    public void matchingLikeDislikeTest(){
        processor.processMessage(platformId, platform, "да");
        registerAccount("stas1", "1", Platform.TELEGRAM, processor);
        fillProfile("1", platform, processor);
        Assertions.assertEquals(processor.processMessage("1", platform, "/myProfile")[0], processor.processMessage("0", platform, "/match")[0]);
        Assertions.assertEquals("Я уведомил этого пользователя, что он тебе приглянулся :)\nЕсли он ответит взаимностью, то вы сможете перейти к общению!", processor.processMessage("0", platform, "да")[0]);
        processor.processMessage("1", platform, "/pending");
        String[] reply = processor.processMessage("1", platform, "нет");
        Assertions.assertEquals("Хорошо, больше ты этого человека не увидишь. Если только не решишь удалить его из списка не понравившихся профилей.", reply[0]);
    }

    /**
     * Test case for rejection by first user
     */
    @Test
    public void matchingDislikeTest(){
        processor.processMessage(platformId, platform, "да");
        registerAccount("stas1", "1", Platform.TELEGRAM, processor);
        fillProfile("1", platform, processor);
        Assertions.assertEquals(processor.processMessage("1", platform, "/myProfile")[0], processor.processMessage("0", platform, "/match")[0]);
        Assertions.assertEquals("Очень жаль, в следующий раз постараюсь лучше :(", processor.processMessage("0", platform, "нет")[0]);
    }

    /**
     * Test case for two users match use at the same time
     */
    @Test
    public void TwoUsersMatchTest(){
        processor.processMessage(platformId, platform, "да");
        registerAccount("stas1", "1", Platform.TELEGRAM, processor);
        fillProfile("1", platform, processor);
        processor.processMessage(platformId, platform, "/match");
        Assertions.assertEquals("Пока нет никого, кто соответствует твоей уникальности ;(",
                processor.processMessage("1", platform, "/match")[0]);
        Assertions.assertEquals("Я уведомил этого пользователя, что он тебе приглянулся :)\n" +
                "Если он ответит взаимностью, то вы сможете перейти к общению!",
                processor.processMessage("0", platform, "да")[0]);
        processor.processMessage("1", platform, "/pending");
        Assertions.assertEquals("Ура! Теперь вы можете перейти к общению.",
                processor.processMessage("1", platform, "да")[0]);
    }
    @Test
    public void repeatedLikeTest(){
        processor.processMessage(platformId, platform, "да");
        registerAccount("stas1", "1", Platform.TELEGRAM, processor);
        fillProfile("1", platform, processor);
        processor.processMessage(platformId, platform, "/match");
        processor.processMessage(platformId, platform, "да");
        processor.processMessage("1", platform, "/pending");
        processor.processMessage("1", platform, "да");
        processor.processMessage("1", platform, "/myMatches");
        processor.processMessage("1", platform, "лайки");
        processor.processMessage("1", platform, "1");
        processor.processMessage("1", platform, "/match");
        Assertions.assertEquals("Вы уже отвечали друг другу взаимностью, продолжайте общение!", processor.processMessage("1", platform, "да")[0]);
    }
    /**
     * Test of /myMatches command
     */
    @Test
    public void myMatchesTest(){
        processor.processMessage(platformId, platform, "да");
        registerAccount("stas1", "1", Platform.TELEGRAM, processor);
        fillProfile("1", platform, processor);
        processor.processMessage("0", platform, "/editProfile");
        processor.processMessage("0", platform, "8");
        processor.processMessage("0", platform, "парень");
        processor.processMessage("1", platform, "/editProfile");
        processor.processMessage("1", platform, "8");
        processor.processMessage("1", platform, "парень");
        Assertions.assertEquals("Просмотренных профилей пока что нет ;(\nПопробуй ввести /match", processor.processMessage("0", platform, "/myMatches")[0]);
        processor.processMessage("0", platform, "/match");
        processor.processMessage("0", platform, "да");
        processor.processMessage("1", platform, "/pending");
        processor.processMessage("1", platform, "да");
        Assertions.assertEquals("Какой список профилей вывести(лайки/дизлайки)?", processor.processMessage("0", platform, "/myMatches")[0]);
        Assertions.assertEquals("Такого списка нет, введи либо \"лайки\", либо \"дизлайки\". Или \"выйти\", если передумал.", processor.processMessage("0", platform, "ыважлпдлавп")[0]);
        Assertions.assertEquals("Этот список пуст :(", processor.processMessage("0", platform, "дизлайки")[0]);
        processor.processMessage("0", platform, "/myMatches");
        String[] reply = processor.processMessage("0", platform, "лайки");
        Assertions.assertEquals("Профили на странице 1:", reply[0]);
        Assertions.assertEquals("Профиль 1:\n" + processor.processMessage("1", platform, "/myProfile")[0] +
                "\nВот имена этого пользователя на разных платформах:\n" +
                "TELEGRAM - @stas1\n", reply[2]);
        Assertions.assertEquals("Больше страниц нет.", processor.processMessage("0", platform, "далее")[0]);
        Assertions.assertEquals("Это первая страница.", processor.processMessage("0", platform, "назад")[0]);
        Assertions.assertEquals("Введи \"далее\" или \"назад\" для смены страниц, \"выйти\" для выхода или номер профиля, который хочешь удалить.", processor.processMessage("0", platform, "выфжадвыьфа")[0]);
        Assertions.assertEquals("Нет профиля с таким номером.", processor.processMessage("0", platform, "2")[0]);
        Assertions.assertEquals("Профиль успешно удален из списка.", processor.processMessage("0", platform, "1")[0]);
        Assertions.assertEquals("Просмотренных профилей пока что нет ;(\nПопробуй ввести /match", processor.processMessage("0", platform, "/myMatches")[0]);
    }
    /**
     * User deletion command test
     */
    @Test
    public void deleteTest(){
        processor.processMessage(platformId, platform, "да");
        Assertions.assertEquals("Ты уверен, что хочешь этого? Все твои данные удалятся, в том числе и список понравившихся тебе людей!", processor.processMessage(platformId, platform, "/deleteProfile")[0]);
        Assertions.assertEquals("Введено неверное значение, процедура удаления прекращена.", processor.processMessage(platformId, platform, "stas1")[0]);
        processor.processMessage(platformId, platform, "/deleteProfile");
        Assertions.assertEquals("Профиль успешно удален.", processor.processMessage(platformId, platform, "stas0")[0]);
        Assertions.assertEquals("Похоже ты еще не авторизован, поэтому тебе доступны только команды:\n    /login - войти в существующую учетную запись\n    /register - создать новую учетную запись\n    /cancel - доступна после отправки одной из вышеперечисленных команд, отменяет процедуру и возвращает обратно в это меню\n", processor.processMessage(platformId, platform, "/start")[0]);
    }
}
