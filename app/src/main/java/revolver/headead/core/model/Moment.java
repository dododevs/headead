package revolver.headead.core.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;
import revolver.headead.ui.fragments.record2.pickers.TimeInputMode;
import revolver.headead.util.misc.TimeFormattingUtils;

public class Moment extends RealmObject implements Parcelable {

    @SerializedName("date")
    private Date date;

    @SerializedName("partOfDay")
    private int partOfDay;

    @SerializedName("timeInputMode")
    private String timeInputMode;

    public Moment() {
    }

    public Moment(Date date, int partOfDay, TimeInputMode timeInputMode) {
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

    public int convertDateToPartOfDay() {
        return date != null ? TimeFormattingUtils.getPartOfDayFromDate(date) : -1;
    }

    public TimeInputMode getTimeInputMode() {
        return timeInputMode != null
                ? TimeInputMode.fromString(timeInputMode)
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

    public boolean after(Moment anotherMoment) {
        if (getTimeInputMode() == TimeInputMode.CLOCK &&
                anotherMoment.getTimeInputMode() == TimeInputMode.CLOCK) {
            return getDate().after(anotherMoment.getDate());
        } else {
            return getTimeInputMode() == TimeInputMode.PART_OF_DAY
                    ? partOfDay > anotherMoment.convertDateToPartOfDay()
                    : convertDateToPartOfDay() > anotherMoment.getPartOfDay();
        }
    }

    public boolean before(Moment anotherMoment) {
        if (getTimeInputMode() == TimeInputMode.CLOCK &&
                anotherMoment.getTimeInputMode() == TimeInputMode.CLOCK) {
            return getDate().before(anotherMoment.getDate());
        } else {
            return getTimeInputMode() == TimeInputMode.PART_OF_DAY
                    ? partOfDay < anotherMoment.convertDateToPartOfDay()
                    : convertDateToPartOfDay() < anotherMoment.getPartOfDay();
        }
    }

    public Moment withTime(Moment time) {
        if (getTimeInputMode() == TimeInputMode.PART_OF_DAY) {
            throw new IllegalStateException("Cannot join two time moments.");
        }
        if (time.getTimeInputMode() == TimeInputMode.CLOCK) {
            return new Moment(TimeFormattingUtils.joinDateAndTime(
                    getDate(), time.getDate()), -1, TimeInputMode.CLOCK);
        } else if (time.getTimeInputMode() == TimeInputMode.PART_OF_DAY) {
            return new Moment(getDate(), time.getPartOfDay(), TimeInputMode.PART_OF_DAY);
        }
        return this;
    }

    public Moment withDate(Moment date) {
        if (getTimeInputMode() == TimeInputMode.CLOCK) {
            return new Moment(TimeFormattingUtils.joinDateAndTime(
                    date.getDate(), getDate()), -1, TimeInputMode.CLOCK);
        } else if (getTimeInputMode() == TimeInputMode.PART_OF_DAY) {
            return new Moment(date.getDate(), getPartOfDay(), TimeInputMode.PART_OF_DAY);
        }
        return this;
    }

    public Moment withDate(Date date) {
        if (getTimeInputMode() == TimeInputMode.CLOCK) {
            return new Moment(TimeFormattingUtils.joinDateAndTime(
                    date, getDate()), -1, TimeInputMode.CLOCK);
        } else if (getTimeInputMode() == TimeInputMode.PART_OF_DAY) {
            return new Moment(date, getPartOfDay(), TimeInputMode.PART_OF_DAY);
        }
        return this;
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

    public static Moment now() {
        return new Moment(new Date(), -1, TimeInputMode.CLOCK);
    }
}
