package database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import mainBot.User;

@Entity
@Table(name = "public.users")
public class UserObject {
    @Id
    @Column(name = "userID")
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "age")
    private int age;
    @Column(name = "sex")
    private String sex;
    @Column(name = "city")
    private String city;
    @Column(name = "information")
    private String information;
    @Column(name = "minExpectedAge")
    private int minExpectedAge;
    @Column(name = "maxExpectedAge")
    private int maxExpectedAge;
    @Column(name = "expectedSex")
    private String expectedSex;
    @Column(name = "expectedCity")
    private String expectedCity;
    @Column(name = "photoID")
    private String photoID;

    public UserObject() {
    }
    public UserObject(User user){
        this.id = user.getId();
        this.name = user.getName();
        this.age = user.getAge();
        this.sex = user.getSex();
        this.city = user.getCity();
        this.information = user.getInformation();
        this.minExpectedAge = user.getMinExpectedAge();
        this.maxExpectedAge = user.getMaxExpectedAge();
        this.expectedSex = user.getExpectedSex();
        this.expectedCity = user.getExpectedCity();
        this.photoID = user.getPhotoID();
    }
}
