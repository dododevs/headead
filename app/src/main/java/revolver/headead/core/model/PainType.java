package revolver.headead.core.model;

import androidx.annotation.StringRes;

import revolver.headead.R;

public enum PainType {
    A(R.string.headache_type_a_long, R.string.headache_type_a_short),
    B(R.string.headache_type_b_long, R.string.headache_type_b_short),
    C(R.string.headache_type_c_long, R.string.headache_type_c_short);

    int longStringLabel, shortStringLabel;

    PainType(@StringRes int longString, @StringRes int shortString) {
        longStringLabel = longString;
        shortStringLabel = shortString;
    }

    public int getLongStringLabel() {
        return longStringLabel;
    }

    public int getShortStringLabel() {
        return shortStringLabel;
    }

    public static PainType fromString(final String name) {
        if (name == null) {
            return null;
        }
        for (final PainType painType: values()) {
            if (name.equals(painType.name())) {
                return painType;
            }
        }
        return null;
    }
}
