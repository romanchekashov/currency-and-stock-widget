package ru.besttuts.stockwidget.sync.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rchekashov
 *         created on 04.10.2016
 */
@Deprecated
public class YahooMultiQueryData {
    public int count;
    public String created;
    public String lang;
    public List<Rate> rates = new ArrayList<>();
    public List<Quote> quotes = new ArrayList<>();

    public YahooMultiQueryData() {
    }

    public static class Rate {
        public final String id;
        public final String Name;
        public final Double Rate;
        public final String Date;
        public final String Time;
        public final String Ask;
        public final String Bid;

        public Rate(String id, String name, Double rate,
                    String date, String time, String ask, String bid) {
            this.id = id;
            Name = name;
            Rate = rate;
            Date = date;
            Time = time;
            Ask = ask;
            Bid = bid;
        }

        @Override
        public String toString() {
            return "Rate{" +
                    "id='" + id + '\'' +
                    ", Name='" + Name + '\'' +
                    ", Rate=" + Rate +
                    ", Date='" + Date + '\'' +
                    ", Time='" + Time + '\'' +
                    ", Ask='" + Ask + '\'' +
                    ", Bid='" + Bid + '\'' +
                    '}';
        }
    }

    public static class Quote {
        public String symbol;
        public String Ask;
        public String AverageDailyVolume;
        public String Bid;
        public String AskRealtime;
        public String BidRealtime;
        public String BookValue;
        public String Change_PercentChange;
        public Double Change;
        public String Commission;
        public String Currency;
        public String ChangeRealtime;
        public String AfterHoursChangeRealtime;
        public String DividendShare;
        public String LastTradeDate;
        public String TradeDate;
        public String EarningsShare;
        public String ErrorIndicationreturnedforsymbolchangedinvalid;
        public String EPSEstimateCurrentYear;
        public String EPSEstimateNextYear;
        public String EPSEstimateNextQuarter;
        public String DaysLow;
        public String DaysHigh;
        public String YearLow;
        public String YearHigh;
        public String HoldingsGainPercent;
        public String AnnualizedGain;
        public String HoldingsGain;
        public String HoldingsGainPercentRealtime;
        public String HoldingsGainRealtime;
        public String MoreInfo;
        public String OrderBookRealtime;
        public String MarketCapitalization;
        public String MarketCapRealtime;
        public String EBITDA;
        public String ChangeFromYearLow;
        public String PercentChangeFromYearLow;
        public String LastTradeRealtimeWithTime;
        public String ChangePercentRealtime;
        public String ChangeFromYearHigh;
        public String PercebtChangeFromYearHigh;
        public String LastTradeWithTime;
        public Double LastTradePriceOnly;
        public String HighLimit;
        public String LowLimit;
        public String DaysRange;
        public String DaysRangeRealtime;
        public String FiftydayMovingAverage;
        public String TwoHundreddayMovingAverage;
        public String ChangeFromTwoHundreddayMovingAverage;
        public String PercentChangeFromTwoHundreddayMovingAverage;
        public String ChangeFromFiftydayMovingAverage;
        public String PercentChangeFromFiftydayMovingAverage;
        public String Name;
        public String Notes;
        public String Open;
        public String PreviousClose;
        public String PricePaid;
        public String ChangeinPercent;
        public String PriceSales;
        public String PriceBook;
        public String ExDividendDate;
        public String PERatio;
        public String DividendPayDate;
        public String PERatioRealtime;
        public String PEGRatio;
        public String PriceEPSEstimateCurrentYear;
        public String PriceEPSEstimateNextYear;
        public String Symbol;
        public String SharesOwned;
        public String ShortRatio;
        public String LastTradeTime;
        public String TickerTrend;
        public String OneyrTargetPrice;
        public String Volume;
        public String HoldingsValue;
        public String HoldingsValueRealtime;
        public String YearRange;
        public String DaysValueChange;
        public String DaysValueChangeRealtime;
        public String StockExchange;
        public String DividendYield;
        public String PercentChange;

        public Quote() {
        }

