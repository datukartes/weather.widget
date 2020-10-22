package lv.datukartes.weather;

import android.os.Parcel;
import android.os.Parcelable;
public class WeatherData implements Parcelable {

    private Temperature temperature;
    private Meta meta;
    private Precipitation precipitation;
    private Wind wind;

    public Temperature getTemperature() {
        return temperature;
    }

    public Meta getMeta() {
        return meta;
    }

    public Precipitation getPrecipitation() {
        return precipitation;
    }

    public Wind getWind() {
        return wind;
    }

    public String getHumidity() {
        return humidity;
    }

    private String humidity;

    public WeatherData(Meta meta, Temperature temperature, Precipitation precipitation, Wind wind, String humidity) {
        this.meta = meta;
        this.temperature = temperature;
        this.precipitation = precipitation;
        this.wind = wind;
        this.humidity = humidity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.getMeta(), flags);
        dest.writeParcelable(this.getTemperature(), flags);
        dest.writeParcelable(this.getPrecipitation(), flags);
        dest.writeParcelable(this.getWind(), flags);
        dest.writeString(this.getHumidity());
    }

    public static final Parcelable.Creator<WeatherData> CREATOR
            = new Parcelable.Creator<WeatherData>() {
        public WeatherData createFromParcel(Parcel in) {
            return new WeatherData(
                    in.readParcelable(Meta.class.getClassLoader()),
                    in.readParcelable(Temperature.class.getClassLoader()),
                    in.readParcelable(Precipitation.class.getClassLoader()),
                    in.readParcelable(Wind.class.getClassLoader()),
                    in.readString()
            );
        }

        public WeatherData[] newArray(int size) {
            return new WeatherData[size];
        }
    };
}
