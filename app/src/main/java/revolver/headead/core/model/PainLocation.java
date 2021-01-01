package revolver.headead.core.model;

import androidx.annotation.StringRes;

import revolver.headead.R;

public enum PainLocation {
    SX(R.string.headache_location_sx_long, R.string.headache_location_sx_short),
    DX(R.string.headache_location_dx_long, R.string.headache_location_dx_short),
    BACK(R.string.headache_location_back_long, R.string.headache_location_back_short),
    BILATERAL(R.string.headache_location_bilateral_long, R.string.headache_location_bilateral_short);

    int longStringLabel, shortStringLabel;

    PainLocation(@StringRes int longString, @StringRes int shortString) {
        longStringLabel = longString;
        shortStringLabel = shortString;
    }

    public int getLongStringLabel() {
        return longStringLabel;
    }

    public int getShortStringLabel() {
        return shortStringLabel;
    }

    public static PainLocation fromString(final String name) {
        if (name == null) {
            return null;
        }
        for (final PainLocation painLocation : values()) {
            if (name.equals(painLocation.name())) {
                return painLocation;
            }
        }
        return null;
    }
}
