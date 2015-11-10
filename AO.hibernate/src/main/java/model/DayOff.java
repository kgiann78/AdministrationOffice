package model;

import javax.persistence.*;
import java.util.Date;

/*
Entity Bean with JPA annotations
Hibernate provides JPA implementation
 */

@Entity
@Table(name = "dayoffs")
public class DayOff {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "type")
    private String type;
    @Column(name = "departure")
    private Date departure;
    @Column(name = "arrival")
    private Date arrival;
    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDeparture() {
        return departure;
    }

    public void setDeparture(Date departure) {
        this.departure = departure;
    }

    public Date getArrival() {
        return arrival;
    }

    public void setArrival(Date arrival) {
        this.arrival = arrival;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return "DayOff{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", departure=" + departure +
                ", arrival=" + arrival +
                '}';
    }
}
