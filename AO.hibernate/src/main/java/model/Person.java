package model;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;
/*
Entity Bean with JPA annotations
Hibernate provides JPA implementation
 */

@Entity
@Table(name = "personnel")
public class Person {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "surname")
    private String surname;
    @Column(name = "birthday")
    private Date birthday;
    @Column(name = "agm")
    private String agm;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "person_data")
    private PersonData personData;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "person")
    private Set<Transfer> transfers;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "person")
    private Set<DayOff> dayOffs;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "person")
    private Set<Training> trainings;

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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getAgm() {
        return agm;
    }

    public void setAgm(String agm) {
        this.agm = agm;
    }

    public PersonData getPersonData() {
        return personData;
    }

    public void setPersonData(PersonData personData) {
        this.personData = personData;
    }

    public Set<Transfer> getTransfers() {
        return transfers;
    }

    public void setTransfers(Set transfers) {
        this.transfers = transfers;
    }

    public Set<DayOff> getDayOffs() {
        return dayOffs;
    }

    public void setDayOffs(Set dayOffs) {
        this.dayOffs = dayOffs;
    }

    public Set<Training> getTrainings() {
        return trainings;
    }

    public void setTrainings(Set trainings) {
        this.trainings = trainings;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", birthday=" + birthday +
                ", agm='" + agm + '\'' +
                ", personData=" + personData +
                ", transfers=" + transfers +
                ", dayOffs=" + dayOffs +
                ", trainings=" + trainings +
                '}';
    }
}
