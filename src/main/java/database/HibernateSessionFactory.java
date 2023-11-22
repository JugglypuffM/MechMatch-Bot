package database;

import database.models.Connection;
import database.models.User;
import io.github.cdimascio.dotenv.Dotenv;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

/**
 * Session factory class.
 */
public class HibernateSessionFactory {
    private final Dotenv dotenv = Dotenv.load();
    private SessionFactory sessionFactory;
    public HibernateSessionFactory() {}

    /**
     * Initializes new session factory if it is not initialized
     * @return {@link HibernateSessionFactory#sessionFactory}
     */
    public SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration().configure();
                configuration.addAnnotatedClass(User.class);
                configuration.addAnnotatedClass(Connection.class);
                configuration.setProperty("hibernate.connection.username", "postgres");
                configuration.setProperty("hibernate.connection.password", dotenv.get("POSTGRES_PASSWORD"));
                configuration.setProperty("hibernate.connection.url", "jdbc:postgresql://db:5432/" + Dotenv.load().get("POSTGRES_DB"));
                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
                sessionFactory = configuration.buildSessionFactory(builder.build());
            } catch (Exception e) {
                System.out.println("Исключение!\n" + e);
            }
        }
        return sessionFactory;
    }
}