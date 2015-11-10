package dao;

import model.Person;

import java.util.List;

public interface PersonDAO {
    public int create(Person person);

    public Person update(Person person);

    public void delete(int id);

    public Person getPerson(int id);

    public List<Person> personnel();
}
