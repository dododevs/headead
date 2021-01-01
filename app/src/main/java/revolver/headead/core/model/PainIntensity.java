package revolver.headead.core.model;

import androidx.annotation.StringRes;

import revolver.headead.R;

public enum PainIntensity {
    LOW(R.string.headache_intensity_low_long, R.string.headache_intensity_low_short),
    MEDIUM(R.string.headache_intensity_medium_long, R.string.headache_intensity_medium_short),
    HIGH(R.string.headache_intensity_high_long, R.string.headache_intensity_high_short);

    int longStringLabel, shortStringLabel;

    PainIntensity(@StringRes int longString, @StringRes int shortString) {
        longStringLabel = longString;
        shortStringLabel = shortString;
    }

    public int getLongStringLabel() {
        return longStringLabel;
    }

    public int getShortStringLabel() {
        return shortStringLabel;
    }

    public static PainIntensity fromString(final String name) {
        if (name == null) {
            return null;
        }
        for (final PainIntensity painIntensity : values()) {
            if (name.equals(painIntensity.name())) {
                return painIntensity;
            }
        }
        return null;
    }
}
