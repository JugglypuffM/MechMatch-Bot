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
        Assertions.assertEquals("profile_fill", processor.storage.getUser(id).getGlobalState());
        Assertions.assertEquals("pf_START", processor.storage.getUser(id).getLocalState());
        processor.processMessage(id, "ASKJDBNFN,MD N;LZKSN C;SK,ADDX,FMJE'X,D");
        Assertions.assertEquals("pf_NAME", processor.storage.getUser(id).getLocalState());
        processor.processMessage(id, "Стас");
        Assertions.assertEquals("pf_AGE", processor.storage.getUser(id).getLocalState());
        Assertions.assertEquals("Стас", processor.storage.getUser(id).getName());
        processor.processMessage(id, "19");
        Assertions.assertEquals("pf_SEX", processor.storage.getUser(id).getLocalState());
        Assertions.assertEquals(19, processor.storage.getUser(id).getAge());
        processor.processMessage(id, "Парень");
        Assertions.assertEquals("pf_CITY", processor.storage.getUser(id).getLocalState());
        Assertions.assertEquals("парень", processor.storage.getUser(id).getSex());
        processor.processMessage(id, "Екатеринбург");
        Assertions.assertEquals("pf_ABOUT", processor.storage.getUser(id).getLocalState());
        Assertions.assertEquals("Екатеринбург", processor.storage.getUser(id).getCity());
        processor.processMessage(id, "просто круд");
        Assertions.assertEquals("pf_EAGEMIN", processor.storage.getUser(id).getLocalState());
        Assertions.assertEquals("просто круд", processor.storage.getUser(id).getInformation());
        processor.processMessage(id, "17");
        Assertions.assertEquals("pf_EAGEMAX", processor.storage.getUser(id).getLocalState());
        Assertions.assertEquals(17, processor.storage.getUser(id).getMinExpectedAge());
        processor.processMessage(id, "23");
        Assertions.assertEquals("pf_ESEX", processor.storage.getUser(id).getLocalState());
        Assertions.assertEquals(23, processor.storage.getUser(id).getMaxExpectedAge());
        processor.processMessage(id, "Девушка");
        Assertions.assertEquals("pf_ECITY", processor.storage.getUser(id).getLocalState());
        Assertions.assertEquals("девушка", processor.storage.getUser(id).getExpectedSex());
        processor.processMessage(id, "Екатеринбург");
        Assertions.assertEquals("pf_FINISH", processor.storage.getUser(id).getLocalState());
        Assertions.assertEquals("Екатеринбург", processor.storage.getUser(id).getExpectedCity());
        processor.processMessage(id, "adfasdfasd");
        Assertions.assertEquals("profile_fill", processor.storage.getUser(id).getGlobalState());
        Assertions.assertEquals("pf_FINISH", processor.storage.getUser(id).getLocalState());
        processor.processMessage(id, "да");
        Assertions.assertEquals("default", processor.storage.getUser(id).getGlobalState());
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
        Assertions.assertEquals("profile_edit", processor.storage.getUser(id).getGlobalState());
        Assertions.assertEquals("pe_START", processor.storage.getUser(id).getLocalState());
        processor.processMessage(id, "svfand");
        Assertions.assertEquals("pe_START", processor.storage.getUser(id).getLocalState());
        processor.processMessage(id, "2");
        Assertions.assertEquals("pe_AGE", processor.storage.getUser(id).getLocalState());
        processor.processMessage(id, "18");
        Assertions.assertEquals("default", processor.storage.getUser(id).getGlobalState());
        Assertions.assertEquals(18, processor.storage.getUser(id).getAge());
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
        for (int i = 1; i < processor.storage.getOtherProfilesList("0").size()/10 + 2; i++){
            processor.processMessage("0", "/match");
            Assertions.assertEquals("matching", processor.storage.getUser("0").getGlobalState());
            processor.processMessage("0", "" + i);
            Assertions.assertEquals("default", processor.storage.getUser("0").getGlobalState());
        }

    }
}
