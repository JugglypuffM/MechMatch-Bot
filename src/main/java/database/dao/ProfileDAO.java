package database.dao;

import database.hibernate.HibernateSessionFactory;
import database.entities.Profile;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ProfileDAO implements DAO<Profile, Integer>{
    private final HibernateSessionFactory sessionFactory;
    public ProfileDAO(HibernateSessionFactory hsf){
        this.sessionFactory = hsf;
    }

    @Override
    public void create(Profile profile) {
        Session session = sessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.persist(profile);
        tx1.commit();
        session.close();
    }
    @Override
    public Profile read(Integer id) {
        return sessionFactory.getSessionFactory().openSession().get(Profile.class, id);
    }
    @Override
    public void update(Profile profile) {
        Session session = sessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.merge(profile);
        tx1.commit();
        session.close();
    }
    @Override
    public void delete(Profile profile) {
        Session session = sessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.remove(profile);
        tx1.commit();
        session.close();
    }
    /**
     * Get list of users, who already filled profiles.
     * @return list of users
     */
    public List<Profile> getProfileFilledAccounts(){
        return sessionFactory.getSessionFactory().openSession().createQuery("From Profile WHERE profileFilled").list();
    }
}
