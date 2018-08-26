package ru.besttuts.stockwidget.provider;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import ru.besttuts.stockwidget.provider.dao.ModelDao;
import ru.besttuts.stockwidget.provider.dao.QuoteDao;
import ru.besttuts.stockwidget.provider.dao.SettingDao;
import ru.besttuts.stockwidget.provider.model.Model;
import ru.besttuts.stockwidget.provider.model.Quote;
import ru.besttuts.stockwidget.provider.model.QuoteLastTradeDate;
import ru.besttuts.stockwidget.provider.model.Setting;

@Database(
        entities = {Setting.class, Model.class, Quote.class, QuoteLastTradeDate.class},
        version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase sInstance;

    public abstract SettingDao settingDao();

    public abstract ModelDao modelDao();

    public abstract QuoteDao quoteDao();

    public static AppDatabase getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "quote")
//                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                            .build();
                }
            }
        }
        return sInstance;
    }

//    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) {
//            database.execSQL("CREATE TABLE `Fruit` (`id` INTEGER, "
//                    + "`name` TEXT, PRIMARY KEY(`id`))");
//        }
//    };
}