        public Quote(String symbol, String ask, String averageDailyVolume,
                     String bid, String askRealtime, String bidRealtime, String bookValue,
                     String change_PercentChange, Double change, String commission,
                     String currency, String changeRealtime, String afterHoursChangeRealtime,
                     String dividendShare, String lastTradeDate, String tradeDate,
                     String earningsShare, String errorIndicationreturnedforsymbolchangedinvalid,
                     String EPSEstimateCurrentYear, String EPSEstimateNextYear,
                     String EPSEstimateNextQuarter, String daysLow, String daysHigh, String yearLow,
                     String yearHigh, String holdingsGainPercent, String annualizedGain,
                     String holdingsGain, String holdingsGainPercentRealtime,
                     String holdingsGainRealtime, String moreInfo, String orderBookRealtime,
                     String marketCapitalization, String marketCapRealtime, String EBITDA,
                     String changeFromYearLow, String percentChangeFromYearLow,
                     String lastTradeRealtimeWithTime, String changePercentRealtime,
                     String changeFromYearHigh, String percebtChangeFromYearHigh,
                     String lastTradeWithTime, Double lastTradePriceOnly, String highLimit,
                     String lowLimit, String daysRange, String daysRangeRealtime,
                     String fiftydayMovingAverage, String twoHundreddayMovingAverage,
                     String changeFromTwoHundreddayMovingAverage,
                     String percentChangeFromTwoHundreddayMovingAverage,
                     String changeFromFiftydayMovingAverage, String percentChangeFromFiftydayMovingAverage,
                     String name, String notes, String open, String previousClose, String pricePaid,
                     String changeinPercent, String priceSales, String priceBook, String exDividendDate,
                     String PERatio, String dividendPayDate, String PERatioRealtime, String PEGRatio,
                     String priceEPSEstimateCurrentYear, String priceEPSEstimateNextYear, String symbol1,
                     String sharesOwned, String shortRatio, String lastTradeTime, String tickerTrend,
                     String oneyrTargetPrice, String volume, String holdingsValue,
                     String holdingsValueRealtime, String yearRange, String daysValueChange,
                     String daysValueChangeRealtime, String stockExchange, String dividendYield,
                     String percentChange) {
            this.symbol = symbol;
            Ask = ask;
            AverageDailyVolume = averageDailyVolume;
            Bid = bid;
            AskRealtime = askRealtime;
            BidRealtime = bidRealtime;
            BookValue = bookValue;
            Change_PercentChange = change_PercentChange;
            Change = change;
            Commission = commission;
            Currency = currency;
            ChangeRealtime = changeRealtime;
            AfterHoursChangeRealtime = afterHoursChangeRealtime;
            DividendShare = dividendShare;
            LastTradeDate = lastTradeDate;
            TradeDate = tradeDate;
            EarningsShare = earningsShare;
            ErrorIndicationreturnedforsymbolchangedinvalid = errorIndicationreturnedforsymbolchangedinvalid;
            this.EPSEstimateCurrentYear = EPSEstimateCurrentYear;
            this.EPSEstimateNextYear = EPSEstimateNextYear;
            this.EPSEstimateNextQuarter = EPSEstimateNextQuarter;
            DaysLow = daysLow;
            DaysHigh = daysHigh;
            YearLow = yearLow;
            YearHigh = yearHigh;
            HoldingsGainPercent = holdingsGainPercent;
            AnnualizedGain = annualizedGain;
            HoldingsGain = holdingsGain;
            HoldingsGainPercentRealtime = holdingsGainPercentRealtime;
            HoldingsGainRealtime = holdingsGainRealtime;
            MoreInfo = moreInfo;
            OrderBookRealtime = orderBookRealtime;
            MarketCapitalization = marketCapitalization;
            MarketCapRealtime = marketCapRealtime;
            this.EBITDA = EBITDA;
            ChangeFromYearLow = changeFromYearLow;
            PercentChangeFromYearLow = percentChangeFromYearLow;
            LastTradeRealtimeWithTime = lastTradeRealtimeWithTime;
            ChangePercentRealtime = changePercentRealtime;
            ChangeFromYearHigh = changeFromYearHigh;
            PercebtChangeFromYearHigh = percebtChangeFromYearHigh;
            LastTradeWithTime = lastTradeWithTime;
            LastTradePriceOnly = lastTradePriceOnly;
            HighLimit = highLimit;
            LowLimit = lowLimit;
            DaysRange = daysRange;
            DaysRangeRealtime = daysRangeRealtime;
            FiftydayMovingAverage = fiftydayMovingAverage;
            TwoHundreddayMovingAverage = twoHundreddayMovingAverage;
            ChangeFromTwoHundreddayMovingAverage = changeFromTwoHundreddayMovingAverage;
            PercentChangeFromTwoHundreddayMovingAverage = percentChangeFromTwoHundreddayMovingAverage;
            ChangeFromFiftydayMovingAverage = changeFromFiftydayMovingAverage;
            PercentChangeFromFiftydayMovingAverage = percentChangeFromFiftydayMovingAverage;
            Name = name;
            Notes = notes;
            Open = open;
            PreviousClose = previousClose;
            PricePaid = pricePaid;
            ChangeinPercent = changeinPercent;
            PriceSales = priceSales;
            PriceBook = priceBook;
            ExDividendDate = exDividendDate;
            this.PERatio = PERatio;
            DividendPayDate = dividendPayDate;
            this.PERatioRealtime = PERatioRealtime;
            this.PEGRatio = PEGRatio;
            PriceEPSEstimateCurrentYear = priceEPSEstimateCurrentYear;
            PriceEPSEstimateNextYear = priceEPSEstimateNextYear;
            Symbol = symbol1;
            SharesOwned = sharesOwned;
            ShortRatio = shortRatio;
            LastTradeTime = lastTradeTime;
            TickerTrend = tickerTrend;
            OneyrTargetPrice = oneyrTargetPrice;
            Volume = volume;
            HoldingsValue = holdingsValue;
            HoldingsValueRealtime = holdingsValueRealtime;
            YearRange = yearRange;
            DaysValueChange = daysValueChange;
            DaysValueChangeRealtime = daysValueChangeRealtime;
            StockExchange = stockExchange;
            DividendYield = dividendYield;
            PercentChange = percentChange;
        }

