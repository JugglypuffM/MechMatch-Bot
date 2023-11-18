package mainBot;

import database.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MessageProcessorTests {
    MessageProcessor processor;
    String id;
    /**
     * Utility method to fill all profile data at once
     * @param id user id
     * @param processor instance of {@link MessageProcessor}
     */
    public void fillProfile(String id, MessageProcessor processor){
        processor.processMessage(id, "/start");
        processor.processMessage(id, "usernamestas");
        processor.processMessage(id, "Стас");
        processor.processMessage(id, "Стас");
        processor.processMessage(id, "19");
        processor.processMessage(id, "Парень");
        processor.processMessage(id, "Екатеринбург");
        processor.processMessage(id, "просто круд");
        processor.processMessage(id, "17");
        processor.processMessage(id, "23");
        processor.processMessage(id, "Девушка");
        processor.processMessage(id, "Екатеринбург");
        processor.processPhoto(id, "Екатеринбург");
        processor.processMessage(id, "да");
    }

    /**
     * Initialization of {@link MessageProcessor} and basic user with id 0
     */
    @BeforeEach
    public void initialize(){
        this.id = "0";
        this.processor = new MessageProcessor();
        processor.processMessage(id, "/start");
        processor.processMessage(id, "usernamestas");
        processor.processMessage(id, "Стас");
        processor.processMessage(id, "Стас");
        processor.processMessage(id, "19");
        processor.processMessage(id, "Парень");
        processor.processMessage(id, "Екатеринбург");
        processor.processMessage(id, "просто круд");
        processor.processMessage(id, "17");
        processor.processMessage(id, "23");
        processor.processMessage(id, "Девушка");
        processor.processMessage(id, "Екатеринбург");
        processor.processPhoto(id, "Екатеринбург");
    }
    @AfterEach
    public void deleteUsers(){
        processor.processMessage(id, "/deleteProfile");
        processor.processMessage(id, "stas");
        processor.processMessage("1", "/deleteProfile");
        processor.processMessage("1", "stas");
        processor.processMessage("2", "/deleteProfile");
        processor.processMessage("2", "stas");
    }
    /**
     * Test of profile filling procedure.
     * Tests if states switch correctly and data stores appropriately
     */
    @Test
    public void profileFillTest(){
        processor.processMessage(id, "да");
        String[] reply = processor.processMessage(id, "/myProfile");
        Assertions.assertEquals("""
                Имя: Стас
                Возраст: 19
                Пол: парень
                Город: Екатеринбург
                Информация о себе: просто круд
                Диапазон возраста собеседника: 17 - 23
                Пол собеседника: девушка
                Город собеседника: Екатеринбург""", reply[0]);
        Assertions.assertEquals("Екатеринбург", reply[12]);
    }

    /**
     * Test of transition from fill to edit
     */
    @Test
    public void editAfterFillTest(){
        String[] reply = processor.processMessage(id, "нет");
        Assertions.assertEquals("Что хочешь изменить?", reply[0]);
        Assertions.assertEquals("Вот список полей доступных для изменения: \n" +
                "1 - Имя(Стас)\n" +
                "2 - Возраст(19)\n" +
                "3 - Пол(парень)\n" +
                "4 - Город(Екатеринбург)\n" +
                "5 - Информация о себе(просто круд)\n" +
                "6 - Нижний порог возраста собеседника(17)\n" +
                "7 - Верхний порог возраста собеседника(23)\n" +
                "8 - Пол собеседника(девушка)\n" +
                "9 - Город собеседника(Екатеринбург)\n" +
                "10 - Фото", reply[1]);
        processor.processMessage(id, "2");
        processor.processMessage(id, "18");
    }

    /**
     * Test of profile editing procedure.
     * Tests state switching and appropriate data storage
     */
    @Test
    public void profileEditTest(){
        processor.processMessage(id, "да");
        processor.processMessage(id, "/editProfile");
        Assertions.assertEquals("Напиши либо цифру соответствующую полю, либо название поля.", processor.processMessage(id, "svfand")[0]);
        Assertions.assertEquals("Напиши цифрами новый возраст.", processor.processMessage(id, "2")[0]);
        Assertions.assertEquals("Изменение внесено.", processor.processMessage(id, "18")[0]);
        Assertions.assertEquals("""
                Имя: Стас
                Возраст: 18
                Пол: парень
                Город: Екатеринбург
                Информация о себе: просто круд
                Диапазон возраста собеседника: 17 - 23
                Пол собеседника: девушка
                Город собеседника: Екатеринбург""", processor.processMessage(id, "/myProfile")[0]);
    }

    /**
     * Test for photo editing case.
     * Tests if processPhoto method works correctly
     */
    @Test
    public void photoEditTest(){
        processor.processMessage(id, "да");
        processor.processMessage(id, "/editProfile");
        processor.processMessage(id, "10");
        Assertions.assertEquals("Пожалуйста, отправь картинку.", processor.processMessage(id, "dfgsdfgsdfg")[0]);
        Assertions.assertEquals("Изменение внесено.", processor.processPhoto(id, "dfgsdfgsdfg")[0]);
        Assertions.assertEquals("dfgsdfgsdfg", processor.processMessage(id, "/myProfile")[12]);
    }

    /**
     * Test for profile change procedure.
     * Edit added just in case it may work wrong.
     */
    @Test
    public void changeProfileTest(){
        processor.processMessage(id, "да");
        processor.processMessage(id, "/editProfile");
        processor.processMessage(id, "2");
        processor.processMessage(id, "18");
        processor.processMessage(id, "/changeProfile");
        processor.processMessage(id, "Стас");
        processor.processMessage(id, "сатС");
        processor.processMessage(id, "91");
        processor.processMessage(id, "Девушка");
        processor.processMessage(id, "грубниретакЕ");
        processor.processMessage(id, "дурк отсорп");
        processor.processMessage(id, "71");
        processor.processMessage(id, "82");
        processor.processMessage(id, "Парень");
        processor.processMessage(id, "грубниретакЕ");
        processor.processPhoto(id, "грубниретакЕ");
        processor.processMessage(id, "да");
        Assertions.assertEquals("""
                Имя: сатС
                Возраст: 91
                Пол: девушка
                Город: грубниретакЕ
                Информация о себе: дурк отсорп
                Диапазон возраста собеседника: 71 - 82
                Пол собеседника: парень
                Город собеседника: грубниретакЕ""", processor.processMessage(id, "/myProfile")[0]);
        Assertions.assertEquals("грубниретакЕ", processor.processMessage(id, "/myProfile")[12]);
    }

    /**
     * Test of matching procedure.
     */
    @Test
    public void matchingFailTest(){
        processor.processMessage(id, "да");
        fillProfile("1", processor);
        Assertions.assertEquals("Не нашлось никого, кто соответствует твоей уникальности ;(", processor.processMessage("0", "/match")[0]);
    }
    @Test
    public void matchingLikeLikeTest(){
        processor.processMessage(id, "да");
        fillProfile("1", processor);
        processor.processMessage("0", "/editProfile");
        processor.processMessage("0", "8");
        processor.processMessage("0", "парень");
        processor.processMessage("1", "/editProfile");
        processor.processMessage("1", "8");
        processor.processMessage("1", "парень");
        Assertions.assertEquals(processor.processMessage("1", "/myProfile")[0], processor.processMessage("0", "/match")[0]);
        Assertions.assertEquals("Введи да или нет.", processor.processMessage("0", "дварыера")[0]);
        Assertions.assertEquals("Я уведомил этого пользователя, что он тебе приглянулся :)\nЕсли он ответит взаимностью, то вы сможете перейти к общению!", processor.processMessage("0", "да")[0]);
        Assertions.assertEquals("Введи да или нет.", processor.processMessage("1", "дварыера")[0]);
        String[] reply = processor.processMessage("1", "да");
        Assertions.assertEquals("Ура! Теперь вы можете перейти к общению.", reply[0]);
        Assertions.assertEquals("Вот ссылка на профиль собеседника - @stas", reply[1]);
    }
    @Test
    public void matchingLikeDislikeTest(){
        processor.processMessage(id, "да");
        fillProfile("1", processor);
        processor.processMessage("0", "/editProfile");
        processor.processMessage("0", "8");
        processor.processMessage("0", "парень");
        processor.processMessage("1", "/editProfile");
        processor.processMessage("1", "8");
        processor.processMessage("1", "парень");
        Assertions.assertEquals(processor.processMessage("1", "/myProfile")[0], processor.processMessage("0", "/match")[0]);
        Assertions.assertEquals("Я уведомил этого пользователя, что он тебе приглянулся :)\nЕсли он ответит взаимностью, то вы сможете перейти к общению!", processor.processMessage("0", "да")[0]);
        String[] reply = processor.processMessage("1", "нет");
        Assertions.assertEquals("Хорошо, больше ты этого человека не увидишь. Если только не решишь удалить его из списка не понравившихся профилей.", reply[0]);
    }
    @Test
    public void matchingDislikeTest(){
        processor.processMessage(id, "да");
        fillProfile("1", processor);
        processor.processMessage("0", "/editProfile");
        processor.processMessage("0", "8");
        processor.processMessage("0", "парень");
        processor.processMessage("1", "/editProfile");
        processor.processMessage("1", "8");
        processor.processMessage("1", "парень");
        Assertions.assertEquals(processor.processMessage("1", "/myProfile")[0], processor.processMessage("0", "/match")[0]);
        Assertions.assertEquals("Очень жаль, в следующий раз постараюсь лучше :(", processor.processMessage("0", "нет")[0]);
    }
    /**
     * Test of /myMatches command
     */
    @Test
    public void myMatchesTest(){
        processor.processMessage(id, "да");
        fillProfile("1", processor);
        processor.processMessage("0", "/editProfile");
        processor.processMessage("0", "8");
        processor.processMessage("0", "парень");
        processor.processMessage("1", "/editProfile");
        processor.processMessage("1", "8");
        processor.processMessage("1", "парень");
        processor.processMessage("0", "/match");
        processor.processMessage("0", "/myMatches");
        String[] reply = processor.processMessage("0", "1");
        Assertions.assertEquals(processor.processMessage("1", "/myProfile")[0], reply[2]);
    }
    @Test
    public void deleteTest(){
        processor.processMessage(id, "да");
        Assertions.assertEquals("Ты уверен, что хочешь этого? Все твои данные удалятся, в том числе и список понравившихся тебе людей!", processor.processMessage(id, "/deleteProfile")[0]);
        Assertions.assertEquals("Введено неверное значение, процедура удаления прекращена.", processor.processMessage(id, "stas1")[0]);
        processor.processMessage(id, "/deleteProfile");
        Assertions.assertEquals("Профиль успешно удален.", processor.processMessage(id, "stas")[0]);
        Assertions.assertEquals("требуется имя пользователя", processor.processMessage(id, "/start")[0]);
    }
}
