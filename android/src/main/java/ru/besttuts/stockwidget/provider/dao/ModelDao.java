package ru.besttuts.stockwidget.provider.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Single;
import ru.besttuts.stockwidget.provider.model.Model;

@Dao
public interface ModelDao {

    @Query("SELECT * FROM models ORDER BY sort")
    Single<List<Model>> getAll();

    @Query("SELECT * FROM models WHERE id = :id")
    Model byId(String id);

    @Query("SELECT * FROM models WHERE id IN (:ids) ORDER BY sort")
    List<Model> allByIds(List<Integer> ids);

    @Query("SELECT * FROM models WHERE widget_id = :widgetId ORDER BY sort")
    List<Model> allByWidgetId(int widgetId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Model... models);

    @Update
    void updateAll(Model... models);

    @Delete
    void delete(Model... models);

    @Query("DELETE FROM models")
    void deleteAll();

    @Query("DELETE FROM models WHERE widget_id = :widgetId")
    void deleteAllByWidgetId(int widgetId);
}
