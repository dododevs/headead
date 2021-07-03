package revolver.headead.core.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;
import revolver.headead.aifa.model.DrugPackaging;

public class DrugIntake extends RealmObject implements Parcelable {

    @SerializedName("drugPackaging")
    private DrugPackaging drugPackaging;

    @SerializedName("quantity")
    private double quantity;

    @SerializedName("unit")
    private String unit;

    @SerializedName("date")
    private Date intakeDate;

    @SerializedName("comment")
    private String comment;

    @SerializedName("tag")
    private DrugTag tag;

    public DrugIntake() {
    }

    public DrugIntake(final DrugPackaging drugPackaging, int quantity, String unit, Date intakeDate, String comment, DrugTag tag) {
        this.drugPackaging = drugPackaging;
        this.quantity = quantity;
        this.unit = unit;
        this.intakeDate = intakeDate;
        this.comment = comment;
        this.tag = tag;
    }

    private DrugIntake(Parcel src) {
        this.drugPackaging = src.readParcelable(DrugPackaging.class.getClassLoader());
        this.quantity = src.readDouble();
        this.unit = src.readString();
        this.intakeDate = (Date) src.readSerializable();
        this.comment = src.readString();
        this.tag = src.readParcelable(DrugTag.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(drugPackaging, 0);
        dest.writeDouble(quantity);
        dest.writeString(unit);
        dest.writeSerializable(intakeDate);
        dest.writeString(comment);
        dest.writeParcelable(tag, 0);
    }

    public void setDrugPackaging(DrugPackaging drugPackaging) {
        this.drugPackaging = drugPackaging;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setIntakeDate(Date intakeDate) {
        this.intakeDate = intakeDate;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setTag(DrugTag tag) {
        this.tag = tag;
    }

    public DrugPackaging getDrugPackaging() {
        return drugPackaging;
    }

    public double getQuantity() {
        return quantity;
    }

    public DrugDosageUnit getUnit() {
        return DrugDosageUnit.fromString(unit);
    }

    public Date getIntakeDate() {
        return intakeDate;
    }

    public String getComment() {
        return comment;
    }

    public DrugTag getTag() {
        return tag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DrugIntake> CREATOR = new Creator<DrugIntake>() {
        @Override
        public DrugIntake createFromParcel(Parcel source) {
            return new DrugIntake(source);
        }

        @Override
        public DrugIntake[] newArray(int size) {
            return new DrugIntake[size];
        }
    };
}
