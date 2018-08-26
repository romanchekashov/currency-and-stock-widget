package ru.besttuts.stockwidget.provider.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ru.besttuts.stockwidget.provider.model.Quote;
import ru.besttuts.stockwidget.provider.model.Setting;

@Dao
public interface QuoteDao {

    @Query("SELECT * FROM quotes")
    List<Quote> getAll();

    @Query("SELECT * FROM quotes WHERE quote_symbol IN (:symbols)")
    List<Quote> getAllByWidgetId(String[] symbols);

    @Query("SELECT * FROM quotes WHERE quote_type = :type")
    List<Quote> getAllByQuoteType(int type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Quote... quotes);

    @Update
    void updateAll(Quote... quotes);

    @Delete
    void deleteAll(Quote... quotes);
}
