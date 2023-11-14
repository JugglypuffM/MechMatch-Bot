package database;

import database.models.Connection;
import database.models.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateSessionFactory {
    private SessionFactory sessionFactory;
    public HibernateSessionFactory() {}
    public SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration().configure();
                configuration.addAnnotatedClass(User.class);
                configuration.addAnnotatedClass(Connection.class);
                configuration.setProperty("hibernate.connection.username", System.getenv("dbName"));
                configuration.setProperty("hibernate.connection.password", System.getenv("dbPassword"));
                configuration.setProperty("hibernate.connection.url", System.getenv("dbUrl"));
                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
                sessionFactory = configuration.buildSessionFactory(builder.build());
            } catch (Exception e) {
                System.out.println("Исключение!\n" + e);
            }
        }
        return sessionFactory;
    }
}