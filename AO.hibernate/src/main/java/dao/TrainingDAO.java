package dao;

import model.Training;

import java.util.List;

public interface TrainingDAO {

    public void create(Training training);

    public Training update(Training training);

    public void delete(int id);

    public List<Training> trainings();
}
