package ru.besttuts.stockwidget.sync.sparklab.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rchekashov on 26.08.2018.
 */
public class CurrencyInfoDto {
    private String code;
    private String name;
    private Boolean crypto = false;
    private List<String> countries = new ArrayList<>();

    public CurrencyInfoDto() {
    }

    public CurrencyInfoDto(String code, String name, Boolean crypto) {
        this.code = code;
        this.name = name;
        this.crypto = crypto;
    }

    public CurrencyInfoDto(String code, String name, Boolean crypto, List<String> countries) {
        this.code = code;
        this.name = name;
        this.crypto = crypto;
        this.countries = countries;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getCrypto() {
        return crypto;
    }

    public void setCrypto(Boolean crypto) {
        this.crypto = crypto;
    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }

    @Override
    public String toString() {
        return "CurrencyInfoDto{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", crypto=" + crypto +
                ", countries=" + countries +
                '}';
    }
}
