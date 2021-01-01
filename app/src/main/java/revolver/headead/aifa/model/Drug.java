package revolver.headead.aifa.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import revolver.headead.aifa.adapters.DrugArrayToStringAdapter;

public class Drug extends RealmObject implements Parcelable {

    @SerializedName("sm_field_codice_farmaco")
    @JsonAdapter(DrugArrayToStringAdapter.class)
    private String drugId;

    @SerializedName("sm_field_descrizione_farmaco")
    @JsonAdapter(DrugArrayToStringAdapter.class)
    private String drugDescription;

    @SerializedName("sm_field_descrizione_ditta")
    @JsonAdapter(DrugArrayToStringAdapter.class)
    private String drugMaker;

    public Drug() {
    }

    public void setDrugId(String drugId) {
        this.drugId = drugId;
    }

    public void setDrugDescription(String drugDescription) {
        this.drugDescription = drugDescription;
    }

    public void setDrugMaker(String drugMaker) {
        this.drugMaker = drugMaker;
    }

    public String getDrugId() {
        return drugId;
    }

    public String getDrugDescription() {
        return drugDescription;
    }

    public String getDrugMaker() {
        return drugMaker;
    }

    private Drug(Parcel src) {
        this.drugId = src.readString();
        this.drugDescription = src.readString();
        this.drugMaker = src.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(drugId);
        dest.writeString(drugDescription);
        dest.writeString(drugMaker);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Drug> CREATOR = new Creator<Drug>() {
        @Override
        public Drug createFromParcel(Parcel source) {
            return new Drug(source);
        }

        @Override
        public Drug[] newArray(int size) {
            return new Drug[size];
        }
    };
}
