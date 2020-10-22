package lv.datukartes.weather;

import android.os.Parcel;
import android.os.Parcelable;

public class Temperature implements Parcelable {

    private String value;

    public String getValue() {
        return value;
    }

    public String getFeelsLike() {
        return feelsLike;
    }

    private String feelsLike;

    public Temperature(String value, String feelsLike)
    {
        this.value = value;
        this.feelsLike = feelsLike;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getValue());
        dest.writeString(this.getFeelsLike());
    }

    public static final Parcelable.Creator<Temperature> CREATOR
            = new Parcelable.Creator<Temperature>() {
        public Temperature createFromParcel(Parcel in) {
            return new Temperature(in.readString(), in.readString());
        }

        public Temperature[] newArray(int size) {
            return new Temperature[size];
        }
    };
}
