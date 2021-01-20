package lv.datukartes.weather;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ConfigureActivity extends Activity implements SeekBar.OnSeekBarChangeListener {

    private int transparency = 0;

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
            this.setPreferences(widgetId, cityMapper.reset(city));
            WidgetProvider.update(ConfigureActivity.this, AppWidgetManager.getInstance(this), widgetId);
            this.launch(widgetId);
        });
    }

    private void populateList()
    {
        ListView listView = findViewById(R.id.configure_cities);
        ProgressBar progressBar = findViewById(R.id.configure_loading);
        listView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        CityRetriever cityRetriever = new CityRetriever(this);
        cityRetriever.withCities(new CityRetriever.ResponseHandler() {
            @Override
            public void onSuccess(ArrayList<String> data) {
                CityMapper cityMapper = new CityMapper(ConfigureActivity.this);
                ArrayList<String> cityData = data.stream().map(cityMapper::localize).distinct().collect(Collectors.toCollection(ArrayList::new));
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<>(ConfigureActivity.this, R.layout.city_row, cityData);
                listView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
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

        SeekBar seekBar = findViewById(R.id.opacity);
        seekBar.setProgress(this.transparency);
        seekBar.setOnSeekBarChangeListener(this);

        this.populateList();
    }

    private int getColor(String name)
    {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = ConfigureActivity.this.getTheme();
        int colorAttr = ConfigureActivity.this.getResources().getIdentifier(name, "attr", ConfigureActivity.this.getPackageName());
        theme.resolveAttribute(colorAttr, typedValue, true);
        return typedValue.data;
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

    public void materialThemeSelected(View view) {
        this.setTheme(R.style.Light);
    }

    public void darkThemeSelected(View view) {
        this.setTheme(R.style.Dark);
    }

    private void setPreferences(int widgetId, String city)
    {
        SharedPreferences.Editor editor = getSharedPreferences(String.valueOf(widgetId), MODE_PRIVATE).edit();
        editor.putString("city", city);

        int background = this.getColor("android:background");
        int alpha = Color.argb(255 - this.transparency, Color.red(background), Color.green(background), Color.blue(background));
        editor.putInt("background", alpha);
        editor.putInt("textColor", this.getColor("android:textColor"));
        editor.commit();
    }

    private  boolean isWidgetIdValid(int widgetId)
    {
        return widgetId != AppWidgetManager.INVALID_APPWIDGET_ID;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        this.transparency = seekBar.getProgress();
    }
}
