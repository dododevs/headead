package revolver.headead.core.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SavedDrug extends RealmObject implements Parcelable {

    @PrimaryKey
    @SerializedName("packagingId")
    private String drugPackagingId;

    @SerializedName("intake")
    private DrugIntake drugIntake;

    public SavedDrug() {
    }

    public void setDrugPackagingId(String drugPackagingId) {
        this.drugPackagingId = drugPackagingId;
    }

    public void setDrugIntake(DrugIntake drugIntake) {
        this.drugIntake = drugIntake;
    }

    public String getDrugPackagingId() {
        return drugPackagingId;
    }

    public DrugIntake getDrugIntake() {
        return drugIntake;
    }

    private SavedDrug(Parcel src) {
        this.drugPackagingId = src.readString();
        this.drugIntake = src.readParcelable(DrugIntake.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(drugPackagingId);
        dest.writeParcelable(drugIntake, 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SavedDrug> CREATOR = new Creator<SavedDrug>() {
        @Override
        public SavedDrug createFromParcel(Parcel source) {
            return new SavedDrug(source);
        }

        @Override
        public SavedDrug[] newArray(int size) {
            return new SavedDrug[size];
        }
    };
}
