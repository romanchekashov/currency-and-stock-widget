package ru.besttuts.stockwidget.provider.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

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
