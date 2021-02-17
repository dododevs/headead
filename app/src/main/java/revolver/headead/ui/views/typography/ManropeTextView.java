package revolver.headead.ui.views.typography;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import revolver.headead.App;
import revolver.headead.R;
import revolver.headead.util.ui.M;

public class ManropeTextView extends AppCompatTextView {

    private static final int
            BOLD        = 1,
            EXTRA_BOLD  = 2,
            EXTRA_LIGHT = 9,
            LIGHT       = 3,
            MEDIUM      = 4,
            REGULAR     = 5,
            SEMI_BOLD   = 6;

    public ManropeTextView(Context context) {
        super(context);
    }

    public ManropeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeTypeface(attrs);
    }

    public ManropeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeTypeface(attrs);
    }

    private void initializeTypeface(AttributeSet attributeSet) {
        if (attributeSet == null) {
            setTypeface(App.Fonts.Manrope.Regular);
        } else {
            final TypedArray a = getContext().obtainStyledAttributes(
                    attributeSet, R.styleable.ManropeTextView);
            switch (a.getInt(R.styleable.ManropeTextView_textStyle, REGULAR)) {
                case BOLD:
                    setTypeface(App.Fonts.Manrope.Bold);
                    break;
                case EXTRA_BOLD:
                    setTypeface(App.Fonts.Manrope.ExtraBold);
                    break;
                case EXTRA_LIGHT:
                    setTypeface(App.Fonts.Manrope.ExtraLight);
                    break;
                case LIGHT:
                    setTypeface(App.Fonts.Manrope.Light);
                    break;
                case MEDIUM:
                    setTypeface(App.Fonts.Manrope.Medium);
                    break;
                case REGULAR:
                    setTypeface(App.Fonts.Manrope.Regular);
                    break;
                case SEMI_BOLD:
                    setTypeface(App.Fonts.Manrope.SemiBold);
                    break;
            }
            a.recycle();
        }
        final int padding = M.dp(4.f).intValue();
        setPadding(0, padding, 0, padding);
    }

}
