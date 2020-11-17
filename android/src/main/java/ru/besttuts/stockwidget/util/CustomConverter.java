package ru.besttuts.stockwidget.util;

import ru.besttuts.stockwidget.provider.model.Model;
import ru.besttuts.stockwidget.provider.model.Quote;
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
        if (dto.getBp() != null) model.setBuyPrice(dto.getBp());
        if (dto.getSp() != null) model.setSellPrice(dto.getSp());
        return model;
    }

    public static Model toModel(Quote dto) {
        Model model = new Model();
        model.setId(dto.getId());
        model.setSymbol(dto.getSymbol());
        model.setChange(dto.getChange());
        model.setRate(dto.getRate());
        model.setCurrency(dto.getCurrency());
        model.setName(dto.getName());
        model.setQuoteType(dto.getQuoteType());
        model.setQuoteProvider(dto.getQuoteProvider());
        model.setBuyPrice(dto.getBuyPrice());
        model.setSellPrice(dto.getSellPrice());
        return model;
    }

    public static Quote toQuote(MobileQuoteShort dto) {
        Quote model = new Quote();
        if (dto.getI() != null) model.setId(dto.getI());
        if (dto.getS() != null) model.setSymbol(dto.getS());
        if (dto.getC() != null) model.setChange(dto.getC());
        if (dto.getR() != null) model.setRate(dto.getR());
        if (dto.getCu() != null)
            model.setCurrency(ru.besttuts.stockwidget.provider.model.Currency.values()[dto.getCu()].toString());
        if (dto.getN() != null) model.setName(dto.getN());
        if (dto.getQt() != null) model.setQuoteType(dto.getQt());
        if (dto.getQp() != null) model.setQuoteProvider(dto.getQp());
        if (dto.getBp() != null) model.setBuyPrice(dto.getBp());
        if (dto.getSp() != null) model.setSellPrice(dto.getSp());
        return model;
    }
}
