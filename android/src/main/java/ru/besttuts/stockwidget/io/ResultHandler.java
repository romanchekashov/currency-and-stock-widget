/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.besttuts.stockwidget.io;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.besttuts.stockwidget.io.model.Result;
import ru.besttuts.stockwidget.util.LogUtils;

import static ru.besttuts.stockwidget.util.LogUtils.LOGE;

public class ResultHandler {

    private static final String TAG = LogUtils.makeLogTag(ResultHandler.class);

    // map from room ID to Room model object
    private List<Result> mResults = new ArrayList<>();

    public ResultHandler() {
        //
    }

    public List<Result> getResults() {
        return mResults;
    }

    private void process(JSONObject o) throws JSONException, IOException {
        Result result = new Result();
        result.symbol = o.getString("symbol");
        result.name = o.getString("name");
        result.exch = o.getString("exch");
        result.type = o.getString("type");
        result.exchDisp = o.getString("exchDisp");
        result.typeDisp = o.getString("typeDisp");

        mResults.add(result);
    }

    public void readAndParseJSON(String in) {
        try {
            JSONObject reader = new JSONObject(in);

            if (reader.isNull("ResultSet") || reader.getJSONObject("ResultSet").isNull("Result")) {
                return;
            }

            Object oResults = reader.getJSONObject("ResultSet").get("Result");

            if (oResults instanceof JSONArray) {
                JSONArray results = (JSONArray) oResults;

                for (int i = 0; i < results.length(); i++) {
                    process(results.getJSONObject(i));
                }

            } else {
                process((JSONObject) oResults);
            }

        } catch (JSONException jsone) {
            LOGE(TAG, jsone.getMessage());
            jsone.printStackTrace();
        } catch (IOException ioe) {
            LOGE(TAG, ioe.getMessage());
            ioe.printStackTrace();
        }

    }

}
