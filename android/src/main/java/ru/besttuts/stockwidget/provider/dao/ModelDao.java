package ru.besttuts.stockwidget.provider.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.besttuts.stockwidget.provider.model.Model;

@Dao
public interface ModelDao {

    @Query("SELECT * FROM models")
    List<Model> getAll();

    @Query("SELECT * FROM models WHERE id = :id")
    Model byId(String id);

    @Query("SELECT * FROM models WHERE id IN (:ids)")
    List<Model> allByIds(List<String> ids);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Model... models);

    @Update
    void updateAll(Model... models);

    @Delete
    void deleteAll(Model... models);
}
