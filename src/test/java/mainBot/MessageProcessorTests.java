package mainBot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MessageProcessorTests {
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
     * Test of profile filling procedure, tests if states switch correctly and data stores appropriately
     */
    @Test
    public void profileFillTest(){
        MessageProcessor processor = new MessageProcessor();
        String id = "0";
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
     * Test of profile editing procedure, tests state switching and appropriate data storage
     */
    @Test
    public void profileEditTest(){
        MessageProcessor processor = new MessageProcessor();
        String id = "0";
        fillProfile(id, processor);
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
     * Test of matching procedure, tests if all pages can be accessed
     */
    @Test
    public void MatchingTest(){
        MessageProcessor processor = new MessageProcessor();
        for (int i = 0; i < 16; i++){
            fillProfile("" + i, processor);
        }

    }
}
