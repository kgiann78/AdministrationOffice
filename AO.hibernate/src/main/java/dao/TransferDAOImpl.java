package dao;

import model.Transfer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class TransferDAOImpl implements TransferDAO {
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Transfer getTransfer(int id) {
        Session session = sessionFactory.openSession();
        Transfer transfer = (Transfer) session.get(Transfer.class, id);
        session.close();
        return transfer;
    }

    @Override
    public void create(Transfer transfer) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.persist(transfer);
        transaction.commit();
        session.close();
    }

    @Override
    public Transfer update(Transfer transfer) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        Transfer oldTransfer = (Transfer) session.get(Transfer.class, transfer.getId());
        oldTransfer.setService(transfer.getService());
        oldTransfer.setType(transfer.getType());
        oldTransfer.setArrival(transfer.getArrival());
        oldTransfer.setDeparture(transfer.getDeparture());
        session.update(oldTransfer);
        transaction.commit();
        session.close();
        return oldTransfer;
    }

    @Override
    public void delete(int id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        Transfer transfer = (Transfer) session.get(Transfer.class, id);
        transfer.getPerson().getTransfers().remove(transfer);
        System.out.println("Before");
        System.out.println(transfer.getPerson().getTransfers());


//        session.delete(session.get(Transfer.class, id));
        transaction.commit();

        System.out.println("After");
        System.out.println(transfer.getPerson().getTransfers());
        session.close();
    }

    @Override
    public List<Transfer> transfers() {
        Session session = sessionFactory.openSession();
        List<Transfer> transfersList = session.createQuery("from Transfer").list();
        session.close();
        return transfersList;
    }
}
