package dao;

import model.Transfer;

import java.util.List;

public interface TransferDAO {

    public Transfer getTransfer(int id);

    public void create(Transfer transfer);

    public Transfer update(Transfer transfer);

    public void delete(int id);

    public List<Transfer> transfers();
}
