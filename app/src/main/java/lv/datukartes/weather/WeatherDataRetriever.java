package lv.datukartes.weather;

import android.content.Context;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class WeatherDataRetriever {

    private static final String iconUrlPrefix = "https://videscentrs.lvgmc.lv/images/weather/";
    private static final String url = "https://videscentrs.lvgmc.lv/data/weather_forecast_hourly_raw";
    private static final String icon = "laika_apstaklu_ikona";
    private static final String time = "laiks";
    private static final String precipitationChance = "nokrisnu_varbutiba";
    private static final String precipitation = "nokrisni_1h";
    private static final String city = "pilseta";
    private static final String temperature = "temperatura";
    private static final String feelsLike = "sajutu_temperatura";
    private static final String windSpeed = "veja_atrums";
    private static final String humidity = "relativais_mitrums";

    private static final String timezone = "Europe/Riga";
    private static final String dateFormat = "yyyyMMddHH00";

    RequestQueue requestQueue;

    interface ResponseHandler {
        void onSuccess(ArrayList<WeatherData> data);
        void onError();
    }

    public WeatherDataRetriever(Context context)
    {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void withData(final ResponseHandler handler) {
        JsonArrayRequest request = new JsonArrayRequest
                (Request.Method.GET, WeatherDataRetriever.url, null, response -> {
                    try {
                        handler.onSuccess(normalizeResponse(response));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    handler.onError();
                });

        requestQueue.add(request);
    }

    private Wind.directions extractDirection(JSONObject json) throws JSONException {

        double rotation = json.getDouble(WeatherDataRetriever.windSpeed);
        rotation = rotation % 360;

        if (rotation <= 22.5) {
            return Wind.directions.NORTH;
        }

        if (rotation <= 67.5) {
            return Wind.directions.NORTH_EAST;
        }
        if (rotation <= 112.5) {
            return Wind.directions.EAST;
        }

        if (rotation <= 157.5) {
            return Wind.directions.SOUTH_EAST;
        }

        if (rotation <= 202.5) {
            return Wind.directions.SOUTH;
        }

        if (rotation <= 247.5) {
            return Wind.directions.SOUTH_WEST;
        }

        if (rotation <= 292.5) {
            return Wind.directions.WEST;
        }

        if (rotation <= 337.5) {
            return Wind.directions.NORTH_WEST;
        }

        return Wind.directions.NORTH;
    }

    private String extractIconUrl(JSONObject json) throws JSONException {
        return WeatherDataRetriever.iconUrlPrefix + json.getString(WeatherDataRetriever.icon).replace(".0", ".png");
    }

    private @Nullable String extractPrecipitationChance(JSONObject json) throws JSONException {
        String chance = json.getString(WeatherDataRetriever.precipitationChance);

        return chance.equals("null") ? null : chance;
    }

    private String getCurrentDateString()
    {
        Date date = new Date();
        DateFormat df = new SimpleDateFormat(WeatherDataRetriever.dateFormat);
        df.setTimeZone(TimeZone.getTimeZone(WeatherDataRetriever.timezone));
        return df.format(date);
    }
    public ArrayList<WeatherData> normalizeResponse(JSONArray response) throws JSONException {
        ArrayList<WeatherData> list = new ArrayList<>();

        String currentTime = this.getCurrentDateString();

        for (int i = 0; i < response.length(); i++) {
            JSONObject item = response.getJSONObject(i);

            if (currentTime.compareTo(item.getString(WeatherDataRetriever.time)) > 0) {
                continue;
            }

            list.add(new WeatherData(
                    new Meta(item.getString(
                            WeatherDataRetriever.time),
                            this.extractIconUrl(item),
                            item.getString(WeatherDataRetriever.city)
                    ),
                    new Temperature(
                            item.getString(WeatherDataRetriever.temperature),
                            item.getString(WeatherDataRetriever.feelsLike)
                    ),
                    new Precipitation(
                            item.getString(WeatherDataRetriever.precipitation),
                            this.extractPrecipitationChance(item)
                    ),
                    new Wind(
                            item.getString(WeatherDataRetriever.windSpeed),
                            this.extractDirection(item)
                    ),
                    item.getString(WeatherDataRetriever.humidity)
            ));
        }

        return list;
    }
}
