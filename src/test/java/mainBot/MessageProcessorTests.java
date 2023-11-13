package mainBot;

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
        processor.processMessage(id, "да");
    }

    /**
     * Initialization of {@link MessageProcessor} and basic user with id 0
     */
    @BeforeEach
    public void initialize(){
        this.processor = new MessageProcessor();
        this.id = "0";
        processor.processMessage(id, "/start");
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
    }
    /**
     * Test of profile filling procedure.
     * Tests if states switch correctly and data stores appropriately
     */
    @Test
    public void profileFillTest(){
        processor.processMessage(id, "да");
        Assertions.assertEquals("""
                Имя: Стас
                Возраст: 19
                Пол: парень
                Город: Екатеринбург
                Информация о себе: просто круд
                Диапазон возраста собеседника: 17 - 23
                Пол собеседника: девушка
                Город собеседника: Екатеринбург""", processor.processMessage(id, "/myProfile")[0]);
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
                "9 - Город собеседника(Екатеринбург)", reply[1]);
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
        Assertions.assertEquals("Введи новое значение.", processor.processMessage(id, "2")[0]);
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
    }

    /**
     * Test of matching procedure.
     */
    @Test
    public void matchingTest(){
        processor.processMessage(id, "да");
        Assertions.assertEquals("Кроме тебя пока никого нет ;(", processor.processMessage(id, "/match")[0]);
        fillProfile("1", processor);
        Assertions.assertEquals("Какую страницу анкет вывести(Всего: 1)?", processor.processMessage(id, "/match")[0]);
        Assertions.assertEquals("Пожалуйста, введи ответ цифрами.", processor.processMessage(id, "/match")[0]);
        Assertions.assertEquals("Нет страницы с таким номером.", processor.processMessage(id, "2")[0]);
        Assertions.assertEquals("""
                Имя: Стас
                Возраст: 19
                Пол: парень
                Город: Екатеринбург
                Информация о себе: просто круд
                Диапазон возраста собеседника: 17 - 23
                Пол собеседника: девушка
                Город собеседника: Екатеринбург""", processor.processMessage(id, "1")[2]);
        processor.processMessage("1", "/editProfile");
        processor.processMessage("1", "1");
        processor.processMessage("1", "Тсас");
        processor.processMessage(id, "/match");
        Assertions.assertEquals("""
                Имя: Тсас
                Возраст: 19
                Пол: парень
                Город: Екатеринбург
                Информация о себе: просто круд
                Диапазон возраста собеседника: 17 - 23
                Пол собеседника: девушка
                Город собеседника: Екатеринбург""", processor.processMessage(id, "1")[2]);
    }
}
