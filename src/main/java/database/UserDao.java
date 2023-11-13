package database;


import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;


public class UserDao {
    private final HSFU sessionFactory = new HSFU();
    public UserObject findById(int id) {
        return sessionFactory.getSessionFactory().openSession().get(UserObject.class, id);
    }

    public void save(UserObject user) {
        Session session = sessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.persist(user);
        tx1.commit();
        session.close();
    }

    public void update(UserObject user) {
        Session session = sessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.merge(user);
        tx1.commit();
        session.close();
    }

    public void delete(UserObject user) {
        Session session = sessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.remove(user);
        tx1.commit();
        session.close();
    }

    public List<UserObject> findAll() {
        List<UserObject> users = (List<UserObject>)  sessionFactory.getSessionFactory().openSession().createQuery("From UserObject").list();
        return users;
    }
}
