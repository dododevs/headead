package revolver.headead.ui.fragments.record2.pickers;

public enum TimeInputMode {
    CLOCK, PART_OF_DAY;

    public static TimeInputMode fromString(final String name) {
        if (name == null) {
            return null;
        }
        for (final TimeInputMode timeInputMode : values()) {
            if (name.equals(timeInputMode.name())) {
                return timeInputMode;
            }
        }
        return null;
    }
}
