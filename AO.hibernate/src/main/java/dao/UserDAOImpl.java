package dao;

import model.User;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Iterator;

public class UserDAOImpl implements UserDAO {
    private final Logger logger = Logger.getLogger(PersonDAO.class);
    private SessionFactory sessionFactory;

    @Override
    public boolean validate(String username, String password) throws HibernateException {
        Session session = sessionFactory.openSession();
        Iterator<User> iterator = session.createQuery("from User").iterate();

        while (iterator.hasNext()) {
            User user = iterator.next();
            if (user.getUsername().equalsIgnoreCase(username)
                    && user.getPassword().equalsIgnoreCase(password))
                return true;
        }
        return false;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
