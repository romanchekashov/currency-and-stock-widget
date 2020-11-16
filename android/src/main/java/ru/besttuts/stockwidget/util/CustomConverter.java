package ru.besttuts.stockwidget.util;

import ru.besttuts.stockwidget.provider.model.Model;
import ru.besttuts.stockwidget.sync.sparklab.dto.MobileQuoteShort;

import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * @author rchekashov
 * created on 06.10.2016
 */
public class CustomConverter {
    private static final String TAG = makeLogTag(CustomConverter.class);

    public static Model toModel(MobileQuoteShort dto) {
        Model model = new Model();
        if (dto.getI() != null) model.setId(dto.getI());
        if (dto.getS() != null) model.setSymbol(dto.getS());
        if (dto.getC() != null) model.setChange(dto.getC());
        if (dto.getR() != null) model.setRate(dto.getR());
        if (dto.getCu() != null)
            model.setCurrency(ru.besttuts.stockwidget.provider.model.Currency.values()[dto.getCu()].toString());
        if (dto.getN() != null) model.setName(dto.getN());
        if (dto.getQt() != null) model.setQuoteType(dto.getQt());
        if (dto.getQp() != null) model.setQuoteProvider(dto.getQp());
        return model;
    }
}
