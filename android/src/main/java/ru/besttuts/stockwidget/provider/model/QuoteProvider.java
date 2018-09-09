package ru.besttuts.stockwidget.provider.model;

import ru.besttuts.stockwidget.R;

/**
 * Created by roman on 09.09.2018.
 */
public interface QuoteProvider {
    int AlFA_BANK = 1;

    static int getProvider(String provider) {
        if (provider == null) return 0;

        switch (provider) {
            case "ALFA_BANK": return AlFA_BANK;
            default: return 0;
        }
    }

    static int getDrawableId(int provider) {
        switch (provider) {
            case AlFA_BANK: return R.drawable.alfabank;
            default: return -1;
        }
    }
}
