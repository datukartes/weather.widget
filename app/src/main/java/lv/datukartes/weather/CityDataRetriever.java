package lv.datukartes.weather;

import android.content.Context;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CityDataRetriever extends WeatherDataRetriever {

    public static class CityFilter {

        String pattern;

        public CityFilter(String pattern)
        {
            this.pattern = pattern;
        }

        public boolean containsCity(WeatherData weatherData)
        {
            return weatherData.getMeta().getCity().equals(pattern);
        }
    }

    public CityDataRetriever(Context context) {
        super(context);
    }


    public void withCityData(final String city, final ResponseHandler handler)
    {
        this.withData(new ResponseHandler() {
            @Override
            public void onSuccess(ArrayList<WeatherData> data) {
                CityFilter cityFilter = new CityFilter(city);
                ArrayList<WeatherData> cityData = data.stream().filter(cityFilter::containsCity).collect(Collectors.toCollection(ArrayList<WeatherData>::new));
                handler.onSuccess(cityData);
            }

            @Override
            public void onError() {
                handler.onError();
            }
        });
    }

}
