package dao;

import model.Training;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class TrainingDAOImpl implements TrainingDAO {
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(Training training) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.persist(training);
        transaction.commit();
        session.close();
    }

    @Override
    public Training update(Training training) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        Training oldTraining = (Training) session.get(Training.class, training.getId());
        oldTraining.setArrival(training.getArrival());
        oldTraining.setDeparture(training.getDeparture());
        session.update(oldTraining);
        transaction.commit();
        session.close();
        return oldTraining;
    }

    @Override
    public void delete(int id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.delete(session.get(Training.class, id));
        transaction.commit();
        session.close();
    }

    @Override
    public List<Training> trainings() {
        Session session = sessionFactory.openSession();
        List<Training> trainingsList = session.createQuery("from Training").list();
        session.close();
        return trainingsList;
    }
}
