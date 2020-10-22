package lv.datukartes.weather;

import android.content.Context;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class CityRetriever extends WeatherDataRetriever {

    interface ResponseHandler {
        void onSuccess(ArrayList<String> data);
        void onError();
    }

    public CityRetriever(Context context)
    {
        super(context);
    }

    public static class CityExtractor {

        public static String extract(WeatherData weatherData)
        {
            return weatherData.getMeta().getCity();
        }
    }

    public void withCities(final ResponseHandler handler)
    {
        super.withData(new WeatherDataRetriever.ResponseHandler() {
            @Override
            public void onSuccess(ArrayList<WeatherData> data) {
                ArrayList<String> cityData = data.stream().map(CityExtractor::extract).distinct().collect(Collectors.toCollection(ArrayList<String>::new));
                handler.onSuccess(cityData);
            }

            @Override
            public void onError() {
                handler.onError();
            }
        });
    }
}
