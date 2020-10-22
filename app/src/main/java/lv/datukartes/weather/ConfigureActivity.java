package lv.datukartes.weather;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ConfigureActivity extends Activity {

    private void cancel()
    {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void setClickHandler(ListView listView)
    {
        listView.setClickable(true);
        listView.setOnItemClickListener((arg0, arg1, position, arg3) -> {

            CityMapper cityMapper = new CityMapper(getApplicationContext());
            int widgetId = this.getWidgetId();
            if (!this.isWidgetIdValid(widgetId)) {
                this.cancel();
            }

            String city = listView.getItemAtPosition(position).toString();
            ConfigureActivity.this.setPreferences(widgetId, cityMapper.reset(city));
            this.launch(widgetId);
        });
    }
    private void setPreferences(int widgetId, String city)
    {
        getSharedPreferences("_", MODE_PRIVATE).edit().putString(widgetId + "_city", city).commit();
        WidgetProvider.update(ConfigureActivity.this, AppWidgetManager.getInstance(this), widgetId);
    }

    private void populateList(ListView listView)
    {
        CityRetriever cityRetriever = new CityRetriever(this);
        cityRetriever.withCities(new CityRetriever.ResponseHandler() {
            @Override
            public void onSuccess(ArrayList<String> data) {
                CityMapper cityMapper = new CityMapper(getApplicationContext());
                ArrayList<String> cityData = data.stream().map(cityMapper::localize).distinct().collect(Collectors.toCollection(ArrayList::new));
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_list_item_1, cityData);
                listView.setAdapter(adapter);
                ConfigureActivity.this.setClickHandler(listView);
            }

            @Override
            public void onError() {
                ConfigureActivity.this.cancel();
            }
        });
    }

    private void launch(int widgetId)
    {
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
    @SuppressLint("ApplySharedPref")
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        int widgetId = this.getWidgetId();
        if (!this.isWidgetIdValid(widgetId)) {
            this.cancel();
        }

        setContentView(R.layout.configure);
        ListView cityList = findViewById(R.id.cities);
        this.populateList(cityList);
    }

    private int getWidgetId()
    {
        int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        return widgetId;
    }

    private  boolean isWidgetIdValid(int widgetId)
    {
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            return false;
        }

        return true;
    }
}
