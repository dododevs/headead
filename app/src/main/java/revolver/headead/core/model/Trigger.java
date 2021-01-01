package revolver.headead.core.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import io.realm.RealmObject;

public class Trigger extends RealmObject implements Parcelable {

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("iconResource")
    private @DrawableRes int iconResource;

    public Trigger() {
    }

    public Trigger(String name, String description, @DrawableRes int iconResource) {
        this.name = name;
        this.description = description;
        this.iconResource = iconResource;
    }

    private Trigger(Parcel src) {
        this.name = src.readString();
        this.description = src.readString();
        this.iconResource = src.readInt();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getIconResource() {
        return iconResource;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(iconResource);
    }

    public static final Creator<Trigger> CREATOR = new Creator<Trigger>() {
        @Override
        public Trigger createFromParcel(Parcel source) {
            return new Trigger(source);
        }

        @Override
        public Trigger[] newArray(int size) {
            return new Trigger[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trigger trigger = (Trigger) o;
        return iconResource == trigger.iconResource &&
                Objects.equals(name, trigger.name) &&
                Objects.equals(description, trigger.description);
    }

    @Override
    @NonNull
    public String toString() {
        return "Trigger{name='" + name + '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, iconResource);
    }
}
