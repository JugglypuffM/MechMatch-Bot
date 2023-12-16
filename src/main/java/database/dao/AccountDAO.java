package database.dao;

import bots.platforms.Platform;
import database.hibernate.HibernateSessionFactory;
import database.entities.Account;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class AccountDAO implements DAO<Account, Integer>{
    private final HibernateSessionFactory sessionFactory;
    public AccountDAO(HibernateSessionFactory hsf){
        this.sessionFactory = hsf;
    }

    @Override
    public void create(Account account) {
        Session session = sessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.persist(account);
        tx1.commit();
        session.close();
    }

    @Override
    public Account read(Integer id) {
        return sessionFactory.getSessionFactory().openSession().get(Account.class, id);
    }

    @Override
    public void update(Account account) {
        Session session = sessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.merge(account);
        tx1.commit();
        session.close();
    }

    @Override
    public void delete(Account account) {
        Session session = sessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.remove(account);
        tx1.commit();
        session.close();
    }

    public Account getAccountWithPlatformId(String id, Platform platform) {
        Session session = sessionFactory.getSessionFactory().openSession();
        Query<Account> query = null;
        switch (platform){
            case TELEGRAM -> query = session.createQuery("From Account where tgid = :paramid");
            case DISCORD -> query = session.createQuery("From Account where dsid = :paramid");
        }
        query.setParameter("paramid", id);
        try {
            return query.list().get(0);
        }catch (Exception e){
            return null;
        }
    }

    public Account getAccountWithLogin(String login) {
        Session session = sessionFactory.getSessionFactory().openSession();
        Query<Account> query = session.createQuery("From Account where login = :paramlogin");
        query.setParameter("paramlogin", login);
        try {
            return query.list().get(0);
        }catch (Exception e){
            return null;
        }
    }
}
