package database.dao;


import database.hibernate.HibernateSessionFactory;
import database.models.Connection;
import database.models.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.swing.text.html.parser.Entity;
import java.util.List;

/**
 * Data Access Object for {@link User} and {@link Connection}.
 */
public class UserDAO implements DAO<User, String>{
    private final HibernateSessionFactory sessionFactory;
    public UserDAO(HibernateSessionFactory hsf){
        this.sessionFactory = hsf;
    }
    public void create(User user) {
        Session session = sessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.persist(user);
        tx1.commit();
        session.close();
    }
    public User read(String id) {
        return sessionFactory.getSessionFactory().openSession().get(User.class, id);
    }
    public void update(User user) {
        Session session = sessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.merge(user);
        tx1.commit();
        session.close();
    }
    public void delete(User user) {
        Session session = sessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.remove(user);
        tx1.commit();
        session.close();
    }
    /**
     * Get list of users, who already filled profiles.
     * @return list of users
     */
    public List<User> getProfileFilledUsers(){
        return sessionFactory.getSessionFactory().openSession().createQuery("From User WHERE profileFilled").list();
    }
}
