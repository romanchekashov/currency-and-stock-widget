package ru.besttuts.stockwidget.provider.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ru.besttuts.stockwidget.provider.model.Setting;

@Dao
public interface SettingDao {

    @Query("SELECT * FROM settings")
    List<Setting> getAll();

    @Query("SELECT * FROM settings WHERE widget_id = :widgetId")
    List<Setting> allByWidgetId(int widgetId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Setting... settings);

    @Update
    void updateAll(Setting... settings);

    @Delete
    void deleteAll(Setting... settings);
}
