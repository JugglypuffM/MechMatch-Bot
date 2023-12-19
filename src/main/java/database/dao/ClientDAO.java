package database.dao;


import database.hibernate.HibernateSessionFactory;
import database.entities.Connection;
import database.entities.Client;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Data Access Object for {@link Client} and {@link Connection}.
 */
public class ClientDAO implements DAO<Client, String>{
    private final HibernateSessionFactory sessionFactory;
    public ClientDAO(HibernateSessionFactory hsf){
        this.sessionFactory = hsf;
    }
    @Override
    public void create(Client client) {
        Session session = sessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.persist(client);
        tx1.commit();
        session.close();
    }
    @Override
    public Client read(String id) {
        return sessionFactory.getSessionFactory().openSession().get(Client.class, id);
    }
    @Override
    public void update(Client client) {
        Session session = sessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.merge(client);
        tx1.commit();
        session.close();
    }
    @Override
    public void delete(Client client) {
        Session session = sessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.remove(client);
        tx1.commit();
        session.close();
    }
}
