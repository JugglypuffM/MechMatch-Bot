package database.dao;

import database.hibernate.HibernateSessionFactory;
import database.entities.Connection;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ConnectionDAO implements DAO<Connection, Integer>{
    private final HibernateSessionFactory sessionFactory;
    public ConnectionDAO(HibernateSessionFactory hsf){
        this.sessionFactory = hsf;
    }
    /**
     * Connection creation method.
     * Checks if owner of this connection has less, than 100 likes and dislikes, else deletes the earliest one
     * @param connection new connection
     */
    @Override
    public void create(Connection connection) {
        List<Connection> likes = getLikesOf(connection.getUserID());
        if (likes.size() > 100){
            delete(likes.get(0));
        }
        List<Connection> dislikes = getDislikesOf(connection.getUserID());
        if (dislikes.size() > 100){
            delete(dislikes.get(0));
        }
        Session session = sessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.persist(connection);
        tx1.commit();
        session.close();
    }
    @Override
    public Connection read(Integer id) {
        return sessionFactory.getSessionFactory().openSession().get(Connection.class, id);
    }
    @Override
    public void update(Connection connection) {
        Session session = sessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.merge(connection);
        tx1.commit();
        session.close();
    }
    @Override
    public void delete(Connection connection) {
        Session session = sessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.remove(connection);
        tx1.commit();
        session.close();
    }
    /**
     * Get list with connections of given user with other users
     * @return connections list
     */
    public List<Connection> getConnectionsWith(Integer id) {
        Session session = sessionFactory.getSessionFactory().openSession();
        Query<Connection> query = session.createQuery("From Connection where userID = :paramid");
        query.setParameter("paramid", id);
        return query.list();
    }
    public List<Connection> getConnectionsOf(Integer id) {
        Session session = sessionFactory.getSessionFactory().openSession();
        Query<Connection> query = session.createQuery("From Connection where userID = :paramid or friendID = :paramid");
        query.setParameter("paramid", id);
        return query.list();
    }
    /**
     * Get list with connections of given user with other users, which were not set to like or dislike
     * @return connections list
     */
    public List<Connection> getPendingOf(Integer id){
        Session session = sessionFactory.getSessionFactory().openSession();
        Query<Connection> query = session.createQuery("From Connection where (userID = :paramid and isLiked is null ) order by id asc" );
        query.setParameter("paramid", id);
        return query.list();
    }
    public List<Connection> getLikesOf(Integer id){
        Session session = sessionFactory.getSessionFactory().openSession();
        Query<Connection> query = session.createQuery("From Connection where (userID = :paramid and isLiked is true ) order by id asc");
        query.setParameter("paramid", id);
        return query.list();
    }
    public List<Connection> getDislikesOf(Integer id){
        Session session = sessionFactory.getSessionFactory().openSession();
        Query<Connection> query = session.createQuery("From Connection where (userID = :paramid and isLiked is false ) order by id asc");
        query.setParameter("paramid", id);
        return query.list();
    }
}
