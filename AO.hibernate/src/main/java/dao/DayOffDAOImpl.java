package dao;

import model.DayOff;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class DayOffDAOImpl implements DayOffDAO {
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(DayOff dayOff) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.persist(dayOff);
        transaction.commit();
        session.close();
    }

    @Override
    public DayOff update(DayOff dayOff) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        DayOff oldDayOff = (DayOff) session.get(DayOff.class, dayOff.getId());
        oldDayOff.setType(dayOff.getType());
        oldDayOff.setArrival(dayOff.getArrival());
        oldDayOff.setDeparture(dayOff.getDeparture());
        session.update(oldDayOff);
        transaction.commit();
        session.close();
        return oldDayOff;
    }

    @Override
    public void delete(int id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.delete(session.get(DayOff.class, id));
        transaction.commit();
        session.close();
    }


    @Override
    public List<DayOff> dayOffs() {
        Session session = sessionFactory.openSession();
        List<DayOff> dayOffsList = session.createQuery("from DayOff").list();
        session.close();
        return dayOffsList;
    }
}
