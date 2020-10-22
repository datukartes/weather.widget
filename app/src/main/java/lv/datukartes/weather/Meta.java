package lv.datukartes.weather;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Meta implements Parcelable{
    private String iconUrl;
    private String city;

    public String getIconUrl() {
        return iconUrl;
    }

    public String getCity() {
        return city;
    }

    String date;
    public Meta(String date, String iconUrl, String city) {
        this.date =  date;
        this.iconUrl = iconUrl;
        this.city = city;
    }

    public Date getDate() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        return simpleDateFormat.parse(this.date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeString(this.getIconUrl());
        dest.writeString(this.getCity());
    }

    public static final Parcelable.Creator<Meta> CREATOR
            = new Parcelable.Creator<Meta>() {
        public Meta createFromParcel(Parcel in) {
            return new Meta(in.readString(), in.readString(), in.readString());
        }

        public Meta[] newArray(int size) {
            return new Meta[size];
        }
    };
}
