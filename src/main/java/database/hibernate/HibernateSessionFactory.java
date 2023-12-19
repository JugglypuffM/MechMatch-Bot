package database.hibernate;

import database.entities.Account;
import database.entities.Connection;
import database.entities.Profile;
import database.entities.Client;
import io.github.cdimascio.dotenv.Dotenv;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;

/**
 * Session factory class.
 */
public class HibernateSessionFactory {
    private final Dotenv dotenv = Dotenv.load();
    private SessionFactory sessionFactory;
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(HibernateSessionFactory.class);
    public HibernateSessionFactory() {}

    /**
     * Initializes new session factory if it is not initialized
     * @return {@link HibernateSessionFactory#sessionFactory}
     */
    public SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration().configure();
            configuration.addAnnotatedClass(Client.class);
            configuration.addAnnotatedClass(Profile.class);
            configuration.addAnnotatedClass(Connection.class);
            configuration.addAnnotatedClass(Account.class);
            configuration.setProperty("hibernate.connection.username", dotenv.get("POSTGRES_USER"));
            configuration.setProperty("hibernate.connection.password", dotenv.get("POSTGRES_PASSWORD"));
            try {
                logger.info("Trying to connect to container db:");
                configuration.setProperty("hibernate.connection.url", "jdbc:postgresql://db:5432/" + Dotenv.load().get("POSTGRES_DB"));
                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
                sessionFactory = configuration.buildSessionFactory(builder.build());
            } catch (Exception ContainerConnectException) {
                logger.error("Got exception trying to connect to container db:", ContainerConnectException);
                logger.info("Connection to container db failed, trying to connect to default db:");
                try {
                    configuration.setProperty("hibernate.connection.url",
                            "jdbc:postgresql://" +
                            dotenv.get("PG_DEFAULT_HOST") + ":" +
                            dotenv.get("PG_DEFAULT_PORT") + "/" +
                            Dotenv.load().get("POSTGRES_DB"));
                    StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
                    sessionFactory = configuration.buildSessionFactory(builder.build());
                } catch (Exception LocalConnectException) {
                    logger.error("Got exception trying to connect to default db:", LocalConnectException);
                    logger.warn("Check default host and port settings in .env and check if your db is accepting connections");
                    return sessionFactory;
                }
                logger.info("Connection to default database successful");
                return sessionFactory;
            }
            logger.info("\n\nConnection to database within container successful");
        }
        return sessionFactory;
    }
}