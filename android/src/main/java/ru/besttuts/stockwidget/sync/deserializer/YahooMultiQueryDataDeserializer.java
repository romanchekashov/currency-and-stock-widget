package ru.besttuts.stockwidget.sync.deserializer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import ru.besttuts.stockwidget.sync.model.YahooMultiQueryData;

/**
 * @author rchekashov
 *         created on 06.10.2016
 */
@Deprecated
public class YahooMultiQueryDataDeserializer implements JsonDeserializer<YahooMultiQueryData> {

    @Override
    public YahooMultiQueryData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        YahooMultiQueryData yahooMultiQueryData = new YahooMultiQueryData();
        JsonObject query = json.getAsJsonObject().getAsJsonObject("query");
        if (null == query) return yahooMultiQueryData;

        yahooMultiQueryData.count = query.get("count").getAsInt();
        yahooMultiQueryData.lang = query.get("count").getAsString();
        yahooMultiQueryData.created = query.get("created").getAsString();
        JsonObject resultsJsonObject = query.getAsJsonObject("results");
        if (resultsJsonObject.isJsonNull()) return yahooMultiQueryData;

        JsonElement resultsJsonElement = resultsJsonObject.get("results");
        if(resultsJsonElement.isJsonObject()){
            mapJsonElement(resultsJsonElement, yahooMultiQueryData);
        } else {
            JsonArray results = (JsonArray) resultsJsonElement;
            for (JsonElement je2 : results) {
                if (je2.isJsonObject()) {
                    mapJsonElement(je2, yahooMultiQueryData);
                }
            }
        }
        return yahooMultiQueryData;
    }

    private void mapJsonElement(JsonElement je2, YahooMultiQueryData yahooMultiQueryData) {
        JsonElement je3 = je2.getAsJsonObject().get("rate");
        if(null != je3){
            if(je3 instanceof JsonObject){
                YahooMultiQueryData.Rate rate = new Gson().fromJson(
                        je3, YahooMultiQueryData.Rate.class);
                if(null != rate){
                    yahooMultiQueryData.rates.add(rate);
                }
            } else {
                JsonArray rates = je3.getAsJsonArray();
                for (JsonElement je4 : rates) {
                    YahooMultiQueryData.Rate rate = new Gson().fromJson(
                            je4, YahooMultiQueryData.Rate.class);
                    if(null != rate){
                        yahooMultiQueryData.rates.add(rate);
                    }
                }
            }
        } else {
            je3 = je2.getAsJsonObject().get("quote");
            if(je3 instanceof JsonObject){
                YahooMultiQueryData.Quote quote = new Gson().fromJson(
                        je3, YahooMultiQueryData.Quote.class);
                if(null != quote && null != quote.Name){
                    yahooMultiQueryData.quotes.add(quote);
                }
            } else {
                JsonArray quotes = je3.getAsJsonArray();
                for (JsonElement je4 : quotes) {
                    YahooMultiQueryData.Quote quote = new Gson().fromJson(
                            je4, YahooMultiQueryData.Quote.class);
                    if(null != quote && null != quote.Name){
                        yahooMultiQueryData.quotes.add(quote);
                    }
                }
            }
        }
    }
}
