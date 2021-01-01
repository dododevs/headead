package revolver.headead.util.ui;

import android.text.Editable;
import android.text.TextWatcher;

public final class TextUtils {

    public static String capitalizeWords(final String s) {
        final StringBuilder sb = new StringBuilder();
        final char[] chars = s.toCharArray();
        boolean shouldCapitalize = true;
        for (char c : chars) {
            if (shouldCapitalize && Character.isLetterOrDigit(c)) {
                c = Character.toUpperCase(c);
                shouldCapitalize = false;
            } else {
                c = Character.toLowerCase(c);
            }
            if (Character.isWhitespace(c)) {
                shouldCapitalize = true;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static String capitalizeFirst(final String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
