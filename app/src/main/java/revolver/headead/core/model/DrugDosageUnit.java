package revolver.headead.core.model;

import android.content.Context;

import androidx.annotation.DrawableRes;
import androidx.annotation.PluralsRes;
import androidx.annotation.StringRes;

import java.util.Locale;

import revolver.headead.R;

public enum DrugDosageUnit {
    PILL(R.plurals.drug_dosage_unit_pill, R.drawable.ic_pill, new int[] {
            R.plurals.drug_dosage_unit_pill_alt1
    }),
    SACHET(R.plurals.drug_dosage_unit_sachet, R.drawable.ic_sachet, new int[] {}),
    WEIGHT(R.plurals.drug_dosage_unit_weight, R.drawable.ic_weight, new int[] {
            R.plurals.drug_dosage_unit_weight_alt1
    }),
    VOLUME(R.plurals.drug_dosage_unit_volume, R.drawable.ic_liquid_volume, new int[] {
            R.plurals.drug_dosage_unit_volume_alt1
    }),
    VIAL(R.plurals.drug_dosage_unit_vial, R.drawable.ic_vial, new int[] {}),
    DROP(R.plurals.drug_dosage_unit_drop, R.drawable.ic_drop, new int[] {}),
    SPOON(R.plurals.drug_dosage_unit_spoon, R.drawable.ic_spoon, new int[] {
            R.plurals.drug_dosage_unit_spoon_alt1, R.plurals.drug_dosage_unit_spoon_alt2
    }),
    UNKNOWN(R.plurals.drug_dosage_unit_unknown, R.drawable.ic_dose, new int[] {});

    private final @PluralsRes int nameResource;
    private final @DrawableRes int iconResource;
    private final @StringRes int[] alternativeNames;

    DrugDosageUnit(@PluralsRes int nameResource, @DrawableRes int iconResource, @PluralsRes int[] alternativeNames) {
        this.nameResource = nameResource;
        this.iconResource = iconResource;
        this.alternativeNames = alternativeNames;
    }

    public int getNameResource() {
        return nameResource;
    }

    public int getIconResource() {
        return iconResource;
    }

    public int[] getAlternativeNames() {
        return alternativeNames;
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

    private static boolean containsUnitName(Context context, String description, DrugDosageUnit unit) {
        if (description.toLowerCase().contains(String.format(Locale.getDefault(),
                " %s ", context.getResources().getQuantityString(
                        unit.getNameResource(), 2).toLowerCase())) ||
                description.toLowerCase().contains(String.format(Locale.getDefault(), " %s ",
                        context.getResources().getQuantityString(
                                unit.getNameResource(), 1).toLowerCase()))) {
            return true;
        }
        for (final int alternativeName : unit.getAlternativeNames()) {
            if (description.toLowerCase().contains(String.format(Locale.getDefault(),
                    " %s ", context.getResources().getQuantityString(alternativeName, 2).toLowerCase())) ||
                    description.toLowerCase().contains(String.format(Locale.getDefault(), " %s ",
                            context.getResources().getQuantityString(alternativeName, 1).toLowerCase()))) {
                return true;
            }
        }
        return false;
    }

    public static DrugDosageUnit guessProperUnit(final Context context, final String drugPackagingDescription) {
        if (containsUnitName(context, drugPackagingDescription, PILL)) {
            return PILL;
        } else if (containsUnitName(context, drugPackagingDescription, SACHET)) {
            return SACHET;
        } else if (containsUnitName(context, drugPackagingDescription, VIAL)) {
            return VIAL;
        } else if (containsUnitName(context, drugPackagingDescription, DROP)) {
            return DROP;
        } else if (containsUnitName(context, drugPackagingDescription, SPOON)) {
            return SPOON;
        } else if (containsUnitName(context, drugPackagingDescription, VOLUME)) {
            return VOLUME;
        } else if (containsUnitName(context, drugPackagingDescription, WEIGHT)) {
            return WEIGHT;
        }
        return UNKNOWN;
    }
}
