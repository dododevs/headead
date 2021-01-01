package revolver.headead.ui.views.typography;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import revolver.headead.App;

public class RobotoTextView extends AppCompatTextView {

    public RobotoTextView(Context context) {
        super(context);
    }

    public RobotoTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RobotoTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(App.Fonts.Roboto.Regular);
    }

}
