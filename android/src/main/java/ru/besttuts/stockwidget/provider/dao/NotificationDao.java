package ru.besttuts.stockwidget.provider.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import ru.besttuts.stockwidget.provider.model.Notification;

@Dao
public interface NotificationDao {

    @Query("SELECT * FROM notifications")
    Single<List<Notification>> getAll();

//    @Query("SELECT * FROM settings WHERE widget_id = :widgetId ORDER BY quote_position ASC")
//    List<Setting> allByWidgetId(int widgetId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(Notification... notifications);

    @Update
    void updateAll(Notification... notifications);

    @Delete
    void deleteAll(Notification... notifications);
}
