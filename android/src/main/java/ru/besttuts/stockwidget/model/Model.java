package ru.besttuts.stockwidget.model;

/**
 * Created by roman on 08.01.2015.
 */
public class Model {

    protected String id; // symbol
    protected String name;
    protected double rate;
    protected double change;
    protected String percentChange;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRate() {
        return rate;
    }

    public String getRateToString() {
        return String.valueOf((float)Math.round(rate*100)/100);
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getChange() {
        return change;
    }

    public String getChangeToString() {
        return String.valueOf((float)Math.round(change*10000)/10000);
    }

    public void setChange(double change) {
        this.change = (double)Math.round(change * 10000) / 10000;
        if (0 != (int)(rate * 10000)) {
            double pChange = change * 100 / rate;
            percentChange = String.valueOf((float)Math.round(pChange * 10000) / 10000) + "%";
        }
    }

    public String getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(String percentChange) {
        this.percentChange = percentChange;
    }

    @Override
    public String toString() {
        return "Model{" +
                "name='" + name + '\'' +
                ", rate=" + rate +
                ", change=" + change +
                ", percentChange='" + percentChange + '\'' +
                '}';
    }
}
