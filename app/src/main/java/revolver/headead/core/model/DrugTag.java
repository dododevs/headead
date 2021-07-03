package revolver.headead.core.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.ColorInt;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class DrugTag extends RealmObject implements Parcelable {

    @SerializedName("tag")
    private String tag;

    @SerializedName("color")
    private @ColorInt int color;

    public DrugTag() {
    }

    public DrugTag(String tag, @ColorInt int color) {
        this.tag = tag;
        this.color = color;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTag() {
        return tag;
    }

    public int getColor() {
        return color;
    }

    private DrugTag(Parcel src) {
        this.tag = src.readString();
        this.color = src.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tag);
        dest.writeInt(color);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DrugTag> CREATOR = new Creator<DrugTag>() {
        @Override
        public DrugTag createFromParcel(Parcel source) {
            return new DrugTag(source);
        }

        @Override
        public DrugTag[] newArray(int size) {
            return new DrugTag[size];
        }
    };
}
