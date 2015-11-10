package model;

import javax.persistence.*;
import java.util.Date;

/*
Entity Bean with JPA annotations
Hibernate provides JPA implementation
 */

@Entity
@Table(name = "transfers")
public class Transfer {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "service")
    private String service;
    @Column(name = "type")
    private String type;
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

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        return "Transfer{" +
                "id=" + id +
                ", service='" + service + '\'' +
                ", type='" + type + '\'' +
                ", arrival=" + arrival +
                ", departure=" + departure +
                '}';
    }
}
