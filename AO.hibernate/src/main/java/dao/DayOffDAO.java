package dao;

import model.DayOff;

import java.util.List;

public interface DayOffDAO {

    public void create(DayOff dayOff);

    public DayOff update(DayOff dayOff);

    public void delete(int id);

    public List<DayOff> dayOffs();
}
