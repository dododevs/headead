package revolver.headead.core.model;

import android.location.Location;
import android.location.LocationManager;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import revolver.headead.core.display.ListItem;
import revolver.headead.ui.fragments.record2.pickers.DateTimePickerFragment;

public class Headache extends RealmObject implements Parcelable, ListItem {

    @PrimaryKey
    @SerializedName("uuid")
    private String uuid;

    /*@SerializedName("startDate")
    private Date startDate;

    @SerializedName("endDate")
    private Date endDate;

    @SerializedName("startDateTimeMode")
    private String startDateTimeMode;

    @SerializedName("endDateTimeMode")
    private String endDateTimeMode;*/

    @SerializedName("startMoment")
    private Moment startMoment;

    @SerializedName("endMoment")
    private Moment endMoment;

    @SerializedName("painLocation")
    private RealmList<String> painLocations;

    @SerializedName("painIntensity")
    private int painIntensity;

    @SerializedName("painType")
    private RealmList<String> painTypes;

    @SerializedName("locationLatitude")
    private double latitude = Double.MAX_VALUE;

    @SerializedName("locationLongitude")
    private double longitude = Double.MAX_VALUE;

    @SerializedName("triggers")
    private RealmList<Trigger> selectedTriggers;

    @SerializedName("takenDrugs")
    private RealmList<DrugIntake> drugIntakes;

    @SerializedName("aura")
    private boolean isAuraPresent;

    @SerializedName("movement")
    private boolean getsWorseWithMovement;

    public Headache() {
        this.uuid = UUID.randomUUID().toString();
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setPainIntensity(int painIntensity) {
        this.painIntensity = painIntensity;
    }

    public void setPainLocations(RealmList<String> painLocations) {
        this.painLocations = painLocations;
    }

    public void setPainLocation(List<PainLocation> painLocations) {
        this.painLocations = new RealmList<>();
        for (final PainLocation painLocation : painLocations) {
            this.painLocations.add(painLocation.toString());
        }
    }

    public void setPainType(List<PainType> painTypes) {
        this.painTypes = new RealmList<>();
        for (final PainType painType : painTypes) {
            this.painTypes.add(painType.toString());
        }
    }

    /*public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }*/

    public void setStartMoment(Moment startMoment) {
        this.startMoment = startMoment;
    }

    public void setEndMoment(Moment endMoment) {
        this.endMoment = endMoment;
    }

    /*public void setStartDateTimeMode(String dateTimeMode) {
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
    }*/

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

    public void setIsAuraPresent(boolean auraPresent) {
        this.isAuraPresent = auraPresent;
    }

    public void setGetsWorseWithMovement(boolean getsWorseWithMovement) {
        this.getsWorseWithMovement = getsWorseWithMovement;
    }

    public int getPainIntensity() {
        return this.painIntensity;
    }

    public List<PainLocation> getPainLocations() {
        final List<PainLocation> painLocations = new ArrayList<>();
        for (final String painLocation : this.painLocations) {
            painLocations.add(PainLocation.valueOf(painLocation));
        }
        return painLocations;
    }

    public List<PainType> getPainTypes() {
        final List<PainType> painTypes = new ArrayList<>();
        for (final String painType : this.painTypes) {
            painTypes.add(PainType.valueOf(painType));
        }
        return painTypes;
    }

    public Moment getStartMoment() {
        return startMoment;
    }

    public Moment getEndMoment() {
        return endMoment;
    }

    public String getUuid() {
        return uuid;
    }

    /*public DateTimePickerFragment.DateTimeMode getStartDateTimeMode() {
        return DateTimePickerFragment.DateTimeMode.fromString(startDateTimeMode);
    }

    public DateTimePickerFragment.DateTimeMode getEndDateTimeMode() {
        return DateTimePickerFragment.DateTimeMode.fromString(endDateTimeMode);
    }*/

    public Location getLocation() {
        if (latitude < -180.f || latitude > 180.f || longitude < -180.f || longitude > 180.f) {
            return null;
        }
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

    public boolean isAuraPresent() {
        return isAuraPresent;
    }

    public boolean getsWorseWithMovement() {
        return getsWorseWithMovement;
    }

    private Headache(Parcel src) {
        this.uuid = src.readString();
        /*this.startDate = (Date) src.readSerializable();
        this.endDate = (Date) src.readSerializable();
        this.startDateTimeMode = src.readString();
        this.endDateTimeMode = src.readString();*/
        this.startMoment = src.readParcelable(Moment.class.getClassLoader());
        this.endMoment = src.readParcelable(Moment.class.getClassLoader());
        this.painLocations = new RealmList<>();
        src.readStringList(this.painLocations);
        this.painIntensity = src.readInt();
        this.painTypes = new RealmList<>();
        src.readStringList(this.painTypes);
        this.latitude = src.readDouble();
        this.longitude = src.readDouble();
        this.selectedTriggers = new RealmList<>();
        src.readTypedList(this.selectedTriggers, Trigger.CREATOR);
        this.drugIntakes = new RealmList<>();
        src.readTypedList(this.drugIntakes, DrugIntake.CREATOR);
        this.isAuraPresent = src.readInt() == 1;
        this.getsWorseWithMovement = src.readInt() == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        /*dest.writeSerializable(startDate);
        dest.writeSerializable(endDate);*/
        dest.writeParcelable(startMoment, 0);
        dest.writeParcelable(endMoment, 0);
        dest.writeStringList(painLocations);
        dest.writeInt(painIntensity);
        dest.writeStringList(painTypes);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeTypedList(this.selectedTriggers);
        dest.writeTypedList(this.drugIntakes);
        dest.writeInt(isAuraPresent ? 1 : 0);
        dest.writeInt(getsWorseWithMovement ? 1 : 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Headache{" +
                "uuid='" + uuid + '\'' +
                ", startMoment=" + startMoment +
                ", endMoment=" + endMoment +
                ", painLocations=" + painLocations +
                ", painIntensity=" + painIntensity +
                ", painTypes=" + painTypes +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", selectedTriggers=" + selectedTriggers +
                ", drugIntakes=" + drugIntakes +
                ", isAuraPresent=" + isAuraPresent +
                ", getsWorseWithMovement=" + getsWorseWithMovement +
                '}';
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
