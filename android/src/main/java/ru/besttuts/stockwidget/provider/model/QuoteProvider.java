package ru.besttuts.stockwidget.provider.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import lombok.Data;
import ru.besttuts.stockwidget.R;

/**
 * Created by roman on 09.09.2018.
 */
@Entity(tableName = "quote_providers")
@Data
public class QuoteProvider {

    @PrimaryKey
    @ColumnInfo(name = "code")
    @NonNull
    private String code;

    @ColumnInfo(name = "name")
    private String name;

    public QuoteProvider(@NonNull String code, String name) {
        this.code = code;
        this.name = name;
    }

    static final int AlFA_BANK = 1;

    public static int getProvider(String provider) {
        if (provider == null) return 0;

        switch (provider) {
            case "ALFA_BANK": return AlFA_BANK;
            default: return 0;
        }
    }

    public static int getDrawableId(int provider) {
        switch (provider) {
            case AlFA_BANK: return R.drawable.alfabank;
            default: return -1;
        }
    }
}
