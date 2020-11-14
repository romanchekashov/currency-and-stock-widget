package ru.besttuts.stockwidget.provider.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.besttuts.stockwidget.provider.model.Setting;

@Dao
public interface SettingDao {

    @Query("SELECT * FROM settings")
    List<Setting> getAll();

    @Query("SELECT * FROM settings WHERE widget_id = :widgetId ORDER BY quote_position ASC")
    List<Setting> allByWidgetId(int widgetId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Setting... settings);

    @Update
    void updateAll(Setting... settings);

    @Delete
    void deleteAll(Setting... settings);
}
