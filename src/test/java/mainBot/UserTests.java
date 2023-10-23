package mainBot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserTests {
    @Test
    public void sexVerificationTest(){
        User user = new User("0");
        boolean lastSex = user.getSex();
        Assertions.assertFalse(user.setSex("asdfasdf"));
        Assertions.assertEquals(lastSex, user.getSex());
        Assertions.assertTrue(user.setSex("Парень"));
        Assertions.assertFalse(user.getSex());
        Assertions.assertTrue(user.setSex("Девушка"));
        Assertions.assertTrue(user.getSex());
        lastSex = user.getSex();
        Assertions.assertFalse(user.setSex("asdfasdf"));
        Assertions.assertEquals(lastSex, user.getSex());
    }

    @Test
    public void ageVerificationTest(){
        User user = new User("0");
        int lastAge = user.getAge();
        Assertions.assertFalse(user.setAge(14));
        Assertions.assertFalse(user.setAge(-23));
        Assertions.assertFalse(user.setAge(120));
        Assertions.assertEquals(lastAge, user.getAge());
        Assertions.assertTrue(user.setAge(15));
        Assertions.assertEquals(15, user.getAge());
        Assertions.assertTrue(user.setAge(45));
        Assertions.assertEquals(45, user.getAge());
        Assertions.assertTrue(user.setAge(119));
        Assertions.assertEquals(119, user.getAge());
        lastAge = user.getAge();
        Assertions.assertFalse(user.setAge(140));
        Assertions.assertEquals(lastAge, user.getAge());
    }
}
