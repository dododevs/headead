package revolver.headead.core.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;
import revolver.headead.ui.fragments.record2.pickers.DateTimePickerFragment;

public class Moment extends RealmObject implements Parcelable {

    @SerializedName("date")
    private Date date;

    @SerializedName("partOfDay")
    private int partOfDay;

    @SerializedName("timeInputMode")
    private String timeInputMode;

    public Moment() {
    }

    public Moment(Date date, int partOfDay, DateTimePickerFragment.TimeInputMode timeInputMode) {
        this.date = date;
        this.partOfDay = partOfDay;
        this.timeInputMode = timeInputMode != null ? timeInputMode.name() : null;
    }

    public Date getDate() {
        return date;
    }

    public int getPartOfDay() {
        return partOfDay;
    }

    public DateTimePickerFragment.TimeInputMode getTimeInputMode() {
        return timeInputMode != null
                ? DateTimePickerFragment.TimeInputMode.fromString(timeInputMode)
                : null;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setPartOfDay(int partOfDay) {
        this.partOfDay = partOfDay;
    }

    public void setTimeInputMode(String timeInputMode) {
        this.timeInputMode = timeInputMode;
    }

    private Moment(Parcel src) {
        this.date = (Date) src.readSerializable();
        this.partOfDay = src.readInt();
        this.timeInputMode = src.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(date);
        dest.writeInt(partOfDay);
        dest.writeString(timeInputMode);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Moment> CREATOR = new Creator<Moment>() {
        @Override
        public Moment createFromParcel(Parcel source) {
            return new Moment(source);
        }

        @Override
        public Moment[] newArray(int size) {
            return new Moment[size];
        }
    };
}
