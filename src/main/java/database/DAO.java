package database;


import database.models.Connection;
import database.models.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Data Access Object for {@link User} and {@link Connection}.
 */
public class DAO {
    private final HibernateSessionFactory sessionFactory = new HibernateSessionFactory();
    public void createUser(User user) {
        Session session = sessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.persist(user);
        tx1.commit();
        session.close();
    }
    public User getUser(String id) {
        return sessionFactory.getSessionFactory().openSession().get(User.class, id);
    }
    public void updateUser(User user) {
        Session session = sessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.merge(user);
        tx1.commit();
        session.close();
    }
    public void deleteUser(User user) {
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

    /**
     * Connection creation method.
     * Checks if owner of this connection has less, than 100 likes and dislikes, else deletes the earliest one
     * @param connection
     */
    public void createConnection(Connection connection) {
        List<Connection> likes = getLikesOf(connection.getUserID());
        if (likes.size() > 100){
            deleteConnection(likes.get(0));
        }
        List<Connection> dislikes = getDislikesOf(connection.getUserID());
        if (dislikes.size() > 100){
            deleteConnection(dislikes.get(0));
        }
        Session session = sessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.persist(connection);
        tx1.commit();
        session.close();
    }
    public Connection getConnection(int id) {
        return sessionFactory.getSessionFactory().openSession().get(Connection.class, id);
    }
    public void updateConnection(Connection connection) {
        Session session = sessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.merge(connection);
        tx1.commit();
        session.close();
    }
    public void deleteConnection(Connection connection) {
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
    public List<Connection> getConnectionsWith(String id) {
        Session session = sessionFactory.getSessionFactory().openSession();
        Query<Connection> query = session.createQuery("From Connection where userID = :paramid");
        query.setParameter("paramid", id);
        return query.list();
    }
    /**
     * Get list with connections of given user with other users, which were not set to like or dislike
     * @return connections list
     */
    public List<Connection> getPendingOf(String id){
        Session session = sessionFactory.getSessionFactory().openSession();
        Query<Connection> query = session.createQuery("From Connection where (userID = :paramid and isLiked is null ) order by id asc" );
        query.setParameter("paramid", id);
        return query.list();
    }
    public List<Connection> getLikesOf(String id){
        Session session = sessionFactory.getSessionFactory().openSession();
        Query<Connection> query = session.createQuery("From Connection where (userID = :paramid and isLiked is true ) order by id asc");
        query.setParameter("paramid", id);
        return query.list();
    }
    public List<Connection> getDislikesOf(String id){
        Session session = sessionFactory.getSessionFactory().openSession();
        Query<Connection> query = session.createQuery("From Connection where (userID = :paramid and isLiked is false ) order by id asc");
        query.setParameter("paramid", id);
        return query.list();
    }
}
