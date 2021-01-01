package revolver.headead.ui.views.typography;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import revolver.headead.App;
import revolver.headead.R;

public class MontserratTextView extends AppCompatTextView {

    private static final int
            BLACK       = 0,
            BOLD        = 1,
            EXTRA_BOLD  = 2,
            LIGHT       = 3,
            MEDIUM      = 4,
            REGULAR     = 5,
            SEMI_BOLD   = 6,
            THIN        = 7;

    public MontserratTextView(Context context) {
        super(context);
    }

    public MontserratTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeTypeface(attrs);
    }

    public MontserratTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeTypeface(attrs);
    }

    private void initializeTypeface(AttributeSet attributeSet) {
        if (attributeSet == null) {
            setTypeface(App.Fonts.Montserrat.Regular);
        } else {
            final TypedArray a = getContext().obtainStyledAttributes(attributeSet, R.styleable.MontserratTextView);
            switch (a.getInt(R.styleable.MontserratTextView_textStyle, REGULAR)) {
                case BLACK:
                    setTypeface(App.Fonts.Montserrat.Black);
                    break;
                case BOLD:
                    setTypeface(App.Fonts.Montserrat.Bold);
                    break;
                case EXTRA_BOLD:
                    setTypeface(App.Fonts.Montserrat.ExtraBold);
                    break;
                case LIGHT:
                    setTypeface(App.Fonts.Montserrat.Light);
                    break;
                case MEDIUM:
                    setTypeface(App.Fonts.Montserrat.Medium);
                    break;
                default:
                case REGULAR:
                    setTypeface(App.Fonts.Montserrat.Regular);
                    break;
                case SEMI_BOLD:
                    setTypeface(App.Fonts.Montserrat.SemiBold);
                    break;
                case THIN:
                    setTypeface(App.Fonts.Montserrat.Thin);
                    break;
            }
            a.recycle();
        }
    }

}
