package model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/*
Entity Bean with JPA annotations
Hibernate provides JPA implementation
 */

@Entity
@Table(name = "trainings")
public class Training implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "arrival")
    private Date arrival;
    @Column(name = "departure")
    private Date departure;
    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getArrival() {
        return arrival;
    }

    public void setArrival(Date arrival) {
        this.arrival = arrival;
    }

    public Date getDeparture() {
        return departure;
    }

    public void setDeparture(Date departure) {
        this.departure = departure;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return "Training{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", arrival=" + arrival +
                ", departure=" + departure +
                '}';
    }
}
