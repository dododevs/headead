package revolver.headead.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

import revolver.headead.R;
import revolver.headead.util.ui.M;

public class BelowGrowingAppBarLayoutBehavior extends CoordinatorLayout.Behavior<View> {

    private CoordinatorLayout.LayoutParams thisChildLayoutParams;

    public BelowGrowingAppBarLayoutBehavior() {
        super();
    }

    public BelowGrowingAppBarLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        return dependency instanceof AppBarLayout && dependency.getId() == R.id.toolbar_container;
    }

    @Override
    public void onAttachedToLayoutParams(@NonNull CoordinatorLayout.LayoutParams params) {
        thisChildLayoutParams = params;
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull View child, int layoutDirection) {
        final AppBarLayout appBarLayout = parent.findViewById(R.id.toolbar_container);
        if (appBarLayout != null) {
            parent.onLayoutChild(child, layoutDirection);
            int topMarginToBeSet = appBarLayout.getBottom() + M.dp(4.f).intValue();
            thisChildLayoutParams.setMargins(
                    thisChildLayoutParams.leftMargin,
                    topMarginToBeSet,
                    thisChildLayoutParams.rightMargin,
                    thisChildLayoutParams.bottomMargin
            );
            return true;
        }
        return super.onLayoutChild(parent, child, layoutDirection);
    }
}
