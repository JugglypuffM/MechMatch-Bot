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
            Configuration configuration = new Configuration().configure();
            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(Connection.class);
            configuration.setProperty("hibernate.connection.username", "postgres");
            configuration.setProperty("hibernate.connection.password", dotenv.get("POSTGRES_PASSWORD"));
            try {
                System.out.println("Trying to connect to container db:\n");
                configuration.setProperty("hibernate.connection.url", "jdbc:postgresql://db:5432/" + Dotenv.load().get("POSTGRES_DB"));
                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
                sessionFactory = configuration.buildSessionFactory(builder.build());
            } catch (Exception ContainerConnectException) {
                System.out.println("Got exception:\n" + ContainerConnectException);
                System.out.println("\n\nConnection to container db failed, trying to connect to default db:\n");
                try {
                    configuration.setProperty("hibernate.connection.url",
                            "jdbc:postgresql://" +
                            dotenv.get("PG_DEFAULT_HOST") + ":" +
                            dotenv.get("PG_DEFAULT_PORT") + "/" +
                            Dotenv.load().get("POSTGRES_DB"));
                    StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
                    sessionFactory = configuration.buildSessionFactory(builder.build());
                } catch (Exception LocalConnectException) {
                    System.out.println("Got exception:\n" + LocalConnectException);
                    System.out.println("\n\nCheck default host and port settings in .env and check if your db is accepting connections");
                    return sessionFactory;
                }
                System.out.println("\n\nConnection to default database successful");
                return sessionFactory;
            }
            System.out.println("\n\nConnection to database within container successful");
        }
        return sessionFactory;
    }
}