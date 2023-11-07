package mainBot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MessageProcessorTests {
    public void fillProfile(String id, MessageProcessor processor){
        processor.processMessage(id, "/start", null);
        processor.processMessage(id, "Стас", null);
        processor.processMessage(id, "Стас", null);
        processor.processMessage(id, "19", null);
        processor.processMessage(id, "Парень", null);
        processor.processMessage(id, "Екатеринбург", null);
        processor.processMessage(id, "просто круд", null);
        processor.processMessage(id, "17", null);
        processor.processMessage(id, "23", null);
        processor.processMessage(id, "Девушка", null);
        processor.processMessage(id, "Екатеринбург", null);
        processor.processMessage(id, "да", null);
        processor.processMessage(id, null, "kxcxejvzxcilkcnkfidskjnugkvi");
    }
    @Test
    public void profileFillTest(){
        MessageProcessor processor = new MessageProcessor();
        String id = "0";
        processor.processMessage(id, "/start", null);
        Assertions.assertEquals("profile_fill", processor.storage.getUser(id).getGlobalState());
        Assertions.assertEquals("pf_START", processor.storage.getUser(id).getLocalState());
        processor.processMessage(id, "ASKJDBNFN,MD N;LZKSN C;SK,ADDX,FMJE'X,D", null);
        Assertions.assertEquals("pf_NAME", processor.storage.getUser(id).getLocalState());
        processor.processMessage(id, "Стас", null);
        Assertions.assertEquals("pf_AGE", processor.storage.getUser(id).getLocalState());
        Assertions.assertEquals("Стас", processor.storage.getUser(id).getName());
        processor.processMessage(id, "19", null);
        Assertions.assertEquals("pf_SEX", processor.storage.getUser(id).getLocalState());
        Assertions.assertEquals(19, processor.storage.getUser(id).getAge());
        processor.processMessage(id, "Парень", null);
        Assertions.assertEquals("pf_CITY", processor.storage.getUser(id).getLocalState());
        Assertions.assertEquals("парень", processor.storage.getUser(id).getSex());
        processor.processMessage(id, "Екатеринбург", null);
        Assertions.assertEquals("pf_ABOUT", processor.storage.getUser(id).getLocalState());
        Assertions.assertEquals("Екатеринбург", processor.storage.getUser(id).getCity());
        processor.processMessage(id, "просто круд", null);
        Assertions.assertEquals("pf_EAGEMIN", processor.storage.getUser(id).getLocalState());
        Assertions.assertEquals("просто круд", processor.storage.getUser(id).getInformation());
        processor.processMessage(id, "17", null);
        Assertions.assertEquals("pf_EAGEMAX", processor.storage.getUser(id).getLocalState());
        Assertions.assertEquals(17, processor.storage.getUser(id).getMinExpectedAge());
        processor.processMessage(id, "23", null);
        Assertions.assertEquals("pf_ESEX", processor.storage.getUser(id).getLocalState());
        Assertions.assertEquals(23, processor.storage.getUser(id).getMaxExpectedAge());
        processor.processMessage(id, "Девушка", null);
        Assertions.assertEquals("pf_ECITY", processor.storage.getUser(id).getLocalState());
        Assertions.assertEquals("девушка", processor.storage.getUser(id).getExpectedSex());
        processor.processMessage(id, "Екатеринбург", null);
        Assertions.assertEquals("pf_FINISH", processor.storage.getUser(id).getLocalState());
        Assertions.assertEquals("Екатеринбург", processor.storage.getUser(id).getExpectedCity());
        processor.processMessage(id, "adfasdfasd", null);
        Assertions.assertEquals("profile_fill", processor.storage.getUser(id).getGlobalState());
        Assertions.assertEquals("pf_FINISH", processor.storage.getUser(id).getLocalState());
        processor.processMessage(id, "да", null);
        Assertions.assertEquals("default", processor.storage.getUser(id).getGlobalState());
    }
    @Test
    public void profileEditTest(){
        MessageProcessor processor = new MessageProcessor();
        String id = "0";
        fillProfile(id, processor);
        processor.processMessage(id, "/editProfile", null);
        Assertions.assertEquals("profile_edit", processor.storage.getUser(id).getGlobalState());
        Assertions.assertEquals("pe_START", processor.storage.getUser(id).getLocalState());
        processor.processMessage(id, "svfand", null);
        Assertions.assertEquals("pe_START", processor.storage.getUser(id).getLocalState());
        processor.processMessage(id, "2", null);
        Assertions.assertEquals("pe_AGE", processor.storage.getUser(id).getLocalState());
        processor.processMessage(id, "18", null);
        Assertions.assertEquals("default", processor.storage.getUser(id).getGlobalState());
        Assertions.assertEquals(18, processor.storage.getUser(id).getAge());
    }

    @Test
    public void MatchingTest(){
        MessageProcessor processor = new MessageProcessor();
//        for (int i = 0; i < 16; i++){
//            fillProfile("" + i, processor);
//        }
        fillProfile("0", processor);
        for (int i = 1; i < processor.storage.getOtherProfilesList("0").size()/10 + 2; i++){
            processor.processMessage("0", "/match", null);
            Assertions.assertEquals("matching", processor.storage.getUser("0").getGlobalState());
            processor.processMessage("0", "" + i, null);
            Assertions.assertEquals("default", processor.storage.getUser("0").getGlobalState());
        }

    }
}
