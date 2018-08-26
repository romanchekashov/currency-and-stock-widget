package ru.besttuts.stockwidget.sync.sparklab.dto;

/**
 * Created by rchekashov on 26.08.2018.
 */
public class CommodityInfoDto {
    private String code;
    private String symbol;
    private String name;
    private String category; // Energy, Metals, Agriculture

    public CommodityInfoDto() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
