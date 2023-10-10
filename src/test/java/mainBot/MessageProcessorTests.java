package mainBot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class MessageProcessorTests {
    @Test
    public void initUserTest(){
        User sender = MessageProcessor.initUser("1234");
        Map<String, User> userList = MessageProcessor.getUserList();
        Assertions.assertTrue(userList.containsKey("1234"));
        Assertions.assertEquals(userList.get("1234").getId(), "1234");
        Assertions.assertEquals(userList.get("1234").getLastReply(), "answer");
        Assertions.assertNull(userList.get("1234").getCurrentQuestion());
    }

    @Test
    public void processMessageOneUserTest(){
        String id = "0";
        String[] reply = MessageProcessor.processMessage(id, "/start");
        String previousQuestion = reply[1];
        Assertions.assertEquals(reply[0], MessageProcessor.giveHelp());
        System.out.println(previousQuestion);
        reply = MessageProcessor.processMessage(id, "sdfjkhhshdf");
        Assertions.assertEquals(reply[0], "Да ты глуп!\nМожет сходишь в первый класс, что-ли...");
        Assertions.assertEquals(reply[1], previousQuestion);
        reply = MessageProcessor.processMessage(id, Integer.parseInt(String.valueOf(previousQuestion.charAt(0))) + Integer.parseInt(String.valueOf(previousQuestion.charAt(2)))+"");
        Assertions.assertEquals(reply[0], "Да ты крут!\nМожешь идти во второй класс, наверное...");
        Assertions.assertNotEquals(reply[1], previousQuestion);
    }
    @Test
    public void processMessageSemiUserTest(){
        String id1 = "0";
        String id2 = "1";
        String[] reply1 = MessageProcessor.processMessage(id1, "/start");
        String[] reply2 = MessageProcessor.processMessage(id2, "/start");
        String previousQuestion1 = reply1[1];
        String previousQuestion2 = reply2[1];
        Assertions.assertNotEquals(reply1[1], reply2[1]);
        reply1 = MessageProcessor.processMessage(id1, Integer.parseInt(String.valueOf(previousQuestion1.charAt(0))) + Integer.parseInt(String.valueOf(previousQuestion1.charAt(2)))+"");
        reply2 = MessageProcessor.processMessage(id2, "4-6578-54");
        Assertions.assertNotEquals(previousQuestion1, reply1[1]);
        Assertions.assertEquals(previousQuestion2, reply2[1]);
    }
}
