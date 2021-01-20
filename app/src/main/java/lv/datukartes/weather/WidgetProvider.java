package lv.datukartes.weather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.widget.RemoteViews;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetProvider extends AppWidgetProvider {

    private static final String SYNC_CLICKED    = "lv.datukartes.weather.widget.SYNC_CLICKED";
    private static final String latvian = "latviešu";
    private static final String widgetKey = "widgetId";
    private static final String disclaimerUrl = "https://videscentrs.lvgmc.lv";
    private static final String cityUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
    private static final String timeFormat = "HH:mm";
    interface ResponseHandler {
        void onSuccess(ArrayList<String> warnings, ArrayList<WeatherData> weatherData);
        void onError();
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            update(context, appWidgetManager, appWidgetId);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (SYNC_CLICKED.equals(intent.getAction())) {

            int widgetId = intent.getIntExtra(WidgetProvider.widgetKey, 0);
            WidgetProvider.update(context, AppWidgetManager.getInstance(context), widgetId);
        }
    }

    protected static void updateCityBlock(RemoteViews views, Context context, int widgetId, String city, int textColor)
    {
        Uri cityUri = Uri.parse(WidgetProvider.cityUrl);
        Intent cityIntent = new Intent(Intent.ACTION_VIEW, cityUri);
        CityMapper cityMapper = new CityMapper(context);
        PendingIntent cityPendingIntent = PendingIntent.getActivity(context, widgetId, cityIntent, 0);
        views.setOnClickPendingIntent(R.id.header_city, cityPendingIntent);
        views.setTextViewText(R.id.header_city, cityMapper.localize(city));
        views.setTextColor(R.id.header_city, textColor);
    }

    protected static void updateRefreshBlock(RemoteViews views, Context context, int widgetId, int textColor)
    {
        Intent intent = new Intent(context, WidgetProvider.class);
        intent.setAction(SYNC_CLICKED);
        intent.putExtra(WidgetProvider.widgetKey, widgetId);
        views.setOnClickPendingIntent(R.id.header_refresh, PendingIntent.getBroadcast(context, widgetId, intent, 0));
        views.setTextViewText(R.id.header_refresh, "\u21bb");
        views.setTextColor(R.id.header_refresh, textColor);
    }

    protected static void updateDisclaimerBlock(RemoteViews views, Context context, int widgetId, int textColor)
    {
        DateFormat dateFormat = new SimpleDateFormat(WidgetProvider.timeFormat);
        Date currentTime = Calendar.getInstance().getTime();
        SpannableString string = new SpannableString(context.getResources().getString(R.string.disclaimer));
        string.setSpan(new UnderlineSpan(), string.length()-5, string.length(), 0);
        Uri uri = Uri.parse(WidgetProvider.disclaimerUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, widgetId, intent, 0);
        views.setOnClickPendingIntent(R.id.footer_disclaimer, pendingIntent);
        views.setTextViewText(R.id.footer_disclaimer, string + " (" + dateFormat.format(currentTime) + ")");
        views.setTextColor(R.id.footer_disclaimer, textColor);
    }

    protected static void updateDataList(RemoteViews views, Context context, ArrayList<WeatherData> weatherDataArrayList, ArrayList<String> stringArrayList, int textColor)
    {
        Intent weatherDataIntent = new Intent(context, WidgetService.class);
        Bundle weatherDataBundle = new Bundle();
        weatherDataBundle.putParcelableArrayList(WidgetRemoteViewsFactory.weatherDataKey, weatherDataArrayList);
        weatherDataBundle.putStringArrayList(WidgetRemoteViewsFactory.warningsKey, stringArrayList);
        weatherDataBundle.putInt(WidgetRemoteViewsFactory.textColorKey, textColor);
        weatherDataIntent.putExtra(WidgetRemoteViewsFactory.bundleKey, weatherDataBundle);
        weatherDataIntent.setData(Uri.fromParts("content", String.valueOf(System.currentTimeMillis()), null));
        views.setRemoteAdapter(R.id.body_list, weatherDataIntent);
    }

    private static WarningsRetriever.locales getWarningsLocale()
    {
        String language =  Locale.getDefault().getDisplayLanguage();
        if (language.equals(WidgetProvider.latvian)) {
            return WarningsRetriever.locales.lv;
        } else {
            return WarningsRetriever.locales.en;
        }
    }

    public static void withData(Context context, String city, ResponseHandler handler)
    {
        CityDataRetriever cityDataRetriever = new CityDataRetriever(context);
        WarningsRetriever warningsRetriever = new WarningsRetriever(context);
        warningsRetriever.withData(WidgetProvider.getWarningsLocale(), new WarningsRetriever.ResponseHandler() {
            @Override
            public void onSuccess(ArrayList<String> stringArrayList) {
                cityDataRetriever.withCityData(city, new WeatherDataRetriever.ResponseHandler() {
                    @Override
                    public void onSuccess(ArrayList<WeatherData> weatherDataArrayList) {
                        handler.onSuccess(stringArrayList, weatherDataArrayList);
                    }

                    @Override
                    public void onError() {
                        handler.onError();
                    }
                });
            }

            @Override
            public void onError() {
                handler.onError();
            }
        });
    }

    public static void update(Context context, AppWidgetManager appWidgetManager, int widgetId)
    {
        RemoteViews views = new RemoteViews(
                context.getPackageName(),
                R.layout.widget
        );
        views.setViewVisibility(R.id.body_list, GONE);
        views.setViewVisibility(R.id.body_loading, VISIBLE);
        appWidgetManager.updateAppWidget(widgetId, views);
        String key = String.valueOf(widgetId);
        SharedPreferences prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        int background = prefs.getInt("background", Color.WHITE);
        int textColor = prefs.getInt("textColor", Color.BLACK);
        views.setInt(R.id.layout, "setBackgroundColor", background);
        String city = prefs.getString("city", null);
        if (city != null) {
            WidgetProvider.withData(context, city, new ResponseHandler() {
                @Override
                public void onSuccess(ArrayList<String> warnings, ArrayList<WeatherData> weatherDataArrayList) {
                    WidgetProvider.updateCityBlock(views, context, widgetId, city, textColor);
                    WidgetProvider.updateRefreshBlock(views, context, widgetId, textColor);
                    WidgetProvider.updateDisclaimerBlock(views, context, widgetId, textColor);
                    WidgetProvider.updateDataList(views, context, weatherDataArrayList, warnings, textColor);
                    views.setViewVisibility(R.id.body_loading, GONE);
                    views.setViewVisibility(R.id.body_list, VISIBLE);
                    appWidgetManager.updateAppWidget(widgetId, views);
                    appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.body_list);
                }

                @Override
                public void onError() {
                    views.setViewVisibility(R.id.body_loading, GONE);
                    views.setViewVisibility(R.id.body_list, VISIBLE);
                    appWidgetManager.updateAppWidget(widgetId, views);
                }
            });
        }

    }
}