        @Override
        public String toString() {
            return "Quote{" +
                    "symbol='" + symbol + '\'' +
                    ", Ask='" + Ask + '\'' +
                    ", AverageDailyVolume='" + AverageDailyVolume + '\'' +
                    ", Bid='" + Bid + '\'' +
                    ", AskRealtime='" + AskRealtime + '\'' +
                    ", BidRealtime='" + BidRealtime + '\'' +
                    ", BookValue='" + BookValue + '\'' +
                    ", Change_PercentChange='" + Change_PercentChange + '\'' +
                    ", Change=" + Change +
                    ", Commission='" + Commission + '\'' +
                    ", Currency='" + Currency + '\'' +
                    ", ChangeRealtime='" + ChangeRealtime + '\'' +
                    ", AfterHoursChangeRealtime='" + AfterHoursChangeRealtime + '\'' +
                    ", DividendShare='" + DividendShare + '\'' +
                    ", LastTradeDate='" + LastTradeDate + '\'' +
                    ", TradeDate='" + TradeDate + '\'' +
                    ", EarningsShare='" + EarningsShare + '\'' +
                    ", ErrorIndicationreturnedforsymbolchangedinvalid='" + ErrorIndicationreturnedforsymbolchangedinvalid + '\'' +
                    ", EPSEstimateCurrentYear='" + EPSEstimateCurrentYear + '\'' +
                    ", EPSEstimateNextYear='" + EPSEstimateNextYear + '\'' +
                    ", EPSEstimateNextQuarter='" + EPSEstimateNextQuarter + '\'' +
                    ", DaysLow='" + DaysLow + '\'' +
                    ", DaysHigh='" + DaysHigh + '\'' +
                    ", YearLow='" + YearLow + '\'' +
                    ", YearHigh='" + YearHigh + '\'' +
                    ", HoldingsGainPercent='" + HoldingsGainPercent + '\'' +
                    ", AnnualizedGain='" + AnnualizedGain + '\'' +
                    ", HoldingsGain='" + HoldingsGain + '\'' +
                    ", HoldingsGainPercentRealtime='" + HoldingsGainPercentRealtime + '\'' +
                    ", HoldingsGainRealtime='" + HoldingsGainRealtime + '\'' +
                    ", MoreInfo='" + MoreInfo + '\'' +
                    ", OrderBookRealtime='" + OrderBookRealtime + '\'' +
                    ", MarketCapitalization='" + MarketCapitalization + '\'' +
                    ", MarketCapRealtime='" + MarketCapRealtime + '\'' +
                    ", EBITDA='" + EBITDA + '\'' +
                    ", ChangeFromYearLow='" + ChangeFromYearLow + '\'' +
                    ", PercentChangeFromYearLow='" + PercentChangeFromYearLow + '\'' +
                    ", LastTradeRealtimeWithTime='" + LastTradeRealtimeWithTime + '\'' +
                    ", ChangePercentRealtime='" + ChangePercentRealtime + '\'' +
                    ", ChangeFromYearHigh='" + ChangeFromYearHigh + '\'' +
                    ", PercebtChangeFromYearHigh='" + PercebtChangeFromYearHigh + '\'' +
                    ", LastTradeWithTime='" + LastTradeWithTime + '\'' +
                    ", LastTradePriceOnly=" + LastTradePriceOnly +
                    ", HighLimit='" + HighLimit + '\'' +
                    ", LowLimit='" + LowLimit + '\'' +
                    ", DaysRange='" + DaysRange + '\'' +
                    ", DaysRangeRealtime='" + DaysRangeRealtime + '\'' +
                    ", FiftydayMovingAverage='" + FiftydayMovingAverage + '\'' +
                    ", TwoHundreddayMovingAverage='" + TwoHundreddayMovingAverage + '\'' +
                    ", ChangeFromTwoHundreddayMovingAverage='" + ChangeFromTwoHundreddayMovingAverage + '\'' +
                    ", PercentChangeFromTwoHundreddayMovingAverage='" + PercentChangeFromTwoHundreddayMovingAverage + '\'' +
                    ", ChangeFromFiftydayMovingAverage='" + ChangeFromFiftydayMovingAverage + '\'' +
                    ", PercentChangeFromFiftydayMovingAverage='" + PercentChangeFromFiftydayMovingAverage + '\'' +
                    ", Name='" + Name + '\'' +
                    ", Notes='" + Notes + '\'' +
                    ", Open='" + Open + '\'' +
                    ", PreviousClose='" + PreviousClose + '\'' +
                    ", PricePaid='" + PricePaid + '\'' +
                    ", ChangeinPercent='" + ChangeinPercent + '\'' +
                    ", PriceSales='" + PriceSales + '\'' +
                    ", PriceBook='" + PriceBook + '\'' +
                    ", ExDividendDate='" + ExDividendDate + '\'' +
                    ", PERatio='" + PERatio + '\'' +
                    ", DividendPayDate='" + DividendPayDate + '\'' +
                    ", PERatioRealtime='" + PERatioRealtime + '\'' +
                    ", PEGRatio='" + PEGRatio + '\'' +
                    ", PriceEPSEstimateCurrentYear='" + PriceEPSEstimateCurrentYear + '\'' +
                    ", PriceEPSEstimateNextYear='" + PriceEPSEstimateNextYear + '\'' +
                    ", Symbol='" + Symbol + '\'' +
                    ", SharesOwned='" + SharesOwned + '\'' +
                    ", ShortRatio='" + ShortRatio + '\'' +
                    ", LastTradeTime='" + LastTradeTime + '\'' +
                    ", TickerTrend='" + TickerTrend + '\'' +
                    ", OneyrTargetPrice='" + OneyrTargetPrice + '\'' +
                    ", Volume='" + Volume + '\'' +
                    ", HoldingsValue='" + HoldingsValue + '\'' +
                    ", HoldingsValueRealtime='" + HoldingsValueRealtime + '\'' +
                    ", YearRange='" + YearRange + '\'' +
                    ", DaysValueChange='" + DaysValueChange + '\'' +
                    ", DaysValueChangeRealtime='" + DaysValueChangeRealtime + '\'' +
                    ", StockExchange='" + StockExchange + '\'' +
                    ", DividendYield='" + DividendYield + '\'' +
                    ", PercentChange='" + PercentChange + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        StringBuilder sbRates = new StringBuilder();
        sbRates.append("[");
        for (Rate rate: rates) {
            sbRates.append(rate.toString());
            sbRates.append(",");
        }
        sbRates.append("]");

        StringBuilder sbQuotes = new StringBuilder();
        sbQuotes.append("[");
        for (Quote quote: quotes) {
            sbQuotes.append(quote.toString());
            sbQuotes.append(",");
        }
        sbQuotes.append("]");

        return "YahooMultiQueryData{" +
                "count=" + count +
                ", created='" + created + '\'' +
                ", lang='" + lang + '\'' +
                ", rates=" + sbRates.toString() +
                ", quotes=" + sbQuotes.toString() +
                '}';
    }
}
