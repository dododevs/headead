package revolver.headead.ui.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.google.android.material.slider.Slider;

import revolver.headead.util.ui.ColorUtils;

public class AnimatedSlider extends Slider {

    public AnimatedSlider(Context context) {
        super(context);
    }

    public AnimatedSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimatedSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setValue(float value) {
        float current = getValue();
        final ValueAnimator animator = ValueAnimator
                .ofFloat(current, value).setDuration(150L);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation ->
                super.setValue((Float) animation.getAnimatedValue()));
        animator.start();
    }
}
