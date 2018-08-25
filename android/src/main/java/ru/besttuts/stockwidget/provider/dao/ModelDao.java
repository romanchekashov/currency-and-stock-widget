package ru.besttuts.stockwidget.provider.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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
