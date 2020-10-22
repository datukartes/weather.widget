package lv.datukartes.weather;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public class Precipitation implements Parcelable {
    public String getValue() {
        return value;
    }

    @Nullable
    public String getChance() {
        return chance;
    }

    private String value;
    private @Nullable String chance;
    public Precipitation(String value, @Nullable String chance)
    {
        this.value = value;
        this.chance = chance;
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getValue());
        dest.writeString(this.getChance());
    }

    public static final Parcelable.Creator<Precipitation> CREATOR
            = new Parcelable.Creator<Precipitation>() {
        public Precipitation createFromParcel(Parcel in) {
            return new Precipitation(in.readString(), in.readString());
        }

        public Precipitation[] newArray(int size) {
            return new Precipitation[size];
        }
    };
}
