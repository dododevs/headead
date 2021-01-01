package revolver.headead.core.model;

import android.location.Location;
import android.location.LocationManager;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import revolver.headead.ui.fragments.record2.pickers.DateTimePickerFragment;

public class Headache extends RealmObject implements Parcelable {

    @SerializedName("startDate")
    private Date startDate;

    @SerializedName("endDate")
    private Date endDate;

    @SerializedName("startDateTimeMode")
    private String startDateTimeMode;

    @SerializedName("endDateTimeMode")
    private String endDateTimeMode;

    @SerializedName("painLocation")
    private String painLocation;

    @SerializedName("painIntensity")
    private String painIntensity;

    @SerializedName("painType")
    private String painType;

    @SerializedName("locationLatitude")
    private double latitude;

    @SerializedName("locationLongitude")
    private double longitude;

    @SerializedName("triggers")
    private RealmList<Trigger> selectedTriggers;

    @SerializedName("takenDrugs")
    private RealmList<DrugIntake> drugIntakes;

    public Headache() {
    }

    public void setPainIntensity(String painIntensity) {
        this.painIntensity = painIntensity;
    }

    public void setPainIntensity(PainIntensity painIntensity) {
        this.painIntensity = painIntensity.toString();
    }

    public void setPainLocation(String painLocation) {
        this.painLocation = painLocation;
    }

    public void setPainLocation(PainLocation painLocation) {
        this.painLocation = painLocation.toString();
    }

    public void setPainType(String painType) {
        this.painType = painType;
    }

    public void setPainType(PainType painType) {
        this.painType = painType.toString();
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setStartDateTimeMode(String dateTimeMode) {
        this.startDateTimeMode = dateTimeMode;
    }

    public void setStartDateTimeMode(DateTimePickerFragment.DateTimeMode dateTimeMode) {
        this.startDateTimeMode = dateTimeMode.toString();
    }

    public void setEndDateTimeMode(String dateTimeMode) {
        this.endDateTimeMode = dateTimeMode;
    }

    public void setEndDateTimeMode(DateTimePickerFragment.DateTimeMode dateTimeMode) {
        this.endDateTimeMode = dateTimeMode.toString();
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setSelectedTriggers(RealmList<Trigger> selectedTriggers) {
        this.selectedTriggers = selectedTriggers;
    }

    public void setDrugIntakes(RealmList<DrugIntake> drugIntakes) {
        this.drugIntakes = drugIntakes;
    }

    public PainIntensity getPainIntensity() {
        return PainIntensity.valueOf(this.painIntensity);
    }

    public PainLocation getPainLocation() {
        return PainLocation.valueOf(this.painLocation);
    }

    public PainType getPainType() {
        return PainType.fromString(this.painType);
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public DateTimePickerFragment.DateTimeMode getStartDateTimeMode() {
        return DateTimePickerFragment.DateTimeMode.fromString(startDateTimeMode);
    }

    public DateTimePickerFragment.DateTimeMode getEndDateTimeMode() {
        return DateTimePickerFragment.DateTimeMode.fromString(endDateTimeMode);
    }

    public Location getLocation() {
        final Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(this.latitude);
        location.setLongitude(this.longitude);
        return location;
    }

    public RealmList<Trigger> getSelectedTriggers() {
        return selectedTriggers;
    }

    public RealmList<DrugIntake> getDrugIntakes() {
        return drugIntakes;
    }

    private Headache(Parcel src) {
        this.startDate = (Date) src.readSerializable();
        this.endDate = (Date) src.readSerializable();
        this.startDateTimeMode = src.readString();
        this.endDateTimeMode = src.readString();
        this.painLocation = src.readString();
        this.painIntensity = src.readString();
        this.painType = src.readString();
        this.latitude = src.readDouble();
        this.longitude = src.readDouble();
        this.selectedTriggers = new RealmList<>();
        src.readTypedList(this.selectedTriggers, Trigger.CREATOR);
        this.drugIntakes = new RealmList<>();
        src.readTypedList(this.drugIntakes, DrugIntake.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(startDate);
        dest.writeSerializable(endDate);
        dest.writeString(painLocation);
        dest.writeString(painIntensity);
        dest.writeString(painType);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeTypedList(this.selectedTriggers);
        dest.writeTypedList(this.drugIntakes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Headache> CREATOR = new Creator<Headache>() {
        @Override
        public Headache createFromParcel(Parcel source) {
            return new Headache(source);
        }

        @Override
        public Headache[] newArray(int size) {
            return new Headache[size];
        }
    };
}
