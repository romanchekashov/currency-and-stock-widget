package ru.besttuts.stockwidget.sync.sparklab.dto;

public class QuoteDto extends QuoteBaseDto {
    private String code;
    private String expiredUseNewSymbol;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getExpiredUseNewSymbol() {
        return expiredUseNewSymbol;
    }

    public void setExpiredUseNewSymbol(String expiredUseNewSymbol) {
        this.expiredUseNewSymbol = expiredUseNewSymbol;
    }
}
