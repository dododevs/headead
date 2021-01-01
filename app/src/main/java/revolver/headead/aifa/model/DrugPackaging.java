package revolver.headead.aifa.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import revolver.headead.aifa.adapters.DrugArrayToStringAdapter;
import revolver.headead.aifa.adapters.DrugAuthorizationAdapter;

public class DrugPackaging extends RealmObject implements Parcelable {

    @SerializedName("sm_field_descrizione_farmaco")
    @JsonAdapter(DrugArrayToStringAdapter.class)
    private String drugDescription;

    @SerializedName("sm_field_descrizione_ditta")
    @JsonAdapter(DrugArrayToStringAdapter.class)
    private String drugMaker;

    @SerializedName("sm_field_descrizione_confezione")
    @JsonAdapter(DrugArrayToStringAdapter.class)
    private String packagingDescription;

    @SerializedName("sm_field_stato_farmaco")
    @JsonAdapter(DrugAuthorizationAdapter.class)
    private boolean isAuthorized;

    @SerializedName("sm_field_link_fi")
    private RealmList<String> brochureUrls;

    @SerializedName("sm_field_descrizione_atc")
    @JsonAdapter(DrugArrayToStringAdapter.class)
    private String activePrinciple;

    @SerializedName("sm_field_chiave_confezione")
    @JsonAdapter(DrugArrayToStringAdapter.class)
    private String drugPackagingId;

    public DrugPackaging() {
    }

    public void setDrugDescription(String drugDescription) {
        this.drugDescription = drugDescription;
    }

    public void setDrugMaker(String drugMaker) {
        this.drugMaker = drugMaker;
    }

    public void setPackagingDescription(String packagingDescription) {
        this.packagingDescription = packagingDescription;
    }

    public void setIsAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public void setBrochureUrls(RealmList<String> brochureUrls) {
        this.brochureUrls = brochureUrls;
    }

    public void setActivePrinciple(String activePrinciple) {
        this.activePrinciple = activePrinciple;
    }

    public void setDrugPackagingId(String drugPackagingId) {
        this.drugPackagingId = drugPackagingId;
    }

    public String getDrugDescription() {
        return drugDescription;
    }

    public String getDrugMaker() {
        return drugMaker;
    }

    public String getPackagingDescription() {
        return packagingDescription;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public RealmList<String> getBrochureUrls() {
        return brochureUrls;
    }

    public String getActivePrinciple() {
        return activePrinciple;
    }

    public String getDrugPackagingId() {
        return drugPackagingId;
    }

    private DrugPackaging(Parcel src) {
        this.drugDescription = src.readString();
        this.drugMaker = src.readString();
        this.packagingDescription = src.readString();
        this.isAuthorized = src.readInt() == 1;
        this.brochureUrls = new RealmList<>();
        src.readStringList(this.brochureUrls);
        this.activePrinciple = src.readString();
        this.drugPackagingId = src.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(drugDescription);
        dest.writeString(drugMaker);
        dest.writeString(packagingDescription);
        dest.writeInt(isAuthorized ? 1 : 0);
        dest.writeStringList(brochureUrls);
        dest.writeString(activePrinciple);
        dest.writeString(drugPackagingId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DrugPackaging> CREATOR = new Creator<DrugPackaging>() {
        @Override
        public DrugPackaging createFromParcel(Parcel source) {
            return new DrugPackaging(source);
        }

        @Override
        public DrugPackaging[] newArray(int size) {
            return new DrugPackaging[size];
        }
    };
}
