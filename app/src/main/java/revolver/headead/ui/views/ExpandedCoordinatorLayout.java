package revolver.headead.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import revolver.headead.R;
import revolver.headead.util.ui.M;
import revolver.headead.util.ui.ViewUtils;

public class ExpandedCoordinatorLayout extends CoordinatorLayout {

    private int topOffset;

    public ExpandedCoordinatorLayout(Context context) {
        super(context);
    }

    public ExpandedCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public ExpandedCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(final Context context, final AttributeSet attrs) {
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.ExpandedCoordinatorLayout);
        topOffset = a.getDimensionPixelSize(
                R.styleable.ExpandedCoordinatorLayout_expandedOffset, M.dp(56.f).intValue());
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(
                M.screenHeight() - topOffset - ViewUtils.getStatusBarHeight(),
                    MeasureSpec.EXACTLY));
    }
}
