package dao;

import model.Person;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class PersonDAOImpl implements PersonDAO {
    private final Logger logger = Logger.getLogger(PersonDAO.class);
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public int create(Person person) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.persist(person);
        transaction.commit();
        session.close();
        return person.getId();
    }

    @Override
    public Person update(Person updatedPerson) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        Person person = (Person) session.get(Person.class, updatedPerson.getId());
        person.setAgm(updatedPerson.getAgm());
        person.setBirthday(updatedPerson.getBirthday());
        person.setName(updatedPerson.getName());
        person.setSurname(updatedPerson.getSurname());
        person.getPersonData().setAddress(updatedPerson.getPersonData().getAddress());
        person.getPersonData().setPhone(updatedPerson.getPersonData().getPhone());
        person.getPersonData().setMobile(updatedPerson.getPersonData().getMobile());
        person.getPersonData().setEmail(updatedPerson.getPersonData().getEmail());
        session.update(person);
        transaction.commit();
        session.close();
        return person;
    }

    @Override
    public void delete(int id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.delete(session.get(Person.class, id));
        transaction.commit();
        session.close();
    }

    @Override
    public Person getPerson(int id) {
        Session session = sessionFactory.openSession();
        Person person = (Person) session.get(Person.class, id);
        session.close();
        return person;
    }

    @Override
    public List<Person> personnel() {
        Session session = sessionFactory.openSession();
        List<Person> persons = session.createQuery("from Person").list();
        session.close();
        return persons;
    }
}
