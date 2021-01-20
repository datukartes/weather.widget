package lv.datukartes.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    public static final String textColorKey = "textColor";
    public static final String warningsKey = "warnings";
    public static final String weatherDataKey = "weatherDataArrayList";
    public static final String bundleKey = "data";

    private static final String dateFormat = "E, dd.MM";
    private static final String timeFormat = "HH:mm";
    private static final String empty = "...";

    private Context context;
    private ArrayList<WeatherData> weatherDataArrayList;
    private ArrayList<String> warningsArrayList;
    private int textColor;

    public WidgetRemoteViewsFactory(Context context, Intent intent)
    {
        this.warningsArrayList = Objects.requireNonNull(intent.getBundleExtra(WidgetRemoteViewsFactory.bundleKey))
                .getStringArrayList(WidgetRemoteViewsFactory.warningsKey);
        this.weatherDataArrayList = Objects.requireNonNull(intent.getBundleExtra(WidgetRemoteViewsFactory.bundleKey))
                .getParcelableArrayList(WidgetRemoteViewsFactory.weatherDataKey);
        this.textColor = Objects.requireNonNull(intent.getBundleExtra(WidgetRemoteViewsFactory.bundleKey))
                .getInt(WidgetRemoteViewsFactory.textColorKey);
        this.context = context;
    }

    @Override
    public int getCount()
    {
        return this.warningsArrayList.size() + weatherDataArrayList.size();
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public RemoteViews getLoadingView()
    {
        RemoteViews remoteView = new RemoteViews(context.getPackageName(),
                R.layout.row);

        remoteView.setTextViewText(R.id.temperature_value, WidgetRemoteViewsFactory.empty);
        remoteView.setTextViewText(R.id.temperature_feels_like, WidgetRemoteViewsFactory.empty);
        remoteView.setTextViewText(R.id.precipitation_value, WidgetRemoteViewsFactory.empty);
        remoteView.setTextViewText(R.id.humidity, WidgetRemoteViewsFactory.empty);
        remoteView.setTextViewText(R.id.wind_value, WidgetRemoteViewsFactory.empty);
        remoteView.setTextViewText(R.id.wind_gusts, WidgetRemoteViewsFactory.empty);

        return this.setTextColors(remoteView);
    }

    @Override
    public RemoteViews getViewAt(int position)
    {
        @Nullable RemoteViews remoteViews = this.getWarningsViewAt(position);

        if (remoteViews != null) {
            return remoteViews;
        }

        return this.getWeatherDataViewAt(position - warningsArrayList.size());

    }

    @Nullable
    public RemoteViews getWarningsViewAt(int position)
    {
        if (warningsArrayList.size() <= position) {
            return null;
        }
        String warning = warningsArrayList.get(position);
        RemoteViews remoteView = new RemoteViews(context.getPackageName(),
                R.layout.warning);

        remoteView.setTextViewText(R.id.text, warning);
        remoteView.setTextColor(R.id.text, this.textColor);
        return remoteView;
    }

    public String getDateString(Date date)
    {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat(WidgetRemoteViewsFactory.dateFormat);
        return dateFormat.format(date);
    }

    public String getTimeString(Date date)
    {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat(WidgetRemoteViewsFactory.timeFormat);
        return dateFormat.format(date);
    }

    private String getLocalizedDirection(Wind.directions direction) {
        switch (direction) {
            case EAST:
                return context.getResources().getString(R.string.east);
            case WEST:
                return context.getResources().getString(R.string.west);
            case NORTH:
                return context.getResources().getString(R.string.north);
            case SOUTH:
                return context.getResources().getString(R.string.south);
            case NORTH_EAST:
                return context.getResources().getString(R.string.north) + context.getResources().getString(R.string.east);
            case NORTH_WEST:
                return context.getResources().getString(R.string.north) + context.getResources().getString(R.string.west);
            case SOUTH_EAST:
                return context.getResources().getString(R.string.south) + context.getResources().getString(R.string.east);
            case SOUTH_WEST:
                return context.getResources().getString(R.string.south) + context.getResources().getString(R.string.west);
            default:
                return "";
        }
    }

    @Nullable
    public RemoteViews getWeatherDataViewAt(int position)
    {
        if (weatherDataArrayList.size() <= position) {
            return null;
        }

        WeatherData weatherData = weatherDataArrayList.get(position);
        RemoteViews remoteView = new RemoteViews(context.getPackageName(),
                R.layout.row);
        try {
            Bitmap bitmap = Glide.with(context)
                    .asBitmap()
                    .load(weatherData.getMeta().getIconUrl())
                    .submit(20, 40)
                    .get();

            remoteView.setTextViewText(R.id.meta_time, this.getTimeString(weatherData.getMeta().getDate()));
            remoteView.setTextViewText(R.id.meta_date, this.getDateString(weatherData.getMeta().getDate()));
            remoteView.setImageViewBitmap(R.id.meta_icon_url, bitmap);
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }

        remoteView.setTextViewText(R.id.temperature_value, weatherData.getTemperature().getValue());
        remoteView.setTextViewText(R.id.temperature_feels_like, weatherData.getTemperature().getFeelsLike());
        remoteView.setTextViewText(R.id.precipitation_value, weatherData.getPrecipitation().getValue());
        remoteView.setTextViewText(R.id.humidity, weatherData.getHumidity());
        remoteView.setTextViewText(R.id.wind_value, weatherData.getWind().getSpeed());
        remoteView.setTextViewText(R.id.wind_gusts, this.getLocalizedDirection(weatherData.getWind().getDirection()));

        return this.setTextColors(remoteView);
    }

    private RemoteViews setTextColors(RemoteViews views)
    {
        views.setTextColor(R.id.meta_date, textColor);
        views.setTextColor(R.id.meta_time, textColor);
        views.setTextColor(R.id.temperature_feels_like, textColor);
        views.setTextColor(R.id.temperature_header, textColor);
        views.setTextColor(R.id.temperature_footer, textColor);
        views.setTextColor(R.id.temperature_value, textColor);
        views.setTextColor(R.id.humidity, textColor);
        views.setTextColor(R.id.humidity_footer, textColor);
        views.setTextColor(R.id.precipitation_header, textColor);
        views.setTextColor(R.id.precipitation_value, textColor);
        views.setTextColor(R.id.wind_header, textColor);
        views.setTextColor(R.id.wind_gusts, textColor);
        views.setTextColor(R.id.wind_value, textColor);
        views.setTextColor(R.id.wind_footer, textColor);

        return views;
    }
    @Override
    public int getViewTypeCount()
    {
        // TODO Auto-generated method stub
        return 2;
    }

    @Override
    public boolean hasStableIds()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onCreate()
    {
        onDataSetChanged();
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy()
    {
        weatherDataArrayList.clear();
        warningsArrayList.clear();
    }
}