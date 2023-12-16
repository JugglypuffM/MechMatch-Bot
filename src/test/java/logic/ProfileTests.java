package logic;

import database.entities.Profile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProfileTests {
    /**
     * Test of sex verification.
     * Tests if there are only two acceptable options.
     */
    @Test
    public void sexVerificationTest(){
        Profile user = new Profile(0);
        String lastSex = user.getSex();
        Assertions.assertFalse(user.setSex("asdfasdf"));
        Assertions.assertEquals(lastSex, user.getSex());
        Assertions.assertTrue(user.setSex("Парень"));
        Assertions.assertEquals("парень", user.getSex());
        Assertions.assertTrue(user.setSex("Девушка"));
        Assertions.assertEquals("девушка", user.getSex());
        lastSex = user.getSex();
        Assertions.assertFalse(user.setSex("asdfasdf"));
        Assertions.assertEquals(lastSex, user.getSex());
    }

    /**
     * Test of age verification.
     * Tests if only a range from 15 to 119 is accepted.
     */
    @Test
    public void ageVerificationTest(){
        Profile user = new Profile(0);
        int lastAge = user.getAge();
        Assertions.assertFalse(user.setAge("13"));
        Assertions.assertFalse(user.setAge("-23"));
        Assertions.assertFalse(user.setAge("121"));
        Assertions.assertEquals(lastAge, user.getAge());
        Assertions.assertTrue(user.setAge("15"));
        Assertions.assertEquals(15, user.getAge());
        Assertions.assertTrue(user.setAge("45"));
        Assertions.assertEquals(45, user.getAge());
        Assertions.assertTrue(user.setAge("119"));
        Assertions.assertEquals(119, user.getAge());
        lastAge = user.getAge();
        Assertions.assertFalse(user.setAge("140"));
        Assertions.assertEquals(lastAge, user.getAge());
    }

    @Test
    public void sexValidation(){
        Profile user = new Profile(0);
        Assertions.assertTrue(user.validateSex("Татьяна", "девушка"));
        Assertions.assertFalse(user.validateSex("Татьяна", "парень"));
        Assertions.assertTrue(user.validateSex("Станислав", "парень"));
        Assertions.assertFalse(user.validateSex("Станислав", "девушка"));
    }
}
