package revolver.headead.util.ui;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.google.android.material.snackbar.Snackbar;

public class Snacks {

    public static void normal(View view, String text, boolean extendedText, String actionText, View.OnClickListener actionClickListener) {
        if (view != null) {
            final Snackbar s = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
            if (extendedText) {
                ((TextView) s.getView().findViewById(
                        com.google.android.material.R.id.snackbar_text)).setMaxLines(5);
            }
            if (actionText != null && actionClickListener != null) {
                s.setAction(actionText, actionClickListener).show();
            }
            s.show();
        } else {
            Log.w("Snacks", "Cancelled Snackbar on null view. (" + text + ")");
        }
    }

    public static void normal(View view, String text, boolean extendedText) {
        normal(view, text, extendedText, null, null);
    }

    public static void normal(View view, @StringRes int text, boolean extendedText) {
        if (view != null) {
            final String str = view.getResources().getString(text);
            normal(view, str, extendedText);
        } else {
            normal(null, null, extendedText);
        }
    }

    public static void shorter(View view, String text, boolean extendedText, String actionText, View.OnClickListener actionClickListener) {
        if (view != null) {
            final Snackbar s = Snackbar.make(view, text, Snackbar.LENGTH_SHORT);
            if (extendedText) {
                ((TextView) s.getView().findViewById(
                        com.google.android.material.R.id.snackbar_text)).setMaxLines(5);
            }
            if (actionText != null && actionClickListener != null) {
                s.setAction(actionText, actionClickListener).show();
            }
            s.show();
        } else {
            Log.w("Snacks", "Cancelled Snackbar on null view. (" + text + ")");
        }
    }

    public static void shorter(View view, String text, boolean extendedText) {
        shorter(view, text, extendedText, null, null);
    }

    public static void shorter(View view, @StringRes int text, boolean extendedText) {
        if (view != null) {
            final String str = view.getResources().getString(text);
            shorter(view, str, extendedText);
        } else {
            shorter(null, null, extendedText);
        }
    }

    public static void longer(View view, String text, boolean extendedText, String actionText, View.OnClickListener actionClickListener) {
        if (view != null) {
            final Snackbar s = Snackbar.make(view, text, Snackbar.LENGTH_INDEFINITE);
            if (extendedText) {
                ((TextView) s.getView().findViewById(
                        com.google.android.material.R.id.snackbar_text)).setMaxLines(5);
            }
            if (actionText != null && actionClickListener != null) {
                s.setAction(actionText, actionClickListener).show();
            }
            s.show();
        } else {
            Log.w("Snacks", "Cancelled Snackbar on null view. (" + text + ")");
        }
    }

    public static void longer(View view, String text, boolean extendedText) {
        longer(view, text, extendedText, null, null);
    }

    public static void longer(View view, @StringRes int text, boolean extendedText) {
        if (view != null) {
            final String str = view.getResources().getString(text);
            longer(view, str, extendedText);
        } else {
            longer(null, null, extendedText);
        }
    }

}
