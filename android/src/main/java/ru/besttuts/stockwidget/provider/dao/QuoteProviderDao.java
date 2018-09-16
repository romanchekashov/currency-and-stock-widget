package ru.besttuts.stockwidget.provider.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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
