package revolver.headead.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import revolver.headead.util.ui.M;
import revolver.headead.util.ui.ViewUtils;

public class ExpandedCoordinatorLayout extends CoordinatorLayout {

    public ExpandedCoordinatorLayout(Context context) {
        super(context);
    }

    public ExpandedCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandedCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(
                M.screenHeight() - M.dp(56.f).intValue() - ViewUtils.getStatusBarHeight(),
                    MeasureSpec.EXACTLY));
    }
}
