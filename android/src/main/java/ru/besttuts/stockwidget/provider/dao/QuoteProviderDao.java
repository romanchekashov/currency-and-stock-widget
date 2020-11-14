package ru.besttuts.stockwidget.provider.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.besttuts.stockwidget.provider.model.QuoteProvider;

@Dao
public interface QuoteProviderDao {

    @Query("SELECT * FROM quote_providers")
    List<QuoteProvider> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(QuoteProvider... settings);

    @Update
    void updateAll(QuoteProvider... settings);

    @Delete
    void deleteAll(QuoteProvider... settings);
}
