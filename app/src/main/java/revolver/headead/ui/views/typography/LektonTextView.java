package revolver.headead.ui.views.typography;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import revolver.headead.App;
import revolver.headead.R;

public class LektonTextView extends AppCompatTextView {

    private static final int
            BOLD        = 1,
            REGULAR     = 5,
            ITALIC      = 8;

    public LektonTextView(Context context) {
        super(context);
    }

    public LektonTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeTypeface(attrs);
    }

    public LektonTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeTypeface(attrs);
    }

    private void initializeTypeface(AttributeSet attributeSet) {
        if (attributeSet == null) {
            setTypeface(App.Fonts.Lekton.Regular);
        } else {
            final TypedArray a = getContext().obtainStyledAttributes(attributeSet, R.styleable.LektonTextView);
            switch (a.getInt(R.styleable.LektonTextView_textStyle, REGULAR)) {
                case BOLD:
                    setTypeface(App.Fonts.Lekton.Bold);
                    break;
                case ITALIC:
                    setTypeface(App.Fonts.Lekton.Italic);
                    break;
                default:
                case REGULAR:
                    setTypeface(App.Fonts.Lekton.Regular);
                    break;
            }
            a.recycle();
        }
    }

}
