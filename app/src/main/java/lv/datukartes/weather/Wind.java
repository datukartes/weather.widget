package lv.datukartes.weather;

import android.os.Parcel;
import android.os.Parcelable;

public class Wind implements Parcelable {

    enum directions {
        NORTH,
        SOUTH,
        WEST,
        EAST,
        NORTH_WEST,
        NORTH_EAST,
        SOUTH_WEST,
        SOUTH_EAST
    }

    public String getSpeed() {
        return speed;
    }

    public Wind.directions getDirection() {
        return direction;
    }

    private String speed;
    private Wind.directions direction;

    public Wind(String speed, Wind.directions direction)
    {
        this.speed = speed;
        this.direction = direction;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getSpeed());
        dest.writeInt(this.getDirection().ordinal());
    }

    public static final Parcelable.Creator<Wind> CREATOR
            = new Parcelable.Creator<Wind>() {
        public Wind createFromParcel(Parcel in) {
            return new Wind(in.readString(), Wind.directions.values()[in.readInt()]);
        }

        public Wind[] newArray(int size) {
            return new Wind[size];
        }
    };
}
