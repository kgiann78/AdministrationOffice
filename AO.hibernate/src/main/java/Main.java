import dao.DayOffDAO;
import dao.PersonDAO;
import dao.TransferDAO;
import model.DayOff;
import model.Person;
import model.Transfer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("hibernate.xml");

        PersonDAO personDAO = context.getBean(PersonDAO.class);
        DayOffDAO dayOffDAO = context.getBean(DayOffDAO.class);
        TransferDAO transferDAO = context.getBean(TransferDAO.class);

        List<Person> list = personDAO.personnel();
        List<DayOff> dayOffList = dayOffDAO.dayOffs();
        List<Transfer> transferList = transferDAO.transfers();

        for(Person person : list){
            System.out.println("Person: " + person);
        }

        for(DayOff dayOff : dayOffList){
            System.out.println("DayOff: " + dayOff);
        }

        for(Transfer transfer : transferList){
            System.out.println("Transfer: " + transfer);
        }


        System.out.println("This is the first person in the database: " + personDAO.getPerson(1));


        //close resources
        context.close();
    }
}
