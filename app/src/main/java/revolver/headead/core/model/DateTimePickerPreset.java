package revolver.headead.core.model;

public enum DateTimePickerPreset {
    JUST_STARTED, JUST_ENDED, PAST, CUSTOM_DAY_OFFSET;

    public static DateTimePickerPreset fromString(String name) {
        if (name == null) {
            return null;
        }
        for (DateTimePickerPreset pickerPreset : values()) {
            if (name.equals(pickerPreset.name())) {
                return pickerPreset;
            }
        }
        return null;
    }
}
