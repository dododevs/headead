package revolver.headead.core.model;

import androidx.annotation.DrawableRes;
import androidx.annotation.PluralsRes;

import revolver.headead.R;

public enum DrugDosageUnit {
    PILL(R.plurals.drug_dosage_unit_pill, R.drawable.ic_pill),
    SACHET(R.plurals.drug_dosage_unit_sachet, R.drawable.ic_sachet),
    WEIGHT(R.plurals.drug_dosage_unit_weight, R.drawable.ic_weight),
    VOLUME(R.plurals.drug_dosage_unit_volume, R.drawable.ic_liquid_volume),
    VIAL(R.plurals.drug_dosage_unit_vial, R.drawable.ic_vial),
    UNKNOWN(R.plurals.drug_dosage_unit_unknown, R.drawable.ic_dose);

    private final @PluralsRes int nameResource;
    private final @DrawableRes int iconResource;

    DrugDosageUnit(@PluralsRes int nameResource, @DrawableRes int iconResource) {
        this.nameResource = nameResource;
        this.iconResource = iconResource;
    }

    public int getNameResource() {
        return nameResource;
    }

    public int getIconResource() {
        return iconResource;
    }

    public static DrugDosageUnit fromString(final String name) {
        if (name == null) {
            return null;
        }
        for (final DrugDosageUnit drugDosageUnit : values()) {
            if (name.equals(drugDosageUnit.name())) {
                return drugDosageUnit;
            }
        }
        return null;
    }
}
